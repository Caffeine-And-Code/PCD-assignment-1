package pcd.test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Runs one performance sample against the production physics code. */
final class PhysicsBenchmark {
    private static final long THREAD_TIMEOUT_SECONDS = 60;

    private final long warmupMillis;
    private final long measurementMillis;

    PhysicsBenchmark(long warmupMillis, long measurementMillis) {
        this.warmupMillis = warmupMillis;
        this.measurementMillis = measurementMillis;
    }

    double sequential() throws InterruptedException {
        var board = new pcd.poool.vsynchronized.model.Board();
        board.init(new pcd.poool.vsynchronized.model.MassiveBoardConf());
        board.updateState(0, 0);
        runSequentialFor(board, warmupMillis);
        return runSequentialFor(board, measurementMillis);
    }

    double nativeThreads(int workers) throws Exception {
        var monitor = new RunMonitor();
        var scene = new pcd.poool.vnativethreads.model.Scene();
        scene.initBoard(new NativeBenchmarkBoardConf());
        scene.addObserver(ignored -> monitor.frameCompleted());
        var physics = new pcd.poool.vnativethreads.controller.PhysicsLoop(
                scene, workers, monitor::terminated);
        return runThreaded(physics, monitor);
    }

    double executorFramework(int workers) throws Exception {
        var monitor = new RunMonitor();
        var scene = new pcd.poool.vjavaexecutorframework.model.Scene();
        scene.initBoard(new ExecutorBenchmarkBoardConf());
        scene.addObserver(ignored -> monitor.frameCompleted());
        var executor = Executors.newFixedThreadPool(workers);
        try {
            var physicsLoop = new pcd.poool.vjavaexecutorframework.controller.PhysicsLoop(
                    scene, workers, executor, monitor::terminated);
            var physicsThread = new Thread(physicsLoop, "physics-benchmark");
            return runThreaded(physicsThread, monitor);
        } finally {
            executor.shutdownNow();
            if (!executor.awaitTermination(THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Physics workers did not stop in time");
            }
        }
    }

    private double runSequentialFor(pcd.poool.vsynchronized.model.Board board, long durationMillis)
            throws InterruptedException {
        if (durationMillis == 0) return 0;
        long durationNanos = TimeUnit.MILLISECONDS.toNanos(durationMillis);
        long start = System.nanoTime();
        long frames = 0;
        while (System.nanoTime() - start < durationNanos) {
            board.updateState(16, 0);
            frames++;
        }
        return perSecond(frames, System.nanoTime() - start);
    }

    private double runThreaded(Thread physics, RunMonitor monitor) throws Exception {
        physics.start();
        try {
            monitor.awaitFirstFrame();
            ensureHealthy(physics, monitor);
            Thread.sleep(warmupMillis);

            long framesBefore = monitor.frames();
            long start = System.nanoTime();
            Thread.sleep(measurementMillis);
            long elapsed = System.nanoTime() - start;
            long frames = monitor.frames() - framesBefore;
            ensureHealthy(physics, monitor);
            if (frames == 0) throw new IllegalStateException("No update completed: increase measurement time");
            return perSecond(frames, elapsed);
        } finally {
            physics.interrupt();
            physics.join(TimeUnit.SECONDS.toMillis(THREAD_TIMEOUT_SECONDS));
            if (physics.isAlive()) throw new IllegalStateException("Physics did not stop in time");
            monitor.throwIfFailed();
        }
    }

    private static double perSecond(long frames, long elapsedNanos) {
        return frames * 1_000_000_000.0 / elapsedNanos;
    }

    private static void ensureHealthy(Thread physics, RunMonitor monitor) {
        monitor.throwIfFailed();
        if (!physics.isAlive()) throw new IllegalStateException("Physics stopped before the test ended");
    }

}
