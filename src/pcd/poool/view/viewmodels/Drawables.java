package pcd.poool.view.viewmodels;

import pcd.poool.model.balls.Ball;

import java.util.List;

public record Drawables(List<RenderableBall> smallBalls, RenderableBall player, RenderableBall bot) {

    public static Drawables FromModels(List<Ball> smallBalls, Ball player, Ball bot) {
        return new Drawables(smallBalls.stream().map(RenderableBall::FromModel).toList(), RenderableBall.FromModel(player), RenderableBall.FromModel(bot));
    }
}
