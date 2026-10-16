package pcd.poool.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Monitor: mutable balls never escape; physics and impulses use the same lock.
 */
public class Board {
    private final ReentrantLock lock = new ReentrantLock(true);
    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private int framePerSec;
    private final List<BoardObserver> observers = new ArrayList<>();

    public void init(BoardConf conf) {
        lock.lock();
        try {
            balls = conf.getSmallBalls().stream()
                    .map(b -> new Ball(b.getPos(), b.getRadius(), b.getMass(), b.getVel()))
                    .toList();
            playerBall = BoardConf.getPlayerBall();
            botBall = BoardConf.getBotBall();
            bounds = conf.getBoardBoundary();
        } finally {
            lock.unlock();
        }
    }

    public void addObserver(BoardObserver observer) {
        lock.lock();
        try {
            observers.add(observer);
        } finally {
            lock.unlock();
        }
    }

    // Like sketch02, callbacks run while holding the model lock.
    // Reentrancy allows observers to call snapshot() on this same thread.
    private void notifyObservers() {
        for (var observer : observers) observer.modelUpdate(this);
    }

    public void updateState(long dt) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            updateState(dt, framePerSec);
        } finally {
            lock.unlock();
        }
    }

    public void updateState(long dt, int framePerSec) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            this.framePerSec = framePerSec;
            playerBall.updateState(dt, this);
            botBall.updateState(dt, this);
            for (var ball : balls) ball.updateState(dt, this);
            for (int i = 0; i < balls.size() - 1; i++) {
                for (int j = i + 1; j < balls.size(); j++) {
                    Ball.resolveCollision(balls.get(i), balls.get(j));
                }
            }
            for (var ball : balls) {
                Ball.resolveCollision(playerBall, ball);
                Ball.resolveCollision(botBall, ball);
            }
            Ball.resolveCollision(playerBall, botBall);
            notifyObservers();
        } finally {
            lock.unlock();
        }
    }

    public void kickPlayer(V2d impulse) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            playerBall.kick(playerBall.getVel().sum(impulse));
            notifyObservers();
        } finally {
            lock.unlock();
        }
    }

    public record BallState(P2d pos, double radius) {
        private static BallState of(Ball ball) {
            return new BallState(ball.getPos(), ball.getRadius());
        }
    }

    public record State(List<BallState> balls, BallState player, BallState bot, int framePerSec) {
        public State {
            balls = List.copyOf(balls);
        }
    }

    public State snapshot() {
        lock.lock();
        try {
            return new State(balls.stream().map(BallState::of).toList(),
                    BallState.of(playerBall), BallState.of(botBall), framePerSec);
        } finally {
            lock.unlock();
        }
    }

    public Boundary getBounds() {
        lock.lock();
        try {
            return bounds;
        } finally {
            lock.unlock();
        }
    }
}
