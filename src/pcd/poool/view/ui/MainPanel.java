package pcd.poool.view.ui;

import pcd.poool.view.Renderer;
import pcd.poool.view.viewmodels.RenderFrame;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

public class MainPanel extends JFrame {

    private final RendererPanel panel;

    public MainPanel(int w, int h, Renderer renderer) {
        setTitle("Poool");
        setSize(w, h + 25);
        setResizable(false);
        panel = new RendererPanel(w, h, renderer);
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

    public void render(RenderFrame f) throws InterruptedException {
        panel.show(f);
        try {
            SwingUtilities.invokeAndWait(
                    () -> panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight())
            );
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
