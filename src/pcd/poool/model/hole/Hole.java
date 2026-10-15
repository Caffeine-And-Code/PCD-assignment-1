package pcd.poool.model.hole;

import pcd.poool.model.base.Vector;
import pcd.poool.model.player.PlayerType;

public interface Hole {
    Vector getPosition();
    double getRadius();
    PlayerType getOwner();
}
