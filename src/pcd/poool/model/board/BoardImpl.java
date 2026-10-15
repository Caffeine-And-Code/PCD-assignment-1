package pcd.poool.model.board;

import pcd.poool.model.balls.PlayerBall;
import pcd.poool.model.balls.PocketableBall;
import pcd.poool.model.hole.Hole;
import pcd.poool.model.physics.CollisionResolver;

import java.util.List;

public class BoardImpl implements Board{
    private List<PocketableBall> balls;
    private PlayerBall playerBall;
    private PlayerBall botBall;
    private Boundary bounds;
    private Hole playerHole;
    private Hole botHole;

    public BoardImpl() {
    }

    public void init(BoardConf conf) {
        this.balls = conf.getSmallBalls();
        this.playerBall = BoardConf.getPlayerBall();
        this.botBall = BoardConf.getBotBall();
        this.playerHole = conf.getPlayerHole();
        this.botHole = conf.getBotHole();
        this.bounds = conf.getBoardBoundary();
    }

    public void updateState(long dt) {

        playerBall.updateState(dt, this);
        botBall.updateState(dt, this);

        for (var b: balls) {
            b.updateState(dt, this);
        }

        for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                CollisionResolver.resolveCollision(balls.get(i), balls.get(j));
            }
        }
        for (var b: balls) {
            CollisionResolver.resolveCollision(playerBall, b);
            CollisionResolver.resolveCollision(botBall, b);
        }

    }

    public List<PocketableBall> getBalls(){
        return balls;
    }

    public PlayerBall getPlayerBall() {
        return playerBall;
    }

    public PlayerBall getBotBall() {
        return botBall;
    }

    @Override
    public Hole getPlayerHole() {
        return playerHole;
    }

    @Override
    public Hole getBotHole() {
        return botHole;
    }

    public  Boundary getBounds(){
        return bounds;
    }
}
