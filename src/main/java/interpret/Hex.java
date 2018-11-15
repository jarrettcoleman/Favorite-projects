package interpret;

/**
 * A representation of one hex in a critter world.
 *
 */
public class Hex implements SimObject {

	int col, row;
	public boolean isRock;
	SimObject obj;
	World w;
	
	public Hex(int col, int row, boolean isRock, World w) {
		this.col = col;
		this.row = row;
		this.isRock = isRock;
		obj = null;
		this.w = w;
	}
	
	public Hex(int col, int row, World w) {
		this(col, row, false,w);
	}
	
	public int getCol() {
		return col;
	}
	
	public int getRow() {
		return row;
	}
	
	/**
	 * Get the 'status code' of this hex.
	 * This 'status code' is the same value that a critter's
	 * nearby[] would return.
	 * 
	 * @return an integer n that satisifies the following rules:
	 * n = 0: empty hex
	 * n > 0: hex contains a critter with appearance n
	 * n < -1: hex contains food with energy (-n)-1
	 * n = -1: hex contains a rock
	 */
	public int getStatus() {
		if (obj == null) 
			return isRock ? -1 : 0;
		if (obj instanceof Critter)
			return ((Critter)obj).getState().getAppearance();
		if (obj instanceof Food) return -1 - ((Food)obj).getValue();
		throw new IllegalStateException("This hex is somehow not " +
				"a rock, empty, food, or critter");
	}
	
	/**
	 * Adds a SimObject to this hex.
	 * 
	 * @param o: the SimObject to add
	 * @throws IllegalArgumentException if one attempts to add
	 * a Hex or a World (which are technically still SimObjects)
	 * to this hex.
	 * @return true if the addition succeeds, false otherwise
	 */
	public boolean add(SimObject o) {
		if (o instanceof Hex || o instanceof World)
			throw new IllegalArgumentException("Can only add " +
					"a critter or food to a hex");
		
		if (getStatus() != 0) return false; // space already occupied
		obj = o;
		return true;
	}
	
	/**
	 * Get the occupant of this hex.
	 * 
	 * Precondition: The occupant of this hex is not a rock.
	 * 
	 * @return the occupant of this hex.
	 */
	public SimObject getOccupant() {
		return obj;
	}
	
	/**
	 * Remove the occupant of this hex.
	 * 
	 * @return false if the object to remove is a 'rock' or
	 * there's nothing to remove, true otherwise.
	 * 
	 * Postcondition: the field obj (that holds the object
	 * on this hex) is null.
	 */
	public boolean removeOccupant() {
		if (isRock) return false; // can't remove a rock
		if (obj == null) return false; // can't remove nothing
		
		obj = null;
		return true;
	}
	
	// methods from SimObject
	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Hex at (");
		sb.append(col);
		sb.append(",");
		sb.append(row);
		sb.append(")"+"\n");
		if (isRock) {
			sb.append("is a rock.");
		} else if (obj == null) {
			sb.append("is empty.");
		} else if (obj instanceof Critter) {
			sb.append("contains a critter." + "\n");
			sb.append(obj.getInfo());
		} else {
			// only other possibility is food
			sb.append("contains food." + "\n");
			sb.append(obj.getInfo());
		}
		
		return sb.toString();
	}

	@Override
	public World getWorld() {
		return w;
	}

	/**
	 * Returns the string representation of this hex, i.e.
	 * what is printed out in the map of the world.
	 */
	public String toString() {
		
		//return "(" + col + "," + row + ")";
		
		// TODO change this - above is for testing purposes only.
		
		if (isRock) {
			return "#";
		} else if (obj == null) {
			return "-";
		} else if (obj instanceof Critter) {
			return ((Critter)obj).toString();
		} else {
			// must have food
			return "F";
		}
		
	}
	
}
