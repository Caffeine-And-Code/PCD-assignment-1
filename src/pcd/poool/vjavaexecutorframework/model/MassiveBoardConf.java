package pcd.poool.vjavaexecutorframework.model;

import java.util.ArrayList;
import java.util.List;

public class MassiveBoardConf implements BoardConf {

    @Override
    public List<Ball> getSmallBalls() {
        var balls = new ArrayList<Ball>();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                var px = -1.0 + col * 0.015;
                var py = row * 0.015;
                balls.add(GameFactory.generateBall(new P2d(px, py)));
            }
        }
        return balls;
    }

    public Boundary getBoardBoundary() {
        return new Boundary(-1.5, -1.0, 1.5, 1.0);
    }
}
