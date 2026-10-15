package pcd.poool.view.ui;

import pcd.poool.view.View;
import pcd.poool.view.viewmodels.RenderFrame;

import javax.swing.*;
import java.awt.*;

public class RendererPanel extends JPanel {
    private final View view;
    private volatile RenderFrame frame;

    public RendererPanel(int w, int h, View view) {
        this.view = view;
        setPreferredSize(new Dimension(w, h));
    }

    public void show(RenderFrame f) {
        this.frame = f;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var f = frame;
        if (f == null) {
            return;
        }

        var g2 = (Graphics2D) g;
        view.paint(g2, f.drawables());
        view.paintStats(g2, f.fps(), f.drawables().smallBalls().size());
    }
}
