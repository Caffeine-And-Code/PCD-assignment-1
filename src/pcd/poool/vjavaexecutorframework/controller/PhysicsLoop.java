package pcd.poool.vjavaexecutorframework.controller;

import pcd.poool.vjavaexecutorframework.model.Ball;
import pcd.poool.vjavaexecutorframework.model.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class PhysicsLoop implements Runnable {
    private final Scene scene;
    private final Consumer<Throwable> onTermination;
    private final ExecutorService executor;
    private final int workerCount;

    public PhysicsLoop(Scene scene, int workerCount, ExecutorService executor, Consumer<Throwable> onTermination) {
        if (workerCount <= 0) throw new IllegalArgumentException("workerCount must be positive");
        this.scene = scene;
        this.onTermination = onTermination;
        this.workerCount = workerCount;
        this.executor = executor;
    }

    @Override
    public void run() {
        Throwable failure = null;
        try {
            if (Thread.currentThread().isInterrupted()) return;
            long frames = 0;
            long startTime = System.nanoTime();
            long previous = startTime;
            while (!Thread.currentThread().isInterrupted() && !scene.isStopped()) {
                long now = System.nanoTime();
                long dt = (now - previous) / 1_000_000;
                previous += dt * 1_000_000;
                long duration = now - startTime;
                int fps = duration > 0 ? (int) (++frames * 1_000_000_000L / duration) : 0;
                updateState(dt, fps);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (Throwable ex) {
            failure = ex;
            throw ex;
        } finally {
            onTermination.accept(failure);
        }
    }

    protected void updateState(long dt, int fps) throws InterruptedException {
        scene.updateBoardPhysics(board -> {
            var balls = board.getBalls();
            balls.add(board.getPlayerBall());
            balls.add(board.getBotBall());
            var bounds = board.getBounds();
            List<List<Integer>> collisions = new ArrayList<>();
            for (int i = 0; i < balls.size(); i++) collisions.add(new ArrayList<>());
            List<Callable<Void>> movementTasks = new ArrayList<>();
            List<Callable<Void>> collisionTasks = new ArrayList<>();
            for (int id = 0; id < workerCount; id++) {
                final int workerId = id;
                movementTasks.add(() -> {
                    for (int i = workerId; i < balls.size(); i += workerCount) {
                        balls.get(i).updateState(dt, bounds);
                    }
                    return null;
                });
                collisionTasks.add(() -> {
                    for (int i = workerId; i < balls.size(); i += workerCount) {
                        Ball a = balls.get(i);
                        for (int j = i + 1; j < balls.size(); j++) {
                            Ball b = balls.get(j);
                            if (Ball.checkCollision(a, b)) collisions.get(i).add(j);
                        }
                    }
                    return null;
                });
            }
            executeAndGet(movementTasks);
            executeAndGet(collisionTasks);
            for (int i = 0; i < balls.size(); i++) {
                for (int j : collisions.get(i)) {
                    Ball a = balls.get(i);
                    Ball b = balls.get(j);
                    if (Ball.resolveCollision(a, b)) rememberPlayerHit(a, b);
                }
            }
            board.setBalls(balls.subList(0, balls.size() - 2));
            board.setPlayerBall(balls.get(balls.size() - 2).copy());
            board.setBotBall(balls.get(balls.size() - 1).copy());
            return fps;
        });
    }

    private void executeAndGet(List<Callable<Void>> tasks) throws InterruptedException {
        var results = executor.invokeAll(tasks);
        for (var result : results) {
            try {
                result.get();
            } catch (ExecutionException ex) {
                throw new IllegalStateException("Physics worker failed", ex.getCause());
            }
        }
    }

    private void rememberPlayerHit(Ball i, Ball j) {
        i.hitBy(j.getType());
        j.hitBy(i.getType());
    }
}
