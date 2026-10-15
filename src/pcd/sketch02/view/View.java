package pcd.sketch02.view;

import pcd.sketch02.controller.ActiveController;
import pcd.sketch02.model.Counter;
import pcd.sketch02.model.CounterObserver;

import javax.swing.*;

public class View implements CounterObserver {

	private final ViewModel viewModel;

	private final ViewFrame frame;
	
	public View(ViewModel viewModel, ActiveController controller) {
		this.viewModel = viewModel;
		frame = new ViewFrame(viewModel, controller);	
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			frame.setVisible(true);
		});
	}
	
	@Override
	public synchronized void modelUpdated(Counter model) {
		viewModel.update(model.getCount());
		frame.refresh();
	}
}
