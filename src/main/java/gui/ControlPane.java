package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * A controller for the buttons that let a user
 * control the world.
 *
 */
public class ControlPane {

	private Main m;
	private final long MAX_FPS = 34L; // 1/30 of a second, or about 33.3 ms
	private boolean isRunning = false;
	private Timeline timeline;
	private ImageView play, pause, step;
	
	public ControlPane(Main m) {
		this.m = m;
		loadImages();
		m.getUpdateSpeedButton().setOnAction(new UpdateSpeedClick());
		m.getStepButton().setOnAction(new StepClick());
		m.getPlayButton().setOnAction(new PlayClick());
		m.getStepButton().setGraphic(step);
		m.getPlayButton().setGraphic(play);
	}

	private void updateTimeline(int speed) {
		timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(MAX_FPS), 
						new EventHandler<ActionEvent>() {
			// for world view updates (redrawing)
			@Override
			public void handle(ActionEvent arg0) {
				updateGUI();
			}
			
		}));
		
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(speed), new EventHandler<ActionEvent>() {
			// for world model updates
			@Override
			public void handle(ActionEvent arg0) {
				m.getWorld().advanceTime();
			}
		}));
		
		timeline.setCycleCount(Timeline.INDEFINITE);
	}
	
	private void loadImages() {
		play = new ImageView(new Image(getClass().getResourceAsStream(
				"play.png")));
		pause = new ImageView(new Image(getClass().getResourceAsStream(
				"stop.png")));
		step = new ImageView(new Image(getClass().getResourceAsStream(
				"step.png")));
	}
	/**
	 * Update the GUI. This updates the
	 * world view and world info pane.
	 * 
	 * TODO make this update the hex info pane
	 */
	public void updateGUI() {
		m.getWorldPane().update();
		m.getWorldInfoPane().update();
		
	}
	
	/**
	 * Set whether the user can use the step button,
	 * update speed button, speed box, load world
	 * button, and add critter button.
	 * 
	 * @param b: true to disable all of the above,
	 * false otherwise.
	 */
	public void disableControls(boolean b) {
		m.getStepButton().setDisable(b);
		m.getUpdateSpeedButton().setDisable(b);
		m.getSpeedBox().setDisable(b);
		m.getLoadWorldButton().setDisable(b);
		m.getAddCrittersButton().setDisable(b);
	}
	
	/**
	 * Handler for updating the speed.
	 * A speed update that's less than 1 updates to 0,
	 * the max speed is 100,
	 * and other bad inputs don't change the speed.
	 */
	class UpdateSpeedClick implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent ae) {
			int newSpeed;
			try {
				newSpeed = Integer.parseInt(
						m.getSpeedBox().getText());
				if(newSpeed <= 0) newSpeed = 0;
				if(newSpeed >= 100) newSpeed = 100;
				m.setSpeed(newSpeed);
				m.getWorldInfoPane().update();
			} catch (NumberFormatException e) {
				// do nothing
			} finally {
				// clear the textbox
				m.getSpeedBox().setText("");
			}
			
		}
	}

	/**
	 * Handler for clicking the step button (single time advance)
	 */
	class StepClick implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent ae) {
			long begin = System.currentTimeMillis();
			m.getWorld().advanceTime();
			updateGUI();
			long end = System.currentTimeMillis();
			System.out.println("Time per update: " + (end - begin));
		}
		
	}
	
	/**
	 * Handler for clicking the play button (continuous run)
	 */
	class PlayClick implements EventHandler<ActionEvent> {
		
		public void handle(ActionEvent ae) {
			if(isRunning) {
				// stop
				timeline.stop();
				timeline = null;
				m.getPlayButton().setText("Play");
				m.getPlayButton().setGraphic(play);
				isRunning = false;

				// allow speed updates, stepping, world load,
				// and critter add
				disableControls(false);
			} else {
				// go
				if(m.getSpeed() != 0) {
					// interpreting speed of 0 as no motion
					m.getPlayButton().setGraphic(pause);
					updateTimeline(101-m.getSpeed());
					timeline.play();
					isRunning = true;
					
					// disable controls for speed updates, stepping,
					// world load, and critter add
					disableControls(true);
				}
					
			}
					
		}
	}
	
}
