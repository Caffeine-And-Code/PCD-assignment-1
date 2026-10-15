package pcd.poool.model.board;

import pcd.poool.model.balls.Ball;
import pcd.poool.model.GameFactory;
import pcd.poool.model.balls.PlayerBall;
import pcd.poool.model.balls.PocketableBall;
import pcd.poool.model.hole.Hole;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	List<PocketableBall> getSmallBalls();

	static PlayerBall getPlayerBall() {
		return GameFactory.generatePlayerBall();
	}

	static PlayerBall getBotBall() {
		return GameFactory.generateBotBall();
	}

	Hole getPlayerHole();

	Hole getBotHole();
}
