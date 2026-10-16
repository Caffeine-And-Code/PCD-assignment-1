package pcd.poool.vnativethreads.controller;

import pcd.poool.vnativethreads.model.Scene;
import pcd.poool.vnativethreads.util.BoundedBuffer;
import pcd.poool.vnativethreads.util.BoundedBufferImpl;


public class PlayerController extends Thread {
    private static final int BUFFER_SIZE = 200;
    private final BoundedBuffer<Command> buffer = new BoundedBufferImpl<>(BUFFER_SIZE);
    private final Scene scene;
    private PhysicsLoop physics;
    private BotPlayer bot;

    public PlayerController(Scene scene) {
        super("poool-commands");
        this.scene = scene;
    }

    // Called during initialization, before start().
    public void setGameThreads(PhysicsLoop physics, BotPlayer bot) {
        this.physics = physics;
        this.bot = bot;
        physics.setOnTermination(this::interrupt);
    }

    public void run() {
        try {
            while (!isInterrupted() && !scene.isStopped()) {
                Command command = buffer.get();
                if (isInterrupted()) break;
                command.execute(scene);
            }
        } catch (InterruptedException ex) {
            interrupt();
        } finally {
            scene.setInException();
            if (physics != null) physics.interrupt();
            if (bot != null) bot.interrupt();
        }
    }

    // Full buffer: discard the new command rather than block Swing's EDT.
    public boolean notifyNewCommand(Command command) {
        return !isInterrupted() && !scene.isStopped() && buffer.offer(command);
    }
}
