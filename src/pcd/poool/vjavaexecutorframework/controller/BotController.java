package pcd.poool.vjavaexecutorframework.controller;

import pcd.poool.vjavaexecutorframework.model.Scene;
import pcd.poool.vjavaexecutorframework.model.V2d;

import java.util.Random;

import java.util.function.Consumer;

public class BotController extends Thread {
    private static final long THINK_TIME = 700;
    private final Scene scene;
    private final Consumer<Throwable> onTermination;
    private final Random random = new Random();

    public BotController(Scene scene, Consumer<Throwable> onTermination) {
        super("poool-bot");
        this.scene = scene;
        this.onTermination = onTermination;
    }

    @Override
    public void run() {
        Throwable failure = null;
        try {
            while (!isInterrupted() && !scene.isStopped() && !scene.isGameOver()) {
                Thread.sleep(THINK_TIME);
                if (scene.isStopped() || scene.isGameOver()) return;
                var boardCopy = scene.getBoard();
                var balls = boardCopy.getBalls();
                if (balls.isEmpty()) return;

                var bot = boardCopy.getBotBall();
                var target = balls.get(random.nextInt(balls.size()));
                double dx = target.getPos().x() - bot.getPos().x();
                double dy = target.getPos().y() - bot.getPos().y();
                double distance = Math.hypot(dx, dy);
                if (distance > 1e-6) {
                    scene.updateBoard(board ->
                            board.kickBot(new V2d(
                            Command.UPDATE_STEP * dx / distance,
                            Command.UPDATE_STEP * dy / distance)));
                }
            }
        } catch (InterruptedException ex) {
            interrupt();
        } catch (Exception ex) {
            failure = ex;
            throw ex;
        } finally {
            onTermination.accept(failure);
        }
    }
}
