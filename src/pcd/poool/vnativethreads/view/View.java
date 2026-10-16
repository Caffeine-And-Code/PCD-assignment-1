package pcd.poool.vnativethreads.view;

import pcd.poool.vnativethreads.controller.PooolController;
import pcd.poool.vnativethreads.model.BoardObserver;
import pcd.poool.vnativethreads.model.Scene;

public class View implements BoardObserver {

    private final ViewFrame frame;
    private final ViewModel viewModel;

    public View(ViewModel model, int w, int h, PooolController controller) {
        frame = new ViewFrame(model, w, h, controller);
        frame.setVisible(true);
        this.viewModel = model;
    }

    @Override
    public synchronized void modelUpdate(Scene scene) {
        viewModel.update(scene);
        frame.render();
    }
}
