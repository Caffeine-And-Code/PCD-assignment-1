package pcd.poool.model;

import pcd.poool.model.balls.Ball;
import pcd.poool.model.balls.PlayerBall;
import pcd.poool.model.balls.PocketableBall;
import pcd.poool.model.base.Vector;
import pcd.poool.model.player.PlayerType;

import java.util.Random;

public abstract class GameFactory {
    public static final double BALL_MASS = 0.25;
    public static final double PLAYER_BALL_MASS = 1.5;
    public static final double BALL_RADIUS = 0.01;
    public static final double PLAYER_BALL_RADIUS = 0.05;
    private static final Vector PLAYER_POSITION = new Vector(0, -0.75);
    private static final Vector BOT_POSITION = new Vector(1, -0.75);
    private static final Vector INITIAL_VELOCITY = new Vector(0, 1);


    public static Vector generateVector() {
        Random rand = new Random();

        return new Vector(rand.nextInt(), rand.nextInt());
    }

    public static PocketableBall generateBall(int id, Vector pos) {
        return new PocketableBall(
                id,
                pos,
                Vector.empty(),
                GameFactory.BALL_RADIUS,
                GameFactory.BALL_MASS
        );
    }

    public static PlayerBall generatePlayerBall() {
        return new PlayerBall(
                INITIAL_VELOCITY,
                PLAYER_POSITION,
                PLAYER_BALL_RADIUS,
                PLAYER_BALL_MASS,
                PlayerType.PLAYER);
    }

    public static PlayerBall generateBotBall() {
        return new PlayerBall(
                INITIAL_VELOCITY,
                BOT_POSITION,
                PLAYER_BALL_RADIUS,
                PLAYER_BALL_MASS,
                PlayerType.BOT);
    }
}
