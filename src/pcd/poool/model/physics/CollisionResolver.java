package pcd.poool.model.physics;

import pcd.poool.model.balls.Ball;
import pcd.poool.model.base.Vector;

public class CollisionResolver {

    public static void resolveCollision(Ball a, Ball b) {

        /*
         * Collision case - what to do:
         *
         * 1) solve overlaps, moving balls
         * 2) update velocities
         *
         */
        double RESTITUTION_FACTOR = 1;

        DistanceData data = CollisionDetector.computeDistance(
                a.getPosition(),
                b.getPosition(),
                a.getRadius(),
                b.getRadius());
        double dx   = data.dx();
        double dy   = data.dy();
        double dist = data.dist();
        double minD = data.minD();

        /* dvn = V2d(nx,ny) = dv unit vector */

        double nx = dx / dist;
        double ny = dy / dist;

        /*
         *
         * Update positions to solve overlaps, moving balls along dvn
         * - the displacements is proportional to the mass
         *
         */
        double overlap = minD - dist;
        double totalM  = a.getMass() + b.getMass();

        double a_factor = overlap * (b.getMass() / totalM);
        double a_deltax = nx * a_factor;
        double a_deltay = ny * a_factor;

        a.setPosition(new Vector(
                a.getPosition().x() - a_deltax,
                a.getPosition().y() - a_deltay));

        double b_factor = overlap * (a.getMass() / totalM);
        double b_deltax = nx * b_factor;
        double b_deltay = ny * b_factor;

        b.setPosition(new Vector(
                b.getPosition().x() + b_deltax,
                b.getPosition().y() + b_deltay));

        /* Update velocities  */

        /* relative speed along the normal vector*/

        double dvx = b.getVelocity().x() - a.getVelocity().x();
        double dvy = b.getVelocity().y() - a.getVelocity().y();
        double dvn = dvx * nx + dvy * ny;

        if (dvn <= 0) { /* if not already separating, update velocities */

            double imp = -(1 + RESTITUTION_FACTOR) * dvn / (1.0/a.getMass() + 1.0/b.getMass());
            a.setVelocity(new Vector(
                a.getVelocity().x() - (imp / a.getMass()) * nx,
                a.getVelocity().y() - (imp / a.getMass()) * ny));
            b.setVelocity(new Vector(
                b.getVelocity().x() + (imp / b.getMass()) * nx,
                b.getVelocity().y() + (imp / b.getMass()) * ny));
        }
    }

}
