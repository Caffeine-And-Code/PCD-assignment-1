package pcd.poool.vjavaexecutorframework.view;

import pcd.poool.vjavaexecutorframework.controller.*;
import pcd.poool.vjavaexecutorframework.model.GameStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewFrame extends JFrame {

    private final VisualiserPanel panel;
    private final ViewModel model;
    private final PlayerController controller;

    public ViewFrame(ViewModel model, int w, int h, PlayerController controller, Runnable onClose) {
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
                onClose.run();
            }

            public void windowClosed(WindowEvent ev) {
                onClose.run();
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
            setBackground(Color.WHITE);
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

        private void paintPlayer(BallViewInfo ball, String label, Graphics2D g) {
            paintBall(ball, g);
            int x = (int) (ox + ball.pos().x() * delta);
            int y = (int) (oy - ball.pos().y() * delta);
            var metrics = g.getFontMetrics();
            g.drawString(label, x - metrics.stringWidth(label) / 2,
                    y + (metrics.getAscent() - metrics.getDescent()) / 2);
        }

        private void paintHole(HoleViewInfo hole, Graphics2D g) {
            int x = (int) (ox + hole.center().x() * delta);
            int y = (int) (oy - hole.center().y() * delta);
            int radius = (int) (hole.radius() * delta);
            g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }

        private void paintScore(String score, int x, int y, Graphics2D g) {
            var previousFont = g.getFont();
            g.setFont(previousFont.deriveFont(Font.PLAIN, 72f));
            g.setColor(Color.BLUE);
            g.drawString(score, x - g.getFontMetrics().stringWidth(score) / 2, y);
            g.setFont(previousFont);
            g.setColor(Color.BLACK);
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

            g2.setColor(Color.BLACK);
            for (var hole : model.getHoles()) {
                paintHole(hole, g2);
            }

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
                paintPlayer(pb, "H", g2);
            }

            var pBot = model.getBotBall();
            if (pBot != null) {
                paintPlayer(pBot, "B", g2);
            }

            paintScore(Integer.toString(model.getHumanScore()), getWidth() / 8, oy + delta / 2, g2);
            paintScore(Integer.toString(model.getBotScore()), getWidth() * 7 / 8, oy + delta / 2, g2);

            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("Num small balls: " + model.getBalls().size(), 80, 40);
            g2.drawString("Frame per sec: " + model.getFramePerSec(), 80, 60);
            g2.setColor(Color.BLACK);

            if (model.getStatus() != GameStatus.RUNNING) {
                String message = switch (model.getStatus()) {
                    case HUMAN_WON -> "Player wins";
                    case BOT_WON -> "Bot wins";
                    case DRAW -> "Draw";
                    default -> "";
                };
                var previousFont = g2.getFont();
                g2.setFont(previousFont.deriveFont(Font.BOLD, 36f));
                g2.drawString(message,
                        ox - g2.getFontMetrics().stringWidth(message) / 2,
                        oy - delta / 2);
                g2.setFont(previousFont);
            }

        }

    }
}
