package gui;

import interpret.Critter;
import interpret.Food;
import interpret.World;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

/**
 * A controller for this GUI's display
 * of the critter world
 *
 */
public class WorldPane {
	private final double SQRT_3 = Math.sqrt(3);
	private Main m; // the main controller
	private GraphicsContext gc;
	private int[] origin; // [x,y] in GUI coordinates
	private int[] lastHex; // last clicked with center [x,y]
	private int hexRadius; // hex side length
	private final Color SELECTED = Color.DARKGREEN;
	private final Color NOT_SELECTED = Color.BEIGE;
	private Image rock, critter, food;
	
	public double getHexRadius() {
		return hexRadius;
	}

	public WorldPane(Main m) {
		this.m = m;
		loadImages();
		gc = m.getCanvas().getGraphicsContext2D();
		hexRadius = 20;
		origin = new int[2];
		origin[0] = 100;
		origin[1] = 400;
		lastHex = null;
		m.getCanvas().setOnMouseClicked(new HexClick());
	}

	/**
	 * Update the appearance of the world.
	 */
	public synchronized void update() {
		// TODO make the updates dependent on the world.

		gc.setStroke(Color.BLACK);
		gc.setFill(NOT_SELECTED);
		gc.setLineWidth(1);

		// erase the existing world
		gc.clearRect(0, 0, m.getCanvas().getWidth(), m.getCanvas().getHeight());

		
		// draw the new world
		drawMap();
		if(lastHex != null) {
			gc.setFill(SELECTED);
			drawHexagon(lastHex[0], lastHex[1], hexRadius);
			drawContents(lastHex[0], lastHex[1]);
		}
		
	}

	/**
	 * Handle a click given mouse coordinates.
	 * @param x: the x-coordinate of the mouse click
	 * @param y: the y-coordinate of the mouse click
	 */
	public void handleClick(int x, int y) {
		if(lastHex != null) {
			// reset the last hex clicked on
			gc.setFill(NOT_SELECTED);
			drawHexagon(lastHex[0], lastHex[1], hexRadius);
			drawContents(lastHex[0], lastHex[1]);
		}

		int[] result = search(x, y);
		
		if(result == null) {
			m.getHexInfo().setText("Click out of bounds");
		} else {
			// hex found, so change its color
			if(lastHex == null) lastHex = new int[2];
				// hex found, so select it
				lastHex[0] = result[0];
				lastHex[1] = result[1];

				gc.setFill(SELECTED);
				drawHexagon(result[0], result[1], hexRadius);
				drawContents(result[0], result[1]);
			
			
			
			// update the hex info box
			m.getHexInfoPane().col = (int)getCol(result[0]);
			m.getHexInfoPane().row = (int)getRow(result[0], result[1]);
			m.getHexInfoPane().update();
		}
	}


	/**
	 * Get the center of the hex found where the mouse clicks.
	 * @param mouseX: The mouse's x-coordinate
	 * @param mouseY: The mouse's y-coordinate
	 * @return an array of 2 ints, one for the hex's center
	 * x coordinate and one for the hex's center y coordinate.
	 * Returns null if the mouse click is out of bounds.
	 */
	public int[] search(int mouseX, int mouseY) {

		int r = m.getWorld().getRows();
		int c = m.getWorld().getCols();
		double y;
		double x;
		// Loops through all tiles to check if 
		// the mouse is within that tile
		for(int i = 0; i < r; i++) {
			for(int j = 0; j < c; j++) {
				if((2*i - j) < 0 || (2*i - j) >= (2*r - c)) {
					continue;
				}
				y = m.getCanvas().getHeight()-((SQRT_3*i + 1)*hexRadius - j*SQRT_3/2*hexRadius);
				x = (j * 3.0/2+1)*hexRadius;
				// each boolean corresponds to a boundary line of each hex.
				boolean upper = (mouseY > y - (hexRadius*SQRT_3/2));
				boolean lower = (mouseY < y + (hexRadius*SQRT_3/2));
				/*
				 * l1: top-right diagonal line
				 * l2: bottom-left diagonal line
				 * l3: top-left diagonal line
				 * l4: bottom-right diagonal line
				 */
				boolean l1 = (y-mouseY > 
				SQRT_3*(mouseX - (x+hexRadius)));
				boolean l2 = (y-mouseY < 
						SQRT_3*(mouseX - (x-hexRadius)));
				boolean l3 = (y-mouseY > 
				-SQRT_3*(mouseX - (x-hexRadius)));
				boolean l4 = (y-mouseY < 
						-SQRT_3*(mouseX - (x+hexRadius)));

				if(upper && lower && l1 && l2 && l3 && l4) {
					// within all 6 bounds, so return a result
					int[] result = new int[2];
					result[0] = (int)Math.round(x);
					result[1] = (int)Math.round(y);
					return result;
				}	
			}
		}

		return null;
	}


	public void drawMap() {
		World w = m.getWorld();
		int r = w.getRows();
		int c = w.getCols();
		
		double x = 0.0;
		double y = 0.0;
		
		for(int i = 0; i < r; i ++) {
			for(int j = 0; j < c; j++) {
				if(2*i-j >= 0 && 2*i-j < 2*r-c) {
					
					x = (j * 3.0/2+1)*hexRadius;
					y = m.getCanvas().getHeight()-((SQRT_3*i + 1)*hexRadius - j*SQRT_3/2*hexRadius);
					
					if(lastHex != null && (int)x == lastHex[0] && (int)y == lastHex[1]) {
						gc.setFill(SELECTED);
						drawHexagon(x, y, hexRadius);
						gc.setFill(NOT_SELECTED);
					} else {
						drawHexagon(x, y, hexRadius);
					}
					
					drawContents(x,y);
				}
			}
		}
	}

	public double getCol(int x) {
		return (double) Math.round(2.0/3*(x/hexRadius-1));
	}

	public double getRow(int x, int y) {
		int h = (int) Math.round(m.getCanvas().getHeight());
		return (double) Math.round((h - y)/(hexRadius*SQRT_3) - SQRT_3/3 + getCol(x)/2);
	}

	/**
	 * Draw a regular hexagon centered at (x,y) with side length r.
	 * @param gc: the graphics context in which to draw this on
	 * @param x: the x-coordinate of the center of the hexagon
	 * @param y: the y-coordinate of the center of the hexagon
	 * @param r: the side length of the hexagon
	 */
	public void drawHexagon(double x, double y, double r) {
		// from top-left corner, rotating clockwise
		double[] xVals = {x-(r/2), x+(r/2), x+r, x+(r/2), x-(r/2), x-r}; 
		double[] yVals = {y-(r*SQRT_3/2),y-(r*SQRT_3/2),y,y+(r*SQRT_3/2),y+(r*SQRT_3/2),y};
		gc.strokePolygon(xVals, yVals, 6);
		gc.fillPolygon(xVals, yVals, 6);
	}

	/**
	 * Draw a circle centered at (x,y) with radius r.
	 * @param gc: the graphics context in which to draw this on
	 * @param x: the x-coordinate of the center of the circle
	 * @param y: the y-coordinate of the center of the circle
	 * @param r: the radius of the circle
	 */
	public void drawCircle(GraphicsContext gc, int x, int y, int r) {
		gc.strokeOval(x-r,y-r, 2*r, 2*r);
	}

	/**
	 * Draws either a rock, critter, or food in the hex, depending on what is on the tile
	 * @param o
	 * @param x
	 * @param y
	 */
	public synchronized void drawContents(String o, String species, int size, int col, int row, int dir) {
		//TODO: write direction code, rotate image
		//TODO: should we make drawContents take in coordinates relative to the ragged 2d array instead of just raw coordinates relative to the canvas? this can happen elsewhere when this method is called
		//TODO: add functionality to decide what is in the tile (because rock isn't a class). Maybe change World in interpret? for now I'm using string as a parameter
		//TODO: decide on the appropriate size for images of rocks/critters/food  in a hex
		double FIXEDHEIGHT = hexRadius;
		double FIXEDWIDTH = hexRadius;
		double x = (col * 3.0/2+1)*hexRadius;
		double y = m.getCanvas().getHeight()-((SQRT_3*row + 1)*hexRadius - col*SQRT_3/2*hexRadius);
		switch(o) {
		case "Critter":
			gc.save();
			rotate(dir*60,x,y);
			gc.drawImage(critter, x - FIXEDWIDTH/2, y - FIXEDHEIGHT/2, FIXEDWIDTH, FIXEDHEIGHT);
			gc.restore();
			gc.save();
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setLineWidth(.5);
			gc.strokeText(species, x, y - FIXEDHEIGHT/2, hexRadius);
			gc.strokeText(size + "", x, y + hexRadius*.7, hexRadius);
			gc.restore();
			break;
		case "Rock":
			gc.drawImage(rock, x - FIXEDWIDTH/2, y - FIXEDHEIGHT/2, FIXEDWIDTH, FIXEDHEIGHT);
			break;
		case "Food":
			gc.drawImage(food, x - FIXEDWIDTH/2, y - FIXEDHEIGHT/2, FIXEDWIDTH, FIXEDHEIGHT);
			gc.save();
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setLineWidth(.5);
			gc.strokeText(size + "", x, y + hexRadius *.7, hexRadius);
			gc.restore();
			break;
		}
	}
	
	public synchronized void drawContents(double x, double y) {
		int hexCol = (int)getCol((int)x);
		int hexRow = (int)getRow((int)x, (int)y);
		if( ( (2*hexRow)- hexCol > 0) && ( (2*hexRow)- hexCol < (2*m.getWorld().getRows())-m.getWorld().getCols() )) {
			int status = m.getWorld().getHexAt(hexCol, hexRow).getStatus();
			if(status == -1) {
				drawContents("Rock", "",0,hexCol,hexRow, 0);
			} else if (status > 0) {
				Critter c = (Critter) m.getWorld().getHexAt(hexCol, hexRow).getOccupant();
				drawContents("Critter", c.getState().species, c.getState().mem(3), hexCol, hexRow,
						c.getState().facing);
			} else if (status < -1) {
				drawContents("Food", "", ((Food)m.getWorld().getHexAt(hexCol, hexRow).getOccupant()).getValue(), hexCol, hexRow, 0);
			}
		}

		
	}
	
	private void loadImages() {
		critter = new Image(getClass().getResourceAsStream("Critter.png"));
		rock = new Image(getClass().getResourceAsStream("Rock.jpg"));
		food = new Image(getClass().getResourceAsStream("Food.jpg"));
	}
	
	public void rotate(double angle, double pivotx, double pivoty) {
		Rotate r = new Rotate(angle, pivotx, pivoty);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}
	

	/**
	 * Get the center of the hex last clicked on,
	 * in GUI coordinates.
	 * @return the center of the hex last clicked on,
	 * in GUI coordinates [x,y]
	 */
	public int[] getLastHex() {
		return lastHex;
	}
	
	/**
	 * Handler for clicking on the canvas.
	 */
	class HexClick implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent me) {
			handleClick((int)Math.round(me.getX()), 
					(int)Math.round(me.getY()));
		}
	}
}


