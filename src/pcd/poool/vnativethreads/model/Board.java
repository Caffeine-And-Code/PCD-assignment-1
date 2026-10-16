package pcd.poool.vnativethreads.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final double HOLE_RADIUS = 0.2;

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;

    public Board() {
    }

    public Board(BoardConf conf) {
        init(conf);
    }

    public Board(List<Ball> balls, Ball playerBall, Ball botBall, Boundary boundary) {
        this.balls = balls;
        this.playerBall = playerBall;
        this.botBall = botBall;
        this.bounds = boundary;
    }

    public void init(BoardConf conf) {
        balls = new ArrayList<>(conf.getSmallBalls().stream().map(Ball::copy).toList());
        playerBall = BoardConf.getPlayerBall();
        botBall = BoardConf.getBotBall();
        bounds = conf.getBoardBoundary();
    }

    public void kickPlayer(V2d impulse) {
        playerBall.kick(playerBall.getVel().sum(impulse));
    }

    public void kickBot(V2d impulse) {
        botBall.kick(botBall.getVel().sum(impulse));
    }

    public Board setBalls(List<Ball> balls) {
        this.balls = balls;
        return this;
    }

    public Board setPlayerBall(Ball ball) {
        this.playerBall = ball;
        return this;
    }

    public Board setBotBall(Ball ball) {
        this.botBall = ball;
        return this;
    }

    public List<Ball> getBalls() {
        return new ArrayList<>(balls.stream().map(Ball::copy).toList());
    }

    public Ball getPlayerBall() {
        return playerBall.copy();
    }

    public Ball getBotBall() {
        return botBall.copy();
    }

    public Boundary getBounds() {
        return bounds;
    }

    public boolean isInHole(Ball ball) {
        double leftDx = ball.getPos().x() - bounds.x0();
        double rightDx = ball.getPos().x() - bounds.x1();
        double dy = ball.getPos().y() - bounds.y1();
        double radiusSquared = HOLE_RADIUS * HOLE_RADIUS;
        return leftDx * leftDx + dy * dy <= radiusSquared
                || rightDx * rightDx + dy * dy <= radiusSquared;
    }

    public Board copy() {
        return new Board(getBalls(), getPlayerBall(), getBotBall(), getBounds());
    }
}
