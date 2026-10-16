package pcd.poool.vjavaexecutorframework.model;

public record Hole(P2d center, double radius) {

    public boolean contains(Ball ball) {
        double dx = ball.getPos().x() - center.x();
        double dy = ball.getPos().y() - center.y();
        return dx * dx + dy * dy <= radius * radius;
    }
}
