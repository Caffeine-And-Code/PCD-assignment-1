package pcd.poool.model.board;

import pcd.poool.model.balls.Ball;
import pcd.poool.model.balls.PlayerBall;
import pcd.poool.model.balls.PocketableBall;
import pcd.poool.model.hole.Hole;

import java.util.List;

public interface Board {

    void init(BoardConf conf);

    void updateState(long dt);

    List<PocketableBall> getBalls();

    PlayerBall getPlayerBall();

    PlayerBall getBotBall();

    Hole getPlayerHole();

    Hole getBotHole();

    Boundary getBounds();
}
