package pcd.poool.model;

import pcd.poool.model.base.Vector;

public interface Ball {
    Vector getPosition();

    double getMass();

    double getRadius();

    Vector getVelocity();

    void setPosition(Vector position);

    void setMass(double mass);

    void setRadius(double radius);

    void setVelocity(Vector velocity);
}
