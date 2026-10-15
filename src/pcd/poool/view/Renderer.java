package pcd.poool.view;

import pcd.poool.view.viewmodels.Drawables;
import pcd.poool.view.viewmodels.RenderableBall;

import java.awt.*;

public class Renderer implements View {
    private final int halfW, halfH, delta;

    public Renderer(int w, int h) {
        halfW = w / 2;
        halfH = h / 2;
        delta = Math.min(halfW, halfH);
    }

    @Override
    public void paint(Graphics2D g, Drawables d) {
        setupAndDrawAxes(g);

        g.setStroke(new BasicStroke(1));
        d.smallBalls().forEach(b -> paintBall(b, g));

        g.setStroke(new BasicStroke(3));
        paintBall(d.player(), g, Color.MAGENTA);
        paintBall(d.bot(), g, Color.GREEN);
    }

    private void paintBall(RenderableBall b, Graphics2D g, Color c) {
        if (b == null) {
            return;
        }

        var old = g.getPaint();
        g.setPaint(c);
        paintBall(b, g);
        g.setPaint(old);
    }

    private void paintBall(RenderableBall b, Graphics2D g) {
        int r = (int) (b.radius() * delta);
        int cx = (int) (halfW + b.pos().x() * delta);
        int cy = (int) (halfH - b.pos().y() * delta);
        g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
    }

    private void setupAndDrawAxes(Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.clearRect(0, 0, halfW * 2, halfH * 2);

        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.drawLine(halfW, 0, halfW, halfH * 2);
        graphics2D.drawLine(0, halfH, halfW * 2, halfH);
        graphics2D.setColor(Color.BLACK);
    }


    @Override
    public void paintStats(Graphics2D graphics2D, int framePerSecond, int nSmallBall) {
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.drawString("Num small balls: ".concat(String.valueOf(nSmallBall)), 20, 40);
        graphics2D.drawString("Frame per sec: ".concat(String.valueOf(framePerSecond)), 20, 60);
    }
}
