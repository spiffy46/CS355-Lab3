package cs355.solution;

import java.awt.Color;

import cs355.GUIFunctions;
import cs355.controller.MyController;
import cs355.model.drawing.MyModel;
import cs355.view.MyView;

/**
 * This is the main class. The program starts here.
 * Make you add code below to initialize your model,
 * view, and controller and give them to the app.
 */
public class CS355 {

	/**
	 * This is where it starts.
	 * @param args = the command line arguments
	 */
	public static void main(String[] args) {

		MyModel model = new MyModel();
		MyController controller = new MyController();
		MyView view = new MyView();
		controller.setModel(model);
		view.setModel(model);
		view.setController(controller);
		
		// Fill in the parameters below with your controller and view.
		GUIFunctions.createCS355Frame(controller, view);
		GUIFunctions.changeSelectedColor(Color.white);
		GUIFunctions.setHScrollBarMin(0);
		GUIFunctions.setHScrollBarMax(2048);
		GUIFunctions.setVScrollBarMin(0);
		GUIFunctions.setVScrollBarMax(2048);
		GUIFunctions.refresh();
	}
}
