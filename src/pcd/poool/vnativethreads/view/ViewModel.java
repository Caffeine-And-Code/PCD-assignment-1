package pcd.poool.vnativethreads.view;

import pcd.poool.vnativethreads.model.GameStatus;
import pcd.poool.vnativethreads.model.P2d;
import pcd.poool.vnativethreads.model.Scene;

import java.util.ArrayList;

record BallViewInfo(P2d pos, double radius) {
}

public class ViewModel {

    private final ArrayList<BallViewInfo> balls;
    private BallViewInfo player;
    private BallViewInfo bot;
    private int framePerSec;
    private int humanScore;
    private int botScore;
    private GameStatus status;

    public ViewModel() {
        balls = new ArrayList<>();
        framePerSec = 0;
        status = GameStatus.RUNNING;
    }

    public synchronized void updateFPS(Integer fps) {
        this.framePerSec = fps;
    }

    public void update(Scene scene) {
        var nextBalls = new ArrayList<BallViewInfo>();
        var board = scene.getBoard();
        for (var ball : board.getBalls()) {
            nextBalls.add(new BallViewInfo(ball.getPos(), ball.getRadius()));
        }
        var p = board.getPlayerBall();
        var b = board.getBotBall();
        var nextHumanScore = scene.getHumanScore();
        var nextBotScore = scene.getBotScore();
        var nextStatus = scene.getStatus();
        synchronized (this) {
            balls.clear();
            balls.addAll(nextBalls);
            player = new BallViewInfo(p.getPos(), p.getRadius());
            bot = new BallViewInfo(b.getPos(), b.getRadius());
            humanScore = nextHumanScore;
            botScore = nextBotScore;
            status = nextStatus;
            framePerSec = scene.getFps();
        }
    }

    public synchronized ArrayList<BallViewInfo> getBalls() {
        var copy = new ArrayList<BallViewInfo>();
        copy.addAll(balls);

        return copy;
    }

    public synchronized int getFramePerSec() {
        return framePerSec;
    }

    public synchronized BallViewInfo getPlayerBall() {
        return player;
    }

    public synchronized BallViewInfo getBotBall() {
        return bot;
    }

    public synchronized int getHumanScore() {
        return humanScore;
    }

    public synchronized int getBotScore() {
        return botScore;
    }

    public synchronized GameStatus getStatus() {
        return status;
    }

}
