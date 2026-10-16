package pcd.poool.vjavaexecutorframework.controller;

import pcd.poool.vjavaexecutorframework.model.Scene;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class GameLifecycle {
    private final Scene scene;
    private final PlayerController player;
    private final PhysicsLoop physics;
    private final BotController bot;
    private final ExecutorService executor;
    private boolean started;
    private boolean stopping;

    public GameLifecycle(Scene scene, int workerCount) {
        if (workerCount <= 0) throw new IllegalArgumentException("workerCount must be positive");
        this.scene = scene;
        executor = Executors.newFixedThreadPool(Math.addExact(workerCount, 20));
        player = new PlayerController(scene, this::onTermination);
        physics = new PhysicsLoop(scene, workerCount, executor, this::onTermination);
        bot = new BotController(scene, this::onTermination);
    }

    public PlayerController playerController() {
        return player;
    }

    public synchronized void start() {
        if (started || stopping) return;
        started = true;
        try {
            executor.submit(player);
            executor.submit(bot);
            executor.submit(physics);
        } catch (RuntimeException | Error ex) {
            onTermination(ex);
            throw ex;
        }
    }

    public synchronized void stop() {
        if (stopping) return;
        stopping = true;
        scene.setInExceptionIfRunning();
        executor.shutdownNow();
    }

    public void awaitTermination() throws InterruptedException {
        while (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) { }
    }

    private void onTermination(Throwable cause) {
        if(cause != null) cause.printStackTrace();
        stop();
    }
}
