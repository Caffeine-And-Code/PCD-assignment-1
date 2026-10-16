package pcd.poool.vsynchronized.model;

import pcd.poool.vsynchronized.model.factory.GameFactory;

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

    static Ball getPlayerGoal() {
        return GameFactory.generatePlayerGoal();
    }

    static Ball getBotGoal() {
        return GameFactory.generateBotGoal();
    }
}
