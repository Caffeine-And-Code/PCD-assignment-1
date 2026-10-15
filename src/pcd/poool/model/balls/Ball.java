package pcd.poool.model.balls;

import pcd.poool.model.base.Vector;
import pcd.poool.model.board.Board;

public interface Ball {

    public Vector getPosition();

    double getMass();

    double getRadius();

    Vector getVelocity();

    void setPosition(Vector position);

    void setMass(double mass);

    void setRadius(double radius);

    void setVelocity(Vector velocity);

    String getId();

    void updateState(long dt, Board ctx);
}
