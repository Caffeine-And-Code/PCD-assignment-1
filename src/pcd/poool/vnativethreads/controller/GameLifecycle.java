package pcd.poool.vnativethreads.controller;

import pcd.poool.vnativethreads.model.Scene;

import java.util.concurrent.CountDownLatch;

public final class GameLifecycle {
    private final Scene scene;
    private final PlayerController player;
    private final PhysicsLoop physics;
    private final BotController bot;
    private final CountDownLatch startCompleted = new CountDownLatch(1);
    private boolean started;
    private boolean stopping;

    public GameLifecycle(Scene scene, int workerCount) {
        this.scene = scene;
        player = new PlayerController(scene, this::onTermination);
        physics = new PhysicsLoop(scene, workerCount, this::onTermination);
        bot = new BotController(scene, this::onTermination);
    }

    public PlayerController playerController() {
        return player;
    }

    public synchronized void start() {
        if (started || stopping) return;
        started = true;
        try {
            player.start();
            bot.start();
            physics.start();
        } catch (RuntimeException | Error ex) {
            onTermination(ex);
            throw ex;
        } finally {
            startCompleted.countDown();
        }
    }

    public synchronized void stop() {
        if (stopping) return;
        stopping = true;
        scene.setInExceptionIfRunning();
        player.interrupt();
        bot.interrupt();
        physics.interrupt();
        if (!started) startCompleted.countDown();
    }

    public void awaitTermination() throws InterruptedException {
        physics.join();
        bot.join();
        player.join();
    }

    private void onTermination(Throwable cause) {
        if(cause != null) cause.printStackTrace();
        stop();
    }
}
