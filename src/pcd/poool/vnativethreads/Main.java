package pcd.poool.vnativethreads;

import pcd.poool.vnativethreads.controller.GameLifecycle;
import pcd.poool.vnativethreads.model.MassiveBoardConf;
import pcd.poool.vnativethreads.model.Scene;
import pcd.poool.vnativethreads.view.View;
import pcd.poool.vnativethreads.view.ViewModel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int workerCount = args.length > 0 ? Integer.parseInt(args[0])
                : Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        if (workerCount <= 0) throw new IllegalArgumentException("workers must be positive");
        Scene scene = new Scene();
        scene.initBoard(new MassiveBoardConf());
        var lifecycle = new GameLifecycle(scene, workerCount);
        try {
            SwingUtilities.invokeAndWait(() -> {
                var viewModel = new ViewModel();
                var view = new View(viewModel, 1200, 800,
                        lifecycle.playerController(), lifecycle::stop);
                scene.addObserver(view);
            });
            lifecycle.start();
            // main waits; Swing's EDT remains available to paint and close the window.
            lifecycle.awaitTermination();
        } finally {
            lifecycle.stop();
        }
    }
}
