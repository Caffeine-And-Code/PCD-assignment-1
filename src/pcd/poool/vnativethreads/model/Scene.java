package pcd.poool.vnativethreads.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Scene {

    private final Board board;
    private final ReentrantLock lock = new ReentrantLock(true);
    private int humanScore;
    private int botScore;
    private GameStatus status;
    private int fps;

    @FunctionalInterface
    public interface PhysicsUpdate {
        int apply(Board board) throws InterruptedException;
    }

    public record Snapshot(Board board, int humanScore, int botScore, GameStatus status, int fps) {}

    public Snapshot snapshot() {
        lock.lock();
        try { return new Snapshot(getBoard(), humanScore, botScore, status, fps); }
        finally { lock.unlock(); }
    }

    private final List<BoardObserver> observers = new ArrayList<>();

    public Scene() {
        humanScore = 0;
        botScore = 0;
        status = GameStatus.RUNNING;
        board = new Board();
    }

    public void initBoard(BoardConf conf) {
        lock.lock();
        try {
            board.init(conf);
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

    private void notifyObservers() {
        for (var observer : observers) observer.modelUpdate(this);
    }

    public void updateBoardPhysics(PhysicsUpdate physics) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (isStopped()) return;
            var boardCopy = board.copy();
            int nextFps = physics.apply(boardCopy);
            if (isStopped() || Thread.currentThread().isInterrupted()) return;
            this.fps = nextFps;
            var nextBalls = new ArrayList<Ball>();
            for (var ball : boardCopy.getBalls()) {
                if (board.isInHole(ball)) {
                    if (ball.getLastHitBy() == BallType.PLAYER) humanScore++;
                    if (ball.getLastHitBy() == BallType.BOT) botScore++;
                } else {
                    nextBalls.add(ball.copy());
                }
            }
            var nextPlayer = boardCopy.getPlayerBall();
            var nextBot = boardCopy.getBotBall();
            board.setBalls(nextBalls).setPlayerBall(nextPlayer).setBotBall(nextBot);
            updateStatus();
            notifyObservers();
        } finally {
            lock.unlock();
        }
    }

    public void updateBoard(Consumer<Board> updateBoard) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (isStopped()) return;
            updateBoard.accept(board);
            notifyObservers();
        } finally {
            lock.unlock();
        }
    }

    public Board getBoard() {
        lock.lock();
        try {
            return board.copy();
        } finally {
            lock.unlock();
        }
    }

    public void updateStatus() {
        lock.lock();
        try {
            boolean humanInHole = board.isInHole(board.getPlayerBall());
            boolean botInHole = board.isInHole(board.getBotBall());
            if (humanInHole && botInHole) status = GameStatus.DRAW;
            else if (humanInHole) status = GameStatus.BOT_WON;
            else if (botInHole) status = GameStatus.HUMAN_WON;
            else if (board.getBalls().isEmpty()) {
                if (humanScore > botScore) status = GameStatus.HUMAN_WON;
                else if (botScore > humanScore) status = GameStatus.BOT_WON;
                else status = GameStatus.DRAW;
            }
        } finally {
            lock.unlock();
        }
    }

    public GameStatus getStatus() {
        lock.lock();
        try {
            return status;
        } finally {
            lock.unlock();
        }
    }

    public boolean isGameOver() {
        var current = getStatus();
        return current != GameStatus.RUNNING && current != GameStatus.IN_EXCEPTION;
    }

    public boolean isStopped() {
        return getStatus() != GameStatus.RUNNING;
    }

    public void setInException() {
        lock.lock();
        try { status = GameStatus.IN_EXCEPTION; }
        finally { lock.unlock(); }
    }

    public void setInExceptionIfRunning() {
        lock.lock();
        try {
            if(status == GameStatus.RUNNING)
                status = GameStatus.IN_EXCEPTION;
        }
        finally { lock.unlock(); }
    }
}
