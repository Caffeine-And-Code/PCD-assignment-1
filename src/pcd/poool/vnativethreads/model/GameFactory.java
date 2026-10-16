package pcd.poool.vnativethreads.model;

public abstract class GameFactory {
    public static final double BALL_MASS = 0.25;
    public static final double PLAYER_BALL_MASS = 1.5;
    public static final double BALL_RADIUS = 0.01;
    public static final double PLAYER_BALL_RADIUS = 0.05;

    public static Ball generateBall(P2d pos) {
        return new Ball(
                pos,
                GameFactory.BALL_RADIUS,
                GameFactory.BALL_MASS,
                V2d.empty(),
                BallType.SMALL
        );
    }

    public static Ball generatePlayerBall() {
        return new Ball(new P2d(-0.75, -0.75), PLAYER_BALL_RADIUS, GameFactory.PLAYER_BALL_MASS, V2d.empty(), BallType.PLAYER);
    }

    public static Ball generateBotBall() {
        return new Ball(new P2d(0.75, -0.75), PLAYER_BALL_RADIUS, GameFactory.PLAYER_BALL_MASS, V2d.empty(), BallType.BOT);
    }
}
