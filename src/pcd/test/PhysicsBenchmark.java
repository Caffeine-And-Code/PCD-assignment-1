package pcd.test;

import pcd.poool.vnativethreads.model.Ball;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

final class PhysicsBenchmark {
    private static final int STEP_MILLIS = 16;
    private static final long STOP_TIMEOUT_SECONDS = 30;

    private final int warmupSteps;
    private final int measuredSteps;

    PhysicsBenchmark(int warmupSteps, int measuredSteps) {
        this.warmupSteps = warmupSteps;
        this.measuredSteps = measuredSteps;
    }

    long sequential() throws InterruptedException {
        var scene = new pcd.poool.vnativethreads.model.Scene();
        scene.initBoard(new pcd.poool.vnativethreads.model.MassiveBoardConf());
        for (int i = 0; i < warmupSteps; i++) sequentialStep(scene);

        long start = System.nanoTime();
        for (int i = 0; i < measuredSteps; i++) sequentialStep(scene);
        return System.nanoTime() - start;
    }

    private static void sequentialStep(pcd.poool.vnativethreads.model.Scene scene) throws InterruptedException {
        scene.updateBoardPhysics(board -> {
            var balls = board.getBalls();
            balls.add(board.getPlayerBall());
            balls.add(board.getBotBall());
            var bounds = board.getBounds();
            List<List<Integer>> collisions = new ArrayList<>();
            for (int i = 0; i < balls.size(); i++) collisions.add(new ArrayList<>());

            for (Ball ball : balls) ball.updateState(STEP_MILLIS, bounds);
            for (int i = 0; i < balls.size(); i++) {
                for (int j = i + 1; j < balls.size(); j++) {
                    if (Ball.checkCollision(balls.get(i), balls.get(j))) collisions.get(i).add(j);
                }
            }
            for (int i = 0; i < balls.size(); i++) {
                for (int j : collisions.get(i)) {
                    Ball first = balls.get(i);
                    Ball second = balls.get(j);
                    if (Ball.resolveCollision(first, second)) {
                        first.hitBy(second.getType());
                        second.hitBy(first.getType());
                    }
                }
            }
            board.setBalls(balls.subList(0, balls.size() - 2));
            board.setPlayerBall(balls.get(balls.size() - 2).copy());
            board.setBotBall(balls.get(balls.size() - 1).copy());
            return 0;
        });
    }

    long nativeThreads(int workers) throws Exception {
        var timer = new FrameTimer(warmupSteps, measuredSteps);
        var scene = new pcd.poool.vnativethreads.model.Scene();
        scene.initBoard(new pcd.poool.vnativethreads.model.MassiveBoardConf());
        scene.addObserver(ignored -> timer.frameCompleted());
        var physics = new pcd.poool.vnativethreads.controller.PhysicsLoop(scene, workers, timer::terminated);
        physics.start();
        try {
            return timer.awaitElapsedNanos();
        } finally {
            stop(physics);
        }
    }

    long executorFramework(int workers) throws Exception {
        var timer = new FrameTimer(warmupSteps, measuredSteps);
        var scene = new pcd.poool.vjavaexecutorframework.model.Scene();
        scene.initBoard(new pcd.poool.vjavaexecutorframework.model.MassiveBoardConf());
        scene.addObserver(ignored -> timer.frameCompleted());
        var executor = Executors.newFixedThreadPool(workers);
        var loop = new pcd.poool.vjavaexecutorframework.controller.PhysicsLoop(
                scene, workers, executor, timer::terminated);
        var physics = new Thread(loop, "physics-benchmark");
        physics.start();
        try {
            return timer.awaitElapsedNanos();
        } finally {
            stop(physics);
            executor.shutdownNow();
            if (!executor.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Executor did not stop");
            }
        }
    }

    private static void stop(Thread physics) throws InterruptedException {
        physics.interrupt();
        physics.join(TimeUnit.SECONDS.toMillis(STOP_TIMEOUT_SECONDS));
        if (physics.isAlive()) throw new IllegalStateException("Physics did not stop");
    }
}
