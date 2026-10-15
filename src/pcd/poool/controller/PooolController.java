package pcd.poool.controller;

import pcd.sketch02.util.BoundedBuffer;
import pcd.sketch02.util.BoundedBufferImpl;

public class PooolController extends Thread {

    private final BoundedBuffer<Command> buffer;

    public PooolController(){
        int bufferSize = 200;
        this.buffer = new BoundedBufferImpl<>(bufferSize);
    }

    public void run(){
        while(true){
            try {
                Command command = this.buffer.get();
                command.execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void notifyNewCommand(Command command) throws InterruptedException {
        this.buffer.put(command);
    }
}
