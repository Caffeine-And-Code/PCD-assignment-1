package pcd.poool.vjavaexecutorframework.util;

public interface BoundedBuffer<Item> {

    boolean offer(Item item);

    Item poll();

    void put(Item item) throws InterruptedException;

    Item get() throws InterruptedException;

}
