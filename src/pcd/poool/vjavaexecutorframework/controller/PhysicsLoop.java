package pcd.poool.vjavaexecutorframework.controller;

import pcd.poool.vjavaexecutorframework.model.Ball;
import pcd.poool.vjavaexecutorframework.model.Boundary;
import pcd.poool.vjavaexecutorframework.model.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import java.util.function.Consumer;

public class PhysicsLoop extends Thread {
    private final Scene scene;
    private final Consumer<Throwable> onTermination;
    private final Thread[] workers;
    private final Semaphore[] start;
    private final Semaphore finished = new Semaphore(0);
    private final CyclicBarrier movementDone;
    private long elapsed;
    private List<Ball> balls;
    private List<List<Integer>> collisions;
    private Boundary bounds;
    // Ogni worker scrive solo nella propria posizione se ha avuto un eccezione
    private Throwable[] failures;

    public PhysicsLoop(Scene scene, int workerCount, Consumer<Throwable> onTermination) {
        if (workerCount <= 0) throw new IllegalArgumentException("workerCount must be positive");
        this.scene = scene;
        this.onTermination = onTermination;
        workers = new Thread[workerCount];
        start = new Semaphore[workerCount];
        movementDone = new CyclicBarrier(workerCount);
        for (int i = 0; i < workerCount; i++) {
            int id = i;
            start[i] = new Semaphore(0);
            workers[i] = new Thread(() -> work(id));
        }
    }

    @Override
    public void run() {
        Throwable failure = null;
        try {
            if (isInterrupted()) return;
            for (Thread worker : workers) worker.start();
            long frames = 0;
            long startTime = System.nanoTime();
            long previous = startTime;
            while (!isInterrupted() && !scene.isStopped()) {
                long now = System.nanoTime();
                long dt = (now - previous) / 1_000_000;
                previous += dt * 1_000_000;
                long duration = now - startTime;
                int fps = duration > 0 ? (int) (++frames * 1_000_000_000L / duration) : 0;
                updateState(dt, fps);
            }
        } catch (InterruptedException ex) {
            interrupt();
        } catch (Throwable ex) {
            failure = ex;
            throw ex;
        } finally {
            try {
                onTermination.accept(failure);
            } finally {
                stopWorkers();
            }
        }
    }

    private void stopWorkers() {
        for (Thread worker : workers) worker.interrupt();
        boolean interrupted = Thread.interrupted();
        for (Thread worker : workers) {
            while (worker.isAlive()) {
                try { worker.join(); }
                catch (InterruptedException ex) { interrupted = true; }
            }
        }
        if (interrupted) interrupt();
    }

    protected void updateState(long dt, int fps) throws InterruptedException {
        scene.updateBoardPhysics(board -> {
            var tickBalls = board.getBalls();
            tickBalls.add(board.getPlayerBall());
            tickBalls.add(board.getBotBall());
            balls = tickBalls;
            bounds = board.getBounds();
            elapsed = dt;
            collisions = new ArrayList<>();
            for (int i = 0; i < balls.size(); i++) collisions.add(new ArrayList<>());
            failures = new Throwable[workers.length];
            for (Semaphore signal : start) signal.release();
            // Aspetto che tutti i worker abbiano finito usando un semaforo.
            try {
                finished.acquire(workers.length);
            } catch (InterruptedException ex) {
                // Finish cancellation before releasing Scene's lock and tick data.
                stopWorkers();
                throw ex;
            }
            // se un worker è andato in eccezione allora termino il thread della fisica
            for (Throwable failure : failures) {
                if (failure != null) throw new IllegalStateException("Physics worker failed", failure);
            }
            for (int i = 0; i < balls.size(); i++) {
                for (int j : collisions.get(i)) {
                    Ball a = balls.get(i);
                    Ball b = balls.get(j);

                    if (Ball.resolveCollision(a, b)) {
                        rememberPlayerHit(a, b);
                    }
                }
            }
            board.setBalls(tickBalls.subList(0, tickBalls.size() - 2));
            board.setPlayerBall(tickBalls.get(tickBalls.size() - 2).copy());
            board.setBotBall(tickBalls.get(tickBalls.size() - 1).copy());
            return fps;
        });
    }

    private void rememberPlayerHit(Ball i, Ball j) {
        i.hitBy(j.getType());
        j.hitBy(i.getType());
    }

    private void work(int id) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                start[id].acquire();
                try {
                    try{
                        for (int i = id; i < balls.size(); i += workers.length) {
                            balls.get(i).updateState(elapsed, bounds);
                        }
                    } catch (Throwable ex) {
                        failures[id] = ex;
                    }
                    // Aspetto che tutti i worker finiscano la prima fase
                    movementDone.await();
                    if (failures[id] == null) {
                        // Mi salvo una lista con tutte le collisioni così poi applico solo quelle
                        for (int i = id; i < balls.size(); i += workers.length) {
                            Ball a = balls.get(i);
                            for (int j = i + 1; j < balls.size(); j++) {
                                Ball b = balls.get(j);
                                if (Ball.checkCollision(a, b)) {
                                    collisions.get(i).add(j);
                                }
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Throwable ex) {
                    if (failures[id] == null) failures[id] = ex;
                } finally {
                    finished.release();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
