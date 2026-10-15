package pcd.poool.model.board;

import pcd.poool.model.GameFactory;
import pcd.poool.model.balls.PocketableBall;
import pcd.poool.model.base.Vector;
import pcd.poool.model.hole.Hole;
import pcd.poool.model.hole.HoleImpl;
import pcd.poool.model.player.PlayerType;

import java.util.ArrayList;
import java.util.List;

public class BoardConfImpl implements BoardConf {

	private final double x0 = -1.5;
	private final double y0 = -1.0;
	private final double x1 = 1.5;
	private final double y1 = 1.0;

	private final Boundary boundary = new Boundary(x0, y0, x1, y1);

	@Override
	public List<PocketableBall> getSmallBalls() {
		var ballRadius = 0.01;
        var balls = new ArrayList<PocketableBall>();
		int incrementalId = 0;

    	for (int row = 0; row < 20; row++) {
    		for (int col = 0; col < 20; col++) {
        		var px = -0.25 + col*0.025;
        		var py =  row*0.025;
				var b = GameFactory.generateBall(incrementalId++, new Vector(px, py));
            	balls.add(b);    			
    		}
    	}		
    	return balls;
	}

	@Override
	public Hole getPlayerHole() {
		return new HoleImpl(
				new Vector(x0, y0),
				0.05,
				PlayerType.PLAYER);
	}

	@Override
	public Hole getBotHole() {
		return new HoleImpl(
				new Vector(x1, y0),
				0.05,
				PlayerType.BOT);
	}

	@Override
	public Boundary getBoardBoundary() {
        return this.boundary;
	}
}
