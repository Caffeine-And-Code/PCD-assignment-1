package pcd.poool.vnativethreads;

import pcd.poool.vnativethreads.controller.BotPlayer;
import pcd.poool.vnativethreads.controller.PhysicsLoop;
import pcd.poool.vnativethreads.controller.PooolController;
import pcd.poool.vnativethreads.model.MassiveBoardConf;
import pcd.poool.vnativethreads.model.Scene;
import pcd.poool.vnativethreads.view.View;
import pcd.poool.vnativethreads.view.ViewModel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int workerCount = args.length > 0 ? Integer.parseInt(args[0])
                : Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        if (workerCount <= 0) throw new IllegalArgumentException("workers must be positive");
        SwingUtilities.invokeLater(() -> {
            Scene scene = new Scene();
            scene.initBoard(new MassiveBoardConf());
            var controller = new PooolController(scene);
            var viewModel = new ViewModel();
            var view = new View(viewModel, 1200, 800, controller);
            scene.addObserver(view);
            var physics = new PhysicsLoop(scene, workerCount);
            var bot = new BotPlayer(scene);
            controller.setGameThreads(physics, bot);
            physics.start();
            bot.start();
            controller.start();
        });
    }
}
