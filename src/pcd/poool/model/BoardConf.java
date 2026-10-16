package pcd.poool.model;

import pcd.poool.model.factory.GameFactory;

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
