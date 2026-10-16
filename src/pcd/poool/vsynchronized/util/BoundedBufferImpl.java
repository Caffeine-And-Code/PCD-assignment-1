package pcd.poool.vsynchronized.util;

import java.util.LinkedList;

/**
 * 
 * Simple implementation of a bounded buffer
 * as a monitor, using raw mechanisms
 * 
 * @param <Item>
 */
public class BoundedBufferImpl<Item> implements BoundedBuffer<Item> {

	private final LinkedList<Item> buffer;
	private final int maxSize;

	public BoundedBufferImpl(int size) {
		buffer = new LinkedList<>();
		maxSize = size;
	}

	public synchronized void put(Item item) throws InterruptedException {
		while (isFull()) {
			wait();
		}
		buffer.addLast(item);
		notifyAll();
	}

	public synchronized Item get() throws InterruptedException {
		while (isEmpty()) {
			wait();
		}
		Item item = buffer.removeFirst();
		notifyAll();
		return item;
	}

    public synchronized boolean offer(Item item) {
        if (isFull()) return false;
        buffer.addLast(item);
        notifyAll();
        return true;
    }

    public synchronized Item poll() {
        if (isEmpty()) return null;
        Item item = buffer.removeFirst();
        notifyAll();
        return item;
    }

	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.isEmpty();
	}
}
