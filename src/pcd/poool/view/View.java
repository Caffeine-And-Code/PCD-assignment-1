package pcd.poool.view;

import pcd.poool.controller.PooolController;
import pcd.poool.model.Board;
import pcd.poool.model.BoardObserver;

public class View implements BoardObserver {

	private final ViewFrame frame;
	private final ViewModel viewModel;
	
	public View(ViewModel model, int w, int h, PooolController controller) {
		frame = new ViewFrame(model, w, h, controller);
		frame.setVisible(true);
		this.viewModel = model;
	}
		
	public void render() {
		frame.render();
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}

    @Override
    public synchronized void modelUpdate(Board board) {
        viewModel.update(board);
        frame.render();
    }
}
