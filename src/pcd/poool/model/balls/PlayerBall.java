package pcd.poool.model.balls;

import pcd.poool.model.base.Vector;
import pcd.poool.model.board.Board;
import pcd.poool.model.player.PlayerType;

public class PlayerBall extends BallImpl {
    private final PlayerType owner;

    public PlayerBall(Vector position, Vector velocity, double radius, double mass, PlayerType owner) {
        super(position, velocity, radius, mass);
        this.owner = owner;
    }

    @Override
    public String getId() {
        if (owner == PlayerType.PLAYER) {
            return "H";
        } else {
            return "B";
        }
    }
}
