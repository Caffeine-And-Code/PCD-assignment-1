package pcd.test.jpf;

import pcd.poool.vnativethreads.util.BoundedBuffer;
import pcd.poool.vnativethreads.util.BoundedBufferImpl;

public final class BoundedBufferJpfTest {
    private BoundedBufferJpfTest() {
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBufferImpl<>(1);
        var producer = new Thread(() -> produce(buffer));
        var consumer = new Thread(() -> consume(buffer));

        producer.start();
        consumer.start();
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
            throw new AssertionError(ex);
        }
    }

    private static void consume(BoundedBuffer<Integer> buffer) {
        try {
            assert buffer.get() == 1;
            assert buffer.get() == 2;
        } catch (InterruptedException ex) {
            throw new AssertionError(ex);
        }
    }
}
