package pcd.poool.vsynchronized.model.factory;

import pcd.poool.vsynchronized.model.Ball;
import pcd.poool.vsynchronized.model.P2d;
import pcd.poool.vsynchronized.model.V2d;

import java.util.Random;

public abstract class GameFactory {
    public static final double BALL_MASS = 0.25;
    public static final double PLAYER_BALL_MASS = 1.5;
    public static final double BALL_RADIUS = 0.01;
    public static final double PLAYER_BALL_RADIUS = 0.05;
    public static final double GOAL_RADIUS = 0.15;

    public static P2d generatePosition() {
        Random rand = new Random();

        return new P2d(rand.nextInt(), rand.nextInt());
    }

    public static V2d generateVelocity() {
        Random rand = new Random();

        return new V2d(rand.nextInt(), rand.nextInt());
    }

    public static Ball generateBall(P2d pos) {
        return new Ball(
                pos,
                GameFactory.BALL_RADIUS,
                GameFactory.BALL_MASS,
                V2d.empty()
        );
    }

    public static Ball generatePlayerGoal() {
        return new Ball(
                new P2d(1.5, 1),
                GameFactory.GOAL_RADIUS,
                0,
                V2d.empty()
        );
    }

    public static Ball generateBotGoal() {
        return new Ball(
                new P2d(-1.5, 1),
                GameFactory.GOAL_RADIUS,
                0,
                V2d.empty()
        );
    }

    public static Ball generatePlayerBall() {
        return new Ball(new P2d(0, -0.75), PLAYER_BALL_RADIUS, GameFactory.PLAYER_BALL_MASS, new V2d(0, 1));
    }

    public static Ball generateBotBall() {
        return new Ball(new P2d(1, -0.75), PLAYER_BALL_RADIUS, GameFactory.PLAYER_BALL_MASS, new V2d(0, 1));
    }
}
