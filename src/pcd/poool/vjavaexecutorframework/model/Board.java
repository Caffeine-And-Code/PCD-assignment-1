package pcd.poool.vjavaexecutorframework.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private List<Hole> holes = List.of();

    public Board() {
    }

    public Board(BoardConf conf) {
        init(conf);
    }

    public Board(List<Ball> balls, Ball playerBall, Ball botBall, Boundary boundary, List<Hole> holes) {
        this.balls = balls;
        this.playerBall = playerBall;
        this.botBall = botBall;
        this.bounds = boundary;
        this.holes = holes;
    }

    public void init(BoardConf conf) {
        balls = new ArrayList<>(conf.getSmallBalls().stream().map(Ball::copy).toList());
        playerBall = BoardConf.getPlayerBall();
        botBall = BoardConf.getBotBall();
        bounds = conf.getBoardBoundary();
        holes = conf.getHoles();
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

    public List<Hole> getHoles() {
        return List.copyOf(holes);
    }

    public boolean isInHole(Ball ball) {
        return holes.stream().anyMatch(hole -> hole.contains(ball));
    }

    public Board copy() {
        return new Board(getBalls(), getPlayerBall(), getBotBall(), getBounds(), getHoles());
    }
}
