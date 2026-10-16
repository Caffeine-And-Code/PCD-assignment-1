package pcd.poool.vnativethreads.controller;

import pcd.poool.vnativethreads.model.Scene;
import pcd.poool.vnativethreads.model.V2d;

import java.util.Random;

public class BotPlayer extends Thread {
    private static final long THINK_TIME = 700;
    private final Scene scene;
    private final Random random = new Random();

    public BotPlayer(Scene scene) {
        super("poool-bot");
        this.scene = scene;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted() && !scene.isGameOver()) {
                Thread.sleep(THINK_TIME);
                var board = scene.getBoard();
                var balls = board.getBalls();
                if (balls.isEmpty()) return;

                var bot = board.getBotBall();
                var target = balls.get(random.nextInt(balls.size()));
                double dx = target.getPos().x() - bot.getPos().x();
                double dy = target.getPos().y() - bot.getPos().y();
                double distance = Math.hypot(dx, dy);
                if (distance > 1e-6) {
                    board.kickBot(new V2d(
                            Command.UPDATE_STEP * dx / distance,
                            Command.UPDATE_STEP * dy / distance));
                }
            }
        } catch (InterruptedException ex) {
            interrupt();
        }
    }
}
