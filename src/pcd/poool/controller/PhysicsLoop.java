package pcd.poool.controller;

import pcd.poool.model.Board;
import pcd.poool.model.PhysicsWorkers;

/** Dedicated physics thread. Board serializes physics, commands and snapshots. */
public class PhysicsLoop extends Thread {
    private final Board board;
    private final Runnable onStop;
    private final int workerCount;

    public PhysicsLoop(Board board, Runnable onStop) {
        this(board, Math.max(1, Runtime.getRuntime().availableProcessors() - 1), onStop);
    }

    public PhysicsLoop(Board board, int workerCount, Runnable onStop) {
        super("poool-physics");
        if (workerCount < 1) throw new IllegalArgumentException("workers must be positive");
        this.workerCount = workerCount;
        this.board = board;
        this.onStop = onStop;
    }

    public void run() {
        int nFrames = 0;
        long t0 = System.currentTimeMillis();
        long lastUpdateTime = System.currentTimeMillis();
        try (var workers = new PhysicsWorkers(workerCount)) {
            while (!isInterrupted()) {
                /* update board state */

                long elapsed = System.currentTimeMillis() - lastUpdateTime;
                lastUpdateTime = System.currentTimeMillis();

                /* render */

                nFrames++;
                int framePerSec = 0;
                long dt = (System.currentTimeMillis() - t0);
                if (dt > 0) {
                    framePerSec = (int)(nFrames*1000L/dt);
                }
                board.updateState(elapsed, framePerSec, workers);
            }
        } catch (InterruptedException ex) {
            interrupt();
        } finally {
            onStop.run();
        }
    }
}
