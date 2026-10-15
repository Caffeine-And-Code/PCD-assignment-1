package pcd.sketch01;

import pcd.sketch01.factory.GameFactory;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();

	static Ball getPlayerBall() {
		return GameFactory.generatePlayerBall();
	}
	
	List<Ball> getSmallBalls();

	static Ball getBotBall() {
		return GameFactory.generateBotBall();
	}
}
