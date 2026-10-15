package pcd.poool.view.viewmodels;

import pcd.poool.model.base.Vector;

public record RenderableVector(double x, double y) {
    public static RenderableVector FromModel(Vector v) {
        return new RenderableVector(v.x(), v.y());
    }
}
