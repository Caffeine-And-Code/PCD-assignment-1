package pcd.sketch02;

import pcd.sketch02.controller.ActiveController;
import pcd.sketch02.model.AutonomousUpdater;
import pcd.sketch02.model.Counter;
import pcd.sketch02.view.View;
import pcd.sketch02.view.ViewModel;

public class Sketch02 {


	static void main() {

		var model = new Counter(0);
		
		var controller = new ActiveController(model);				
		
		var viewModel = new ViewModel(model.getCount());
		var view = new View(viewModel, controller);
		model.addObserver(view);
		
		controller.start();
		
		var updater = new AutonomousUpdater(model);
		updater.start();		
		
		view.display();

	}
	
}
