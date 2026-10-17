package pcd.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/** Observes the progress and termination of a concurrent physics loop. */
final class RunMonitor {
    private static final long START_TIMEOUT_SECONDS = 60;

    private final AtomicLong frames = new AtomicLong();
    private final AtomicReference<Throwable> failure = new AtomicReference<>();
    private final CountDownLatch firstFrame = new CountDownLatch(1);

    void frameCompleted() {
        frames.incrementAndGet();
        firstFrame.countDown();
    }

    void terminated(Throwable cause) {
        if (cause != null) failure.compareAndSet(null, cause);
        firstFrame.countDown();
    }

    void awaitFirstFrame() throws InterruptedException {
        if (!firstFrame.await(START_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Physics did not complete its first update in time");
        }
    }

    long frames() {
        return frames.get();
    }

    void throwIfFailed() {
        if (failure.get() != null) throw new IllegalStateException("Physics benchmark failed", failure.get());
    }
}
