package pcd.poool.model.balls;

import pcd.poool.model.base.Vector;
import pcd.poool.model.board.Board;
import pcd.poool.model.board.Boundary;

public abstract class BallImpl implements Ball {
    private Vector position;
    private Vector velocity;
    private double radius;
    private double mass;

    public BallImpl(Vector position, Vector velocity, double radius, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;
    }

    public Vector getPosition() {
        return position;
    }

    public double getMass() {
        return mass;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public double getRadius() {
        return radius;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void updateState(long dt, Board ctx){
        double speed = this.velocity.abs();
        double dt_scaled = dt*0.001;
        if (speed > 0.001) {
            double FRICTION_FACTOR = 0.25;
            double dec    = FRICTION_FACTOR * dt_scaled;
            double factor = Math.max(0, speed - dec) / speed;
            this.velocity = this.velocity.mul(factor);
        } else {
            this.velocity = new Vector(0,0);
        }
        this.position = this.position.sum(this.velocity.mul(dt_scaled));
        applyBoundaryConstraints(ctx);
    }

    private void applyBoundaryConstraints(Board ctx){
        Boundary bounds = ctx.getBounds();
        Vector pos;
        Vector vel;
        if (this.position.x() + radius > bounds.x1()){
            this.position = new Vector(bounds.x1() - radius, this.position.y());
            this.velocity = this.velocity.getSwappedX();
        } else if (this.position.x() - radius < bounds.x0()){
            this.position = new Vector(bounds.x0() + radius, this.position.y());
            this.velocity = this.velocity.getSwappedX();
        } else if (this.position.y() + radius > bounds.y1()){
            this.position = new Vector(this.position.x(), bounds.y1() - radius);
            this.velocity = this.velocity.getSwappedY();
        } else if (this.position.y() - radius < bounds.y0()){
            this.position = new Vector(this.position.x(), bounds.y0() + radius);
            this.velocity = this.velocity.getSwappedY();
        }
    }
}
