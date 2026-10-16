package pcd.poool.vjavaexecutorframework.view;

import pcd.poool.vjavaexecutorframework.model.GameStatus;
import pcd.poool.vjavaexecutorframework.model.Scene;

import java.util.ArrayList;

public class ViewModel {

    private final ArrayList<BallViewInfo> balls;
    private final ArrayList<HoleViewInfo> holes;
    private BallViewInfo player;
    private BallViewInfo bot;
    private int framePerSec;
    private int humanScore;
    private int botScore;
    private GameStatus status;

    public ViewModel() {
        balls = new ArrayList<>();
        holes = new ArrayList<>();
        framePerSec = 0;
        status = GameStatus.RUNNING;
    }

    public void update(Scene scene) {
        var state = scene.snapshot();
        var board = state.board();
        var ballsFromBoard = board.getBalls().stream()
                .map(b -> new BallViewInfo(b.getPos(), b.getRadius())).toList();
        var holesFromBoard = board.getHoles().stream()
                .map(b -> new HoleViewInfo(b.center(), b.radius())).toList();
        var playerBall = board.getPlayerBall();
        var botBall = board.getBotBall();
        synchronized (this) {
            balls.clear();
            balls.addAll(ballsFromBoard);
            holes.clear();
            holes.addAll(holesFromBoard);
            player = new BallViewInfo(playerBall.getPos(), playerBall.getRadius());
            bot = new BallViewInfo(botBall.getPos(), botBall.getRadius());
            humanScore = state.humanScore();
            botScore = state.botScore();
            status = state.status();
            framePerSec = state.fps();
        }
    }

    public synchronized ArrayList<BallViewInfo> getBalls() {
        var copy = new ArrayList<BallViewInfo>();
        copy.addAll(balls);

        return copy;
    }

    public synchronized ArrayList<HoleViewInfo> getHoles() {
        return new ArrayList<>(holes);
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
