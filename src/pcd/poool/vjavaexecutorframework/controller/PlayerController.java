package pcd.poool.vjavaexecutorframework.controller;

import pcd.poool.vjavaexecutorframework.model.Scene;
import pcd.poool.vjavaexecutorframework.util.BoundedBuffer;
import pcd.poool.vjavaexecutorframework.util.BoundedBufferImpl;

import java.util.function.Consumer;

public class PlayerController implements Runnable {
    private static final int BUFFER_SIZE = 200;
    private final BoundedBuffer<Command> buffer = new BoundedBufferImpl<>(BUFFER_SIZE);
    private final Scene scene;
    private final Consumer<Throwable> onTermination;

    public PlayerController(Scene scene, Consumer<Throwable> onTermination) {
        this.scene = scene;
        this.onTermination = onTermination;
    }

    @Override
    public void run() {
        Throwable failure = null;
        try {
            while (!Thread.currentThread().isInterrupted() && !scene.isStopped()) {
                Command command = buffer.get();
                if (Thread.currentThread().isInterrupted()) break;
                command.execute(scene);
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

    // Full buffer: discard the new command rather than block Swing's EDT.
    public boolean notifyNewCommand(Command command) {
        return !scene.isStopped() && buffer.offer(command);
    }
}
