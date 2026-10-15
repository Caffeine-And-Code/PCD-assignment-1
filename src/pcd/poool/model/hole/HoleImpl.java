package pcd.poool.model.hole;

import pcd.poool.model.base.Vector;
import pcd.poool.model.player.PlayerType;

public class HoleImpl implements Hole{
    private final Vector position;
    private final double radius;
    private final PlayerType owner;

    public HoleImpl(Vector position, double radius, PlayerType owner) {
        this.position = position;
        this.radius = radius;
        this.owner = owner;
    }

    @Override
    public Vector getPosition() {
        return this.position;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public PlayerType getOwner() {
        return this.owner;
    }
}
