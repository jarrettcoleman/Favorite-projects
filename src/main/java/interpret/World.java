package interpret;

import java.util.*;

import ast.Action;
import ast.Expr;
import ast.Rule;
import model.Constants;

/**
 * A representation of a critter world.
 */
public class World implements SimObject {
	private ArrayList<Critter> critters;
	private Hex[][] grid; // goes [cols][rows]
	private int cols;
	private int rows;
	private int timeElapsed;
	private static Random r;
	private String name;

	/**
	 * Create a default world of the default size,
	 * no critters, and randomly-placed rocks.
	 */
	public World() {
		this("default", Constants.columns, Constants.rows);
		addRandomRocks(r.nextInt(getCols()) * r.nextInt(getRows()));
	}

	public World(String name, int cols, int rows) {
		
		// initialize
		critters = new ArrayList<Critter>();
		r = new Random();
		this.cols = cols;
		this.rows = rows;

		// create the grid
		grid = new Hex[cols][rows];
		for(int i = 0; i < cols; i++) {
			for(int j = 0; j < rows; j++) {
				grid[i][j] = new Hex(i,j, false, this); 
			}
		}

		// Start invalidating hexes that are off the world

		for (int c = 0; c < grid.length; c++) {
			for (int r = 0; r < grid[c].length; r++) {
				if (((2 * r) - c) < 0)
					grid[c][r] = null;
				if (((2 * r) - c) >= (2 * rows - cols))
					grid[c][r] = null;
			}
		}
	
		timeElapsed = 0;
		this.name = name;
	}
	
	public int getCols() {
		return cols;
	}
	
	public int getRows() {
		return rows;
	}

	public void addCritter(Critter c) {
		critters.add(c);
	}
	
	public void removeCritter(Critter c) {
		critters.remove(c);
	}

	/**
	 * Add random rocks to this World.
	 * This function is only used when the world
	 * is in the process of being created.
	 * Will only make up to half the world rocks.
	 * 
	 * @throws IllegalArgumentException if the amount
	 * specified is larger than the grid to fill.
	 */
	private void addRandomRocks(int amount) {
		int randRow;
		int randCol;

		int numHexes = 0;
		for(Hex[] col : grid) {
			for (Hex h : col){
				if(h != null) numHexes++;
			}
		}

		int rocksToAdd = (amount > numHexes / 2) ? numHexes / 2 : amount;
		while(rocksToAdd > 0) {
			randCol = r.nextInt(grid.length); // biggest col index
			randRow = r.nextInt(grid[0].length); // biggest row index
			if (getHexAt(randCol,randRow) != null && !getHexAt(randCol, randRow).isRock) {
				grid[randCol][randRow].isRock = true;
				rocksToAdd--;
			}
		}
	}

	/**
	 * Get a hex by its column and its row.
	 * @param col: the column of the hex you want
	 * @param row: the row of the hex you want
	 * @return: the hex with the specified column and row.
	 * This may be null, meaning that there's no hex there.
	 */
	public Hex getHexAt(int col, int row) {
		
		// out-of-bounds is considered a null hex.
		if (col >= grid.length || row >= grid[0].length
			|| col < 0 || row < 0) return null;
		
		return grid[col][row];
	}

	/**
	 * Get the number of time steps elapsed since this world
	 * was created.
	 * @return the number of time steps elapsed since creation.
	 */
	public int getTime() {
		return timeElapsed;
	}

	/**
	 * Get the list of critters in this world.
	 * @return the list of critters in this world
	 */
	public ArrayList<Critter> getCritters() {
		return critters;
	}
	// Methods from SimObject

	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Name: " + name + "\n");
		sb.append("Time Step: " + timeElapsed + "\n");
		sb.append("Alive critters: " + critters.size());
		
		return sb.toString();
	}

	@Override
	public World getWorld() {
		return this; // this is the world containing itself
	}

	/**
	 * Returns the string representation of this
	 * world, which is both the map plus info about
	 * current time step and number of critters.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Now viewing the world named " + name + "\n");

		sb.append("Map:" + "\n");
		sb.append(getMap() + "\n");

		sb.append("Current time: ");
		sb.append(timeElapsed + "\n");

		sb.append("Number of critters: ");
		sb.append(critters.size());

		return sb.toString();
	}

	/**
	 * Returns the string representation of this world's
	 * contents, which is an ASCII map.
	 * @return
	 */
	public String getMap() {
		Hex[][] printMatrix = new Hex[grid[0].length][grid.length];

		// convert the grid into a print matrix that looks good
		int colCount = 0;
		for(Hex[] col : grid) {
			for(int i = printMatrix.length - 1; i >= 0; i--) {
				if (col[i] == null) {
					printMatrix[-i + printMatrix.length - 1][colCount] = null;
				}
				else {
					printMatrix[-i + printMatrix.length - 1][col[i].getCol()] = col[i];
				}
			}
			colCount++;
		}

		List<List<Hex>> inter = new ArrayList<List<Hex>>();
		for (int n = 0; n < cols; n++)
			inter.add(new ArrayList<Hex>());

		for (int c = 0; c < printMatrix[0].length; c++) {
			for (int r = 0; r < printMatrix.length; r++) {
				if (printMatrix[r][c] != null) {
					inter.get(c).add(printMatrix[r][c]);
				}
			}
		}
		
		List<List<Hex>> output = new ArrayList<List<Hex>>();
		for (int ix = 0; ix < inter.get(0).size(); ix++) {
			ArrayList<Hex> vals = new ArrayList<>();
			for (int j = 0; j < inter.size(); j++) {
				if (ix >= inter.get(j).size())
					vals.add(null);
				else
					vals.add(inter.get(j).get(ix));
			}
			output.add(vals);
		}

		StringBuilder sBuild = new StringBuilder();

		for (int index = 0; index < output.size(); index++) {
			if (output.get(output.size() - 1).contains(null)) {
				for (int ix = 0; ix < output.get(index).size(); ix++) {
					if (ix % 2 == 0) 
						if (output.get(index).get(ix) == null)
							sBuild.append("  ");
						else
							sBuild.append(output.get(index).get(ix).toString() + "   ");
				}
				sBuild.append("\n");
				sBuild.append("  ");
				for (int ix = 0; ix < output.get(index).size(); ix++) {
					if (ix % 2 == 1) {
						if (output.get(index).get(ix) == null)
							sBuild.append("  ");
						else
							sBuild.append(output.get(index).get(ix).toString() + "   ");
					}	
				}
				sBuild.append("\n");
			}
			else {
				sBuild.append("  ");
				for (int ix = 0; ix < output.get(index).size(); ix++) {
					if (ix % 2 == 1) 
						sBuild.append(output.get(index).get(ix).toString() + "   ");
				}	
				sBuild.append("\n");
				for (int ix = 0; ix < output.get(index).size(); ix++) {
					if (ix % 2 == 0)
						sBuild.append(output.get(index).get(ix).toString() + "   ");
				}
				sBuild.append("\n");
			}
		}
		return sBuild.toString();
	}

	
	public void advanceTime() {
		timeElapsed++;
		
		for(Critter c : critters) {
			c.getState().partner = null;
		}
		
		for (Critter c : critters) {
			CritterState cs = c.getState();
			Result r = (new Interpreter()).interpret(cs.p, cs);
			Rule rule = r.getRule();
			c.setLastRule(rule);
			Action act = r.getAction();
			performAction(c, act);
		}
	}
	
	/**
	 * Converts Action nodes from the AST to actions
	 * that affect the world.
	 * 
	 * @param c: the critter that will perform the action
	 * @param a: the AST action node that will be performed.
	 */
	public void performAction(Critter c, Action a) {
		switch(a.type) {
		case WAIT:
			c.pause();
			break;
		case FORWARD:
			c.move(1);
			break;
		case BACKWARD:
			c.move(-1);
			break;
		case LEFT:
			c.turn(-1);
			break;
		case RIGHT:
			c.turn(1);
			break;
		case EAT:
			c.eat();
			break;
		case ATTACK:
			c.attack();
			break;
		case GROW:
			c.grow();
			break;
		case BUD:
			c.bud();
			break;
		case MATE:
			c.mate();
			break;
		case TAG:
			c.tag(new Interpreter().evaluateExpr(
					(Expr) a.getChildren().get(0)));
			break;
		case SERVE:
			c.serve(new Interpreter().evaluateExpr(
					(Expr) a.getChildren().get(0)));
			break;
		default:
			break;
		}
	}
}
