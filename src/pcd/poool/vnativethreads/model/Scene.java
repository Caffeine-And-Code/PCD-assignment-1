package pcd.poool.vnativethreads.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class Scene {

    private final Board board;
    private final ReentrantLock lock = new ReentrantLock(true);
    private int humanScore;
    private int botScore;
    private GameStatus status;
    private int fps;
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

    public void updateBoardPhysics(Function<Board, Integer> physics) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (status != GameStatus.RUNNING) return;
            var boardCopy = board.copy();
            this.fps = physics.apply(boardCopy);
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
            updateBoard((Board board) ->
                    board.setBalls(nextBalls)
                            .setPlayerBall(nextPlayer)
                            .setBotBall(nextBot)
            );
            updateStatus();
            notifyObservers();
        } finally {
            lock.unlock();
        }
    }

    public void updateBoard(Consumer<Board> updateBoard) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            updateBoard.accept(board);
            notifyObservers();
        } finally {
            lock.unlock();
        }
    }

    public Board getBoard() {
        lock.lock();
        try {
            return board;
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

    public int getHumanScore() {
        lock.lock();
        try {
            return humanScore;
        } finally {
            lock.unlock();
        }
    }

    public int getBotScore() {
        lock.lock();
        try {
            return botScore;
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
        return getStatus() != GameStatus.RUNNING;
    }

    public int getFps() {
        return this.fps;
    }

}
