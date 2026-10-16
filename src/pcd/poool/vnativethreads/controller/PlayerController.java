package pcd.poool.vnativethreads.controller;

import pcd.poool.vnativethreads.model.Scene;
import pcd.poool.vnativethreads.util.BoundedBuffer;
import pcd.poool.vnativethreads.util.BoundedBufferImpl;

import java.util.function.Consumer;

public class PlayerController extends Thread {
    private static final int BUFFER_SIZE = 200;
    private final BoundedBuffer<Command> buffer = new BoundedBufferImpl<>(BUFFER_SIZE);
    private final Scene scene;
    private final Consumer<Throwable> onTermination;

    public PlayerController(Scene scene, Consumer<Throwable> onTermination) {
        super("poool-commands");
        this.scene = scene;
        this.onTermination = onTermination;
    }

    @Override
    public void run() {
        Throwable failure = null;
        try {
            while (!isInterrupted() && !scene.isStopped()) {
                Command command = buffer.get();
                if (isInterrupted()) break;
                command.execute(scene);
            }
        } catch (InterruptedException ex) {
            interrupt();
        } catch (Throwable ex) {
            failure = ex;
            throw ex;
        } finally {
            onTermination.accept(failure);
        }
    }

    // Full buffer: discard the new command rather than block Swing's EDT.
    public boolean notifyNewCommand(Command command) {
        return !isInterrupted() && !scene.isStopped() && buffer.offer(command);
    }
}
