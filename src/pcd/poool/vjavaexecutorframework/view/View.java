package pcd.poool.vjavaexecutorframework.view;

import pcd.poool.vjavaexecutorframework.controller.PlayerController;
import pcd.poool.vjavaexecutorframework.model.BoardObserver;
import pcd.poool.vjavaexecutorframework.model.Scene;

public class View implements BoardObserver {

    private final ViewFrame frame;
    private final ViewModel viewModel;

    public View(ViewModel model, int w, int h, PlayerController controller, Runnable onClose) {
        frame = new ViewFrame(model, w, h, controller, onClose);
        frame.setVisible(true);
        this.viewModel = model;
    }

    @Override
    public synchronized void modelUpdate(Scene scene) {
        viewModel.update(scene);
        frame.render();
    }
}
