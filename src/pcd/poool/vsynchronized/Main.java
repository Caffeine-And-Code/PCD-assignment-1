package pcd.poool.vsynchronized;

import pcd.poool.vsynchronized.controller.PhysicsLoop;
import pcd.poool.vsynchronized.controller.PooolController;
import pcd.poool.vsynchronized.model.Board;
import pcd.poool.vsynchronized.model.MassiveBoardConf;
import pcd.poool.vsynchronized.view.View;
import pcd.poool.vsynchronized.view.ViewModel;

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
