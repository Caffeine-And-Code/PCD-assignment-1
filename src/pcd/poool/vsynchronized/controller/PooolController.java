package pcd.poool.vsynchronized.controller;

import pcd.poool.vsynchronized.model.Board;
import pcd.poool.vsynchronized.util.BoundedBuffer;
import pcd.poool.vsynchronized.util.BoundedBufferImpl;


public class PooolController extends Thread {
    private static final int BUFFER_SIZE = 200;
    private final BoundedBuffer<Command> buffer = new BoundedBufferImpl<>(BUFFER_SIZE);
    private final Board board;
    private PhysicsLoop physics;

    public PooolController(Board board) {
        super("poool-commands");
        this.board = board;
    }

    // Called during initialization, before start().
    public void setPhysicsLoop(PhysicsLoop physics) {
        this.physics = physics;
    }

    public void run() {
        try {
            while (!isInterrupted()) {
                Command command = buffer.get();
                if (isInterrupted()) break;
                command.execute(board);
            }
        } catch (InterruptedException ex) {
            interrupt();
        } finally {
            if (physics != null) physics.interrupt();
        }
    }

    // Full buffer: discard the new command rather than block Swing's EDT.
    public boolean notifyNewCommand(Command command) {
        return buffer.offer(command);
    }
}
