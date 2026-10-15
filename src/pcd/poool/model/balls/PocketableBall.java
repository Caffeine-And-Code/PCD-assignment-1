package pcd.poool.model.balls;

import pcd.poool.model.base.Vector;

public class PocketableBall extends BallImpl {
    private final int id;
    private Ball lastToucher;

    public PocketableBall(int id, Vector position, Vector velocity, double radius, double mass) {
        super(position, velocity, radius, mass);
        this.id = id;
        this.lastToucher = null;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    public Ball getLastToucher() {
        return lastToucher;
    }

    public void setLastToucher(Ball lastToucher) {
        this.lastToucher = lastToucher;
    }
}