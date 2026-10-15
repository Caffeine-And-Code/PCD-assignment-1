package pcd.poool.view;

import pcd.poool.view.viewmodels.Drawables;

import java.awt.*;

public interface View {
    void paint(Graphics2D graphics2D, Drawables drawables);

    void paintStats(Graphics2D graphics2D, int framePerSecond, int nSmallBall);
}
