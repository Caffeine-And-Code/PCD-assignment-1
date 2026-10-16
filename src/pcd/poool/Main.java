package pcd.poool;

import pcd.poool.controller.PhysicsLoop;
import pcd.poool.controller.PooolController;
import pcd.poool.model.Board;
import pcd.poool.model.MassiveBoardConf;
import pcd.poool.view.View;
import pcd.poool.view.ViewModel;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            board.init(new MassiveBoardConf());
            var controller = new PooolController(board);
            var viewModel = new ViewModel();
            viewModel.update(board, 0);
            var view = new View(viewModel, 1200, 800, controller);
            board.addObserver(view);
            var physics = new PhysicsLoop(board, controller::interrupt);
            controller.setPhysicsLoop(physics);
            physics.start();
            controller.start();
        });
    }
}
