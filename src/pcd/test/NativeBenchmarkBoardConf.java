package pcd.test;

import pcd.poool.vnativethreads.model.Ball;
import pcd.poool.vnativethreads.model.BoardConf;
import pcd.poool.vnativethreads.model.Boundary;
import pcd.poool.vnativethreads.model.GameFactory;
import pcd.poool.vnativethreads.model.P2d;

import java.util.ArrayList;
import java.util.List;

/** Fixed workload used to benchmark the native-threads implementation. */
final class NativeBenchmarkBoardConf implements BoardConf {
    @Override
    public List<Ball> getSmallBalls() {
        var balls = new ArrayList<Ball>();
        for (int row = 0; row < 30; row++) {
            for (int column = 0; column < 150; column++) {
                balls.add(GameFactory.generateBall(new P2d(-1.0 + column * 0.015, row * 0.015)));
            }
        }
        return balls;
    }

    @Override
    public Boundary getBoardBoundary() {
        return new Boundary(-1.5, -1.0, 1.5, 1.0);
    }
}
