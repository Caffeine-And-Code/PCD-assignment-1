package pcd.poool.view.viewmodels;

import pcd.poool.model.Ball;

public record RenderableBall(RenderableVector pos, double radius) {
    public static RenderableBall FromModel(Ball ball) {
        return new RenderableBall(RenderableVector.FromModel(ball.getPosition()), ball.getRadius());
    }
}
