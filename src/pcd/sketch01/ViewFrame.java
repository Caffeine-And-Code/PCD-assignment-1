package pcd.sketch01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewFrame extends JFrame {

    private final VisualiserPanel panel;
    private final ViewModel model;
    private final RenderSynch sync;

    public ViewFrame(ViewModel model, int w, int h) {
        this.model = model;
        this.sync = new RenderSynch();
        setTitle("Sketch 03");
        setSize(w, h + 25);
        setResizable(false);
        panel = new VisualiserPanel(w, h);
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }

            public void windowClosed(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    public void render() {
        long nf = sync.nextFrameToRender();
        panel.repaint();
        try {
            sync.waitForFrameRendered(nf);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public class VisualiserPanel extends JPanel {
        private final int ox;
        private final int oy;
        private final int delta;

        public VisualiserPanel(int w, int h) {
            setSize(w, h + 25);
            ox = w / 2;
            oy = h / 2;
            delta = Math.min(ox, oy);
        }

        private void paintBall(BallViewInfo b, Graphics2D g2) {
            var p = b.pos();
            int x0 = (int) (ox + p.x() * delta);
            int y0 = (int) (oy - p.y() * delta);
            int radiusX = (int) (b.radius() * delta);
            int radiusY = (int) (b.radius() * delta);
            g2.drawOval(x0 - radiusX, y0 - radiusY, radiusX * 2, radiusY * 2);
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, this.getWidth(), this.getHeight());

            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(ox, 0, ox, oy * 2);
            g2.drawLine(0, oy, ox * 2, oy);
            g2.setColor(Color.BLACK);

            /* Draw Small Balls */
            g2.setStroke(new BasicStroke(1));
            for (var b : model.getBalls()) {
                paintBall(b, g2);
            }

            /* Draw Player and Bot */
            g2.setStroke(new BasicStroke(3));
            var pb = model.getPlayerBall();
            if (pb != null) {
                g2.setPaint(Color.MAGENTA);
                paintBall(pb, g2);
                g2.setPaint(Color.BLACK);
            }

            var pBot = model.getBotBall();
            if (pBot != null) {
                g2.setPaint(Color.GREEN);
                paintBall(pBot, g2);
                g2.setPaint(Color.BLACK);
            }

            g2.setStroke(new BasicStroke(1));
            g2.drawString("Num small balls: " + model.getBalls().size(), 20, 40);
            g2.drawString("Frame per sec: " + model.getFramePerSec(), 20, 60);

            sync.notifyFrameRendered();
        }

    }
}
