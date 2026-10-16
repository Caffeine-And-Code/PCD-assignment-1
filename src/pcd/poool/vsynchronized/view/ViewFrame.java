package pcd.poool.vsynchronized.view;

import pcd.poool.vsynchronized.controller.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewFrame extends JFrame {

    private final VisualiserPanel panel;
    private final ViewModel model;
    private final PooolController controller;

    public ViewFrame(ViewModel model, int w, int h, PooolController controller) {
        this.controller = controller;
        this.model = model;
        setTitle("Poool");
        setSize(w, h + 25);
        setResizable(false);
        panel = new VisualiserPanel(w, h);
        getContentPane().add(panel);
        bind("UP", new GoUpCommand());
        bind("W", new GoUpCommand());
        bind("DOWN", new GoDownCommand());
        bind("S", new GoDownCommand());
        bind("LEFT", new GoLeftCommand());
        bind("A", new GoLeftCommand());
        bind("RIGHT", new GoRightCommand());
        bind("D", new GoRightCommand());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                controller.interrupt();
            }

            public void windowClosed(WindowEvent ev) {
                controller.interrupt();
            }
        });
    }

    private void bind(String key, Command command) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        panel.getActionMap().put(key, new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                controller.notifyNewCommand(command);
            }
        });
    }

    public void render() {
        panel.repaint();
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


        private void paintBall(BallViewInfo ball, Graphics2D g) {
            int x = (int) (ox + ball.pos().x() * delta);
            int y = (int) (oy - ball.pos().y() * delta);
            int radius = (int) (ball.radius() * delta);
            g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
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
            for (var b : model.getBalls().stream().filter((BallViewInfo ball) -> !ball.disabled()).toList()) {
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

            var botGoal = model.getBotGoal();
            if (botGoal != null) {
                paintBall(botGoal, g2);
            }

            var playerGoal = model.getPlayerGoal();
            if (playerGoal != null) {
                paintBall(playerGoal, g2);
            }

            g2.setStroke(new BasicStroke(1));
            g2.drawString("Num small balls: " + model.getBalls().size(), 20, 40);
            g2.drawString("Frame per sec: " + model.getFramePerSec(), 20, 60);

            g2.setFont(new Font("Arial", Font.PLAIN, 84));

            g2.setPaint(Color.BLUE);
            g2.drawString("H " + String.valueOf(model.getBotScore()), getWidth() / 7, (getHeight() / 4) * 3);
            g2.drawString("B " + String.valueOf(model.getPlayerScore()), getWidth() / 6 * 5, (getHeight() / 4) * 3);
            g2.setPaint(Color.BLACK);
        }

    }
}
