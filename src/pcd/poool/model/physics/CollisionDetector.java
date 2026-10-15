package pcd.poool.model.physics;

import pcd.poool.model.hole.Hole;
import pcd.poool.model.balls.Ball;
import pcd.poool.model.base.Vector;

public class CollisionDetector{

    public static DistanceData computeDistance(
            Vector positionA,
            Vector positionB,
            double radiusA,
            double radiusB) {
        double dx   = positionB.x() - positionA.x();
        double dy   = positionB.y() - positionA.y();
        double dist = Math.hypot(dx, dy);
        double minD = radiusA + radiusB;
        return new DistanceData(dx, dy, dist, minD);
    }

    private static boolean areShapeColliding(
            Vector positionA,
            Vector positionB,
            double radiusA,
            double radiusB) {
        final double EPSILON = 1e-6;
        DistanceData data = computeDistance(
                positionA,
                positionB,
                radiusA,
                radiusB);
        return data.dist() < data.minD() && data.dist() > EPSILON;
    }

    public static boolean areColliding(Ball a, Ball b) {
        return areShapeColliding(
                a.getPosition(),
                b.getPosition(),
                a.getRadius(),
                b.getRadius());
    }

    public static boolean isInside(Hole a, Ball b) {
        return areShapeColliding(
                a.getPosition(),
                b.getPosition(),
                a.getRadius(),
                b.getRadius());
    }
}
