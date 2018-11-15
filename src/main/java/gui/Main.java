package gui;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import interpret.World;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Main extends Application{

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="play"
	private Button play; // Value injected by FXMLLoader

	@FXML // fx:id="worldBox"
	private ScrollPane worldBox; // Value injected by FXMLLoader

	@FXML // fx:id="hexInfo"
	private TextArea hexInfo; // Value injected by FXMLLoader

	@FXML // fx:id="worldInfo"
	private TextArea worldInfo; // Value injected by FXMLLoader

	@FXML // fx:id="updateSpeed"
	private Button updateSpeed; // Value injected by FXMLLoader

	@FXML // fx:id="step"
	private Button step; // Value injected by FXMLLoader

	@FXML // fx:id="addCritter"
	private Button addCritters; // Value injected by FXMLLoader

	@FXML // fx:id="speedBox"
	private TextField speedBox; // Value injected by FXMLLoader

	@FXML // fx:id="loadWorld"
	private Button loadWorld; // Value injected by FXMLLoader

	@FXML // fx:id="numCritters"
	private TextField critters; // Value injected by FXMLLoader
	
	@FXML // fx:id="column"
	private TextField column; // Value injected by FXMLLoader
	
	@FXML // fx:id="row"
	private TextField row; // Value injected by FXMLLoader
	
	@FXML // fx:id="addCritter
	private Button addCritter;
	
	@FXML
	private Canvas canvas;
	
	private World w;
	private int speed;
	Stage s;
	
	// external controllers
	private WorldInfoPane worldInfoPane;
	private WorldPane worldPane;
	private ControlPane controlPane;
	private HexInfoPane hexInfoPane;
	private LoadPane loadPane;
	
	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert play != null : "fx:id=\"play\" was not injected: check your FXML file 'main.fxml'.";
		assert worldBox != null : "fx:id=\"worldBox\" was not injected: check your FXML file 'main.fxml'.";
		assert hexInfo != null : "fx:id=\"hexInfo\" was not injected: check your FXML file 'main.fxml'.";
		assert worldInfo != null : "fx:id=\"worldInfo\" was not injected: check your FXML file 'main.fxml'.";
		assert updateSpeed != null : "fx:id=\"updateSpeed\" was not injected: check your FXML file 'main.fxml'.";
		assert step != null : "fx:id=\"step\" was not injected: check your FXML file 'main.fxml'.";
		assert addCritters != null : "fx:id=\"addCritter\" was not injected: check your FXML file 'main.fxml'.";
		assert speedBox != null : "fx:id=\"speedBox\" was not injected: check your FXML file 'main.fxml'.";
		assert loadWorld != null : "fx:id=\"loadWorld\" was not injected: check your FXML file 'main.fxml'.";
		assert canvas != null : "fx:id=\"canvas\" was not injected: check your FXML file 'main.fxml'.";
		
		// stop people from editing the boxes
		hexInfo.setEditable(false);
		worldInfo.setEditable(false);
		
		// set up default things
		w = new World(); // load a default world
		speed = 0;
		
		// use additional controllers for code cleanliness
		worldInfoPane = new WorldInfoPane(this);
		worldPane = new WorldPane(this);
		controlPane = new ControlPane(this);
		hexInfoPane = new HexInfoPane(this);
		loadPane = new LoadPane(this, s);

		canvas.setHeight(Math.round(((w.getRows()-0.5*w.getCols())*Math.sqrt(3)+1)*worldPane.getHexRadius()));
		canvas.setWidth(Math.round(w.getCols()*3.0/2 + 0.5)*worldPane.getHexRadius());
		
		
		worldInfoPane.update();
		worldPane.update();
		
		//Test code for file loading
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		s = stage;
		// Copied and modified from Example - Lab09 fxml
		try {
			final URL r = getClass().getResource("main.fxml");
			if (r == null) {
				System.out.println("No FXML resource found.");
				try {
					stop();
				} catch (final Exception e) {}
				return;
			}
			
			// load the FXML from a file
			final Parent node = FXMLLoader.load(r);
			final Scene scene = new Scene(node);
			
			stage.setTitle("Critter World");
			
			// stop users from resizing the window to a size too small
			stage.setMinWidth(660);
			stage.setMinHeight(440);
			
			stage.setScene(scene);
			stage.sizeToScene();
			stage.show();


		} catch (final IOException ioe) {
			System.out.println("Can't load FXML file.");
			ioe.printStackTrace();
			try {
				stop();
			} catch (final Exception e) {}
		}
	}
	
	
	// Getters for components of the window
	
	/**
	 * Get the world this view is looking at.
	 * Since the view is essentially a data structure,
	 * only one thread should be able to operate with
	 * it at a time.
	 * 
	 * @return the world this view is looking at
	 */
	public synchronized World getWorld() {
		return w;
	}
	
	public void setWorld(World wd) {
		w = wd;
	}

	public TextArea getHexInfo() {
		return hexInfo;
	}

	public TextArea getWorldInfo() {
		return worldInfo;
	}

	public TextField getSpeedBox() {
		return speedBox;
	}

	public Button getUpdateSpeedButton() {
		return updateSpeed;
	}
	
	public Button getStepButton() {
		return step;
	}
	
	public Button getPlayButton() {
		return play;
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public Button getLoadWorldButton() {
		return loadWorld;
	}
	
	public Button getAddCrittersButton() {
		return addCritters;
	}
	
	public TextField getNumCritters() {
		return critters;
	}
	
	public int getColumn() {
		return Integer.parseInt(column.getText());
	}
	
	public int getRow() {
		return Integer.parseInt(row.getText());
	}
	
	public Button getAddCritterButton() {
		return addCritter;
	}
	
	// Getters and setters for other important fields
	
	public void setSpeed(int newSpeed) {
		speed = newSpeed;
	}

	public int getSpeed() {
		return speed;
	}
	
	// Getters for sub-controllers
	

	public WorldInfoPane getWorldInfoPane() {
		return worldInfoPane;
	}

	public WorldPane getWorldPane() {
		return worldPane;
	}

	public ControlPane getControlPane() {
		return controlPane;
	}

	public HexInfoPane getHexInfoPane() {
		return hexInfoPane;
	}

	public LoadPane getLoadPane() {
		return loadPane;
	}

}



