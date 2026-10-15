package pcd.sketch01;

import pcd.sketch01.factory.GameFactory;

import java.util.ArrayList;
import java.util.List;

public class MinimalBoardConf implements BoardConf {

	@Override
	public List<Ball> getSmallBalls() {		
        var balls = new ArrayList<Ball>();
		var b1 = GameFactory.generateBall((new P2d(0, 0.5)));
		var b2 = GameFactory.generateBall((new P2d(0.05, 0.55)));
    	balls.add(b1);
    	balls.add(b2);

		return balls;
	}

	@Override
	public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
	}

}
