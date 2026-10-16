package pcd.poool.vnativethreads.controller;

import pcd.poool.vnativethreads.model.Scene;
import pcd.poool.vnativethreads.util.BoundedBuffer;
import pcd.poool.vnativethreads.util.BoundedBufferImpl;


public class PooolController extends Thread {
    private static final int BUFFER_SIZE = 200;
    private final BoundedBuffer<Command> buffer = new BoundedBufferImpl<>(BUFFER_SIZE);
    private final Scene scene;
    private PhysicsLoop physics;
    private BotPlayer bot;

    public PooolController(Scene scene) {
        super("poool-commands");
        this.scene = scene;
    }

    // Called during initialization, before start().
    public void setGameThreads(PhysicsLoop physics, BotPlayer bot) {
        this.physics = physics;
        this.bot = bot;
    }

    public void run() {
        try {
            while (!isInterrupted()) {
                Command command = buffer.get();
                if (isInterrupted()) break;
                command.execute(scene);
            }
        } catch (InterruptedException ex) {
            interrupt();
        } finally {
            if (physics != null) physics.interrupt();
            if (bot != null) bot.interrupt();
        }
    }

    // Full buffer: discard the new command rather than block Swing's EDT.
    public boolean notifyNewCommand(Command command) {
        return buffer.offer(command);
    }
}
