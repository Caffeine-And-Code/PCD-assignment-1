package pcd.test.jpf;

import pcd.poool.vjavaexecutorframework.util.BoundedBuffer;
import pcd.poool.vjavaexecutorframework.util.BoundedBufferImpl;

/** JPF entry point for the Executor Framework bounded buffer. */
public final class ExecutorBufferJpfTest {
    private ExecutorBufferJpfTest() {
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBufferImpl<>(1);
        var producer = new Thread(() -> produce(buffer), "producer");
        var consumer = new Thread(() -> consume(buffer), "consumer");

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();

        assert !producer.isAlive();
        assert !consumer.isAlive();
        assert buffer.poll() == null;
    }

    private static void produce(BoundedBuffer<Integer> buffer) {
        try {
            buffer.put(1);
            buffer.put(2);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Producer interrupted", ex);
        }
    }

    private static void consume(BoundedBuffer<Integer> buffer) {
        try {
            assert buffer.get() == 1;
            assert buffer.get() == 2;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Consumer interrupted", ex);
        }
    }
}
