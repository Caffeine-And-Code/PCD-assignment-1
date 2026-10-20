package pcd.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

final class PhysicsBenchmark {
    private static final long THREAD_TIMEOUT_SECONDS = 60;
    static final long RESPONSE_INTERVAL_MILLIS = 250;

    private final long warmupMillis;
    private final long measurementMillis;

    PhysicsBenchmark(long warmupMillis, long measurementMillis) {
        this.warmupMillis = warmupMillis;
        this.measurementMillis = measurementMillis;
    }

    BenchmarkSample sequential() throws Exception {
        var monitor = new RunMonitor();
        var board = new pcd.poool.vsynchronized.model.Board();
        board.init(new pcd.poool.vsynchronized.model.MassiveBoardConf());
        board.addObserver(ignored -> monitor.frameCompleted());
        var physics = new pcd.poool.vsynchronized.controller.PhysicsLoop(
                board, () -> monitor.terminated(null));
        var commands = new pcd.poool.vsynchronized.controller.PooolController(board);
        commands.setPhysicsLoop(physics);
        monitorFailure(physics, monitor);
        monitorFailure(commands, monitor);
        return runThreaded(physics, commands, monitor, completed ->
                commands.notifyNewCommand(target -> {
                    target.kickPlayer(new pcd.poool.vsynchronized.model.V2d(0, 0));
                    completed.run();
                }));
    }

    BenchmarkSample nativeThreads(int workers) throws Exception {
        var monitor = new RunMonitor();
        var scene = new pcd.poool.vnativethreads.model.Scene();
        scene.initBoard(new pcd.poool.vnativethreads.model.MassiveBoardConf());
        scene.addObserver(ignored -> monitor.frameCompleted());
        var physics = new pcd.poool.vnativethreads.controller.PhysicsLoop(
                scene, workers, monitor::terminated);
        var commands = new pcd.poool.vnativethreads.controller.PlayerController(scene, monitor::terminated);
        monitorFailure(physics, monitor);
        monitorFailure(commands, monitor);
        return runThreaded(physics, commands, monitor, completed ->
                commands.notifyNewCommand(target -> {
                    target.updateBoard(board ->
                            board.kickPlayer(new pcd.poool.vnativethreads.model.V2d(0, 0)));
                    completed.run();
                }));
    }

    BenchmarkSample executorFramework(int workers) throws Exception {
        var monitor = new RunMonitor();
        var scene = new pcd.poool.vjavaexecutorframework.model.Scene();
        scene.initBoard(new pcd.poool.vjavaexecutorframework.model.MassiveBoardConf());
        scene.addObserver(ignored -> monitor.frameCompleted());
        var executor = Executors.newFixedThreadPool(workers);
        try {
            var physicsLoop = new pcd.poool.vjavaexecutorframework.controller.PhysicsLoop(
                    scene, workers, executor, monitor::terminated);
            var physics = new Thread(physicsLoop, "physics-benchmark");
            var player = new pcd.poool.vjavaexecutorframework.controller.PlayerController(
                    scene, monitor::terminated);
            var commands = new Thread(player, "commands-benchmark");
            monitorFailure(physics, monitor);
            monitorFailure(commands, monitor);
            return runThreaded(physics, commands, monitor, completed ->
                    player.notifyNewCommand(target -> {
                        target.updateBoard(board ->
                                board.kickPlayer(new pcd.poool.vjavaexecutorframework.model.V2d(0, 0)));
                        completed.run();
                    }));
        } finally {
            executor.shutdownNow();
            if (!executor.awaitTermination(THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Physics workers did not stop in time");
            }
        }
    }

    private BenchmarkSample runThreaded(Thread physics, Thread commands, RunMonitor monitor,
                                        Predicate<Runnable> submitCommand) throws Exception {
        commands.start();
        physics.start();
        try {
            monitor.awaitFirstFrame();
            ensureHealthy(physics, commands, monitor);
            Thread.sleep(warmupMillis);

            long framesBefore = monitor.frames();
            long start = System.nanoTime();
            long deadline = start + TimeUnit.MILLISECONDS.toNanos(measurementMillis);
            long nextResponse = start;
            long responseNanos = 0;
            int responses = 0;
            while ((nextResponse += TimeUnit.MILLISECONDS.toNanos(RESPONSE_INTERVAL_MILLIS)) < deadline) {
                sleepUntil(nextResponse);
                responseNanos += measureResponse(submitCommand);
                responses++;
                ensureHealthy(physics, commands, monitor);
            }
            sleepUntil(deadline);
            long elapsed = System.nanoTime() - start;
            long frames = monitor.frames() - framesBefore - responses;
            ensureHealthy(physics, commands, monitor);
            if (frames == 0) throw new IllegalStateException("No update completed: increase measurement time");
            if (responses == 0) throw new IllegalStateException("No response measured: increase measurement time");
            return new BenchmarkSample(perSecond(frames, elapsed), responseNanos / (double) responses / 1_000_000);
        } finally {
            commands.interrupt();
            physics.interrupt();
            join(commands, "Command controller");
            join(physics, "Physics");
            monitor.throwIfFailed();
        }
    }

    private long measureResponse(Predicate<Runnable> submitCommand) throws InterruptedException {
        var completed = new CountDownLatch(1);
        long start = System.nanoTime();
        if (!submitCommand.test(completed::countDown)) {
            throw new IllegalStateException("Command buffer rejected the responsiveness probe");
        }
        if (!completed.await(THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Command did not complete in time");
        }
        return System.nanoTime() - start;
    }

    private static void sleepUntil(long deadline) throws InterruptedException {
        long remaining = deadline - System.nanoTime();
        if (remaining > 0) TimeUnit.NANOSECONDS.sleep(remaining);
    }

    private static void join(Thread thread, String name) throws InterruptedException {
        thread.join(TimeUnit.SECONDS.toMillis(THREAD_TIMEOUT_SECONDS));
        if (thread.isAlive()) throw new IllegalStateException(name + " did not stop in time");
    }

    private static void monitorFailure(Thread thread, RunMonitor monitor) {
        thread.setUncaughtExceptionHandler((ignored, cause) -> monitor.terminated(cause));
    }

    private static double perSecond(long frames, long elapsedNanos) {
        return frames * 1_000_000_000.0 / elapsedNanos;
    }

    private static void ensureHealthy(Thread physics, Thread commands, RunMonitor monitor) {
        monitor.throwIfFailed();
        if (!physics.isAlive()) throw new IllegalStateException("Physics stopped before the test ended");
        if (!commands.isAlive()) throw new IllegalStateException("Command controller stopped before the test ended");
    }

}
