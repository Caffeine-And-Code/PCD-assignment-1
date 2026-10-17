package pcd.test;

import pcd.poool.vjavaexecutorframework.model.Ball;
import pcd.poool.vjavaexecutorframework.model.BoardConf;
import pcd.poool.vjavaexecutorframework.model.Boundary;
import pcd.poool.vjavaexecutorframework.model.GameFactory;
import pcd.poool.vjavaexecutorframework.model.P2d;

import java.util.ArrayList;
import java.util.List;

/** Fixed workload used to benchmark the Executor Framework implementation. */
final class ExecutorBenchmarkBoardConf implements BoardConf {
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
