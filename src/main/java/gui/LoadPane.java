package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import console.Console;

/**
 * A controller for the buttons to add
 * a critter and load a world in the GUI.
 * 
 */
public class LoadPane {

	private Main m;
	private Console c;
	Stage stage;

	public LoadPane(Main m, Stage stage) {
		this.stage = stage;
		this.m = m;
		c = new Console();
		c.newWorld();
		m.setWorld(c.getWorld());
		updateGUI();
		m.getLoadWorldButton().setOnAction(new LoadWorldClick());
		m.getAddCrittersButton().setOnAction(new AddCrittersClick());
		m.getAddCritterButton().setOnAction(new AddCritterClick());
	}

	/**
	 * Update the GUI. This updates the world
	 * view and world info pane.
	 */
	public void updateGUI() {
		m.getWorldPane().update();
		m.getWorldInfoPane().update();
	}

	/**
	 * Updates the world
	 */
	class LoadWorldClick implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent ae) {
			FileChooser fC = new FileChooser();
			fC.setTitle("Open File");
			File f = fC.showOpenDialog(stage);
			if (f == null) {
				System.err.println("Couldn't load file");
				return;
			}
			String worldFileName = f.toString();
			c.loadWorld(worldFileName);
			m.setWorld(c.getWorld());
			m.getCanvas().setHeight(Math.round(((m.getWorld().getRows()-0.5*m.getWorld().getCols())*Math.sqrt(3)+1)*m.getWorldPane().getHexRadius()));
			m.getCanvas().setWidth(Math.round(m.getWorld().getCols()*3.0/2 + 0.5)*m.getWorldPane().getHexRadius());
			updateGUI();
		}
	}

		/**
		 * Adds critters to the world
		 */
		class AddCrittersClick implements EventHandler<ActionEvent>{
			@Override
			public void handle(ActionEvent ae) {
				FileChooser cFC = new FileChooser();
				cFC.setTitle("Open File");
				File cF = cFC.showOpenDialog(stage);
				if (cF == null) {
					System.err.println("Couldn't load file");
					return;
				}
				String critterFileName = cF.toString();
				int numCritters = Integer.parseInt(m.getNumCritters().getText());
				c.loadCritters(critterFileName, numCritters);
				m.setWorld(c.getWorld());
				updateGUI();
			}
		}

		/**
		 * Adds a critter to a specific location
		 */
		class AddCritterClick implements EventHandler<ActionEvent>{
			@Override
			public void handle(ActionEvent ae) {
				try {
					FileChooser cFC = new FileChooser();
					cFC.setTitle("Open File");
					File cF = cFC.showOpenDialog(stage);
					if (cF == null) {
						System.err.println("Couldn't load file");
						return;
					}
					String critterFileName = cF.toString();
					int col = m.getColumn();
					int row = m.getRow();
					c.loadCritterFile(new FileReader(critterFileName), c.getWorld().getHexAt(col, row), 0);
					m.setWorld(c.getWorld());
					updateGUI();
				} catch (FileNotFoundException e) {
					System.err.println("File not found");
				} catch (IOException e) {
					System.err.println("Error adding critter");
				}
			}
		}
	}