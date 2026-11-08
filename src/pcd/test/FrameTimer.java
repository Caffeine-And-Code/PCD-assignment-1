package pcd.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

final class FrameTimer {
    private static final long TIMEOUT_SECONDS = 120;

    private final int warmupSteps;
    private final int totalSteps;
    private final CountDownLatch completed = new CountDownLatch(1);
    private int frames;
    private long start;
    private long end;
    private Throwable failure;

    FrameTimer(int warmupSteps, int measuredSteps) {
        this.warmupSteps = warmupSteps;
        totalSteps = warmupSteps + measuredSteps;
    }

    void frameCompleted() {
        frames++;
        if (frames == warmupSteps) start = System.nanoTime();
        if (frames == totalSteps) {
            end = System.nanoTime();
            completed.countDown();
        }
    }

    void terminated(Throwable cause) {
        if (end == 0) {
            failure = cause != null ? cause : new IllegalStateException("Physics stopped before completing the test");
            completed.countDown();
        }
    }

    long awaitElapsedNanos() throws Exception {
        if (!completed.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Performance test timed out");
        }
        if (failure != null) throw new IllegalStateException("Physics failed", failure);
        return end - start;
    }
}
