package pcd.poool.controller;

import pcd.poool.model.Board;

/** Dedicated physics thread. Board serializes physics, commands and snapshots. */
public class PhysicsLoop extends Thread {
    private final Board board;
    private final Runnable onStop;

    public PhysicsLoop(Board board, Runnable onStop) {
        super("poool-physics");
        this.board = board;
        this.onStop = onStop;
    }

    public void run() {
        int nFrames = 0;
        long t0 = System.currentTimeMillis();
        long lastUpdateTime = System.currentTimeMillis();
        try {
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
                board.updateState(elapsed, framePerSec);
            }
        } catch (InterruptedException ex) {
            interrupt();
        } finally {
            onStop.run();
        }
    }
}