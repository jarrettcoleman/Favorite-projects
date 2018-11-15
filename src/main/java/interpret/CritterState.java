package interpret;

import ast.ProgramImpl;

/**
 * A model of a critter.
 * @author Syd
 *
 */
public class CritterState implements State {

	Hex location;
	public String species;
	int[] mem;
	Critter c;
	ProgramImpl p;
	int pc; // pass counter
	public int facing;
	
	Critter partner; // mating partner
	
	public CritterState(Hex l, int f, int[] mem, ProgramImpl p, Critter c) {
		location = l;
		facing = f;
		this.mem = mem;
		this.p = p;
		this.c = c;
	}

	@Override
	public int mem(int index) {
		return mem[index];
	}

	@Override
	public void setMem(int index, int val) {
		mem[index] = val; 
	}

	@Override
	public int nearby(int dir) {

		int[] delta = nextHex(dir);

		// sees a rock if it's the edge of the world
		if(getWorld().getHexAt(location.getCol() + delta[0],
				location.getRow() + delta[1]) == null) return -1;

		return getWorld().getHexAt(location.getCol() + delta[0],
				location.getRow() + delta[1]).getStatus();
	}

	@Override
	public int ahead(int dist) {

		int[] delta = nextHex(facing);

		delta[0] *= dist;
		delta[1] *= dist;

		// sees a rock if it's the edge of the world
		if(getWorld().getHexAt(location.getCol() + delta[0],
				location.getRow() + delta[1]) == null) return -1;

		return getWorld().getHexAt(location.getCol() + delta[0],
				location.getRow() + delta[1]).getStatus();
	}

	private World getWorld() {
		return location.getWorld();
	}

	@Override
	public void reset() {
		pc = 0;
	}

	@Override
	public void step() {
		pc++;
	}

	/**
	 * Get the difference of coordinates between the next hex
	 * to go to (in the direction this critter is {@code facing})
	 * and where the critter is right now.
	 * 
	 * @param facing: the direction this critter is currently facing.
	 * If this number is outside the normal range of 0 to 5 (inclusive
	 * of both endpoints), this will return [0,0].
	 * 
	 * @return an array of two integers. The first one is
	 * the column difference, and the second is the row difference.
	 */
	public int[] nextHex(int facing) {
		int dRow = 0;
		int dCol = 0;

		switch(facing) {
		case 0:
			dRow = 1;
			break;
		case 1:
			dCol = 1; dRow = 1;
			break;
		case 2:
			dCol = 1;
			break;
		case 3:
			dRow = -1;
			break;
		case 4:
			dCol = -1; dRow = -1;
			break;
		case 5:
			dCol = -1;
			break;
		default:
			break;
		}

		int[] result = new int[2];
		result[0] = dCol;
		result[1] = dRow;
		return result;
	}

	/**
	 * Return the hex that this critter is facing and is also 1
	 * hex away.
	 * 
	 * @return the hex that this critter is facing. This may return
	 * Since this depends on {@code World.getHexAt()}, this may return
	 * null, a hex that is a rock, or a hex that isn't a rock.
	 */
	public Hex getFacingHex() {
		int[] delta = nextHex(facing);
		return getWorld().getHexAt(location.getCol() + delta[0],
				location.getRow() + delta[1]);
	}
	
	/**
	 * Get the appearance of this critter, which is defined as
	 * (100,000 * size) + (1,000 * tag) + 
	 * 	(10 * posture) + direction.
	 * 
	 * @return this critter's appearance.
	 */
	public int getAppearance() {
		return (100000 * mem(3)) + (1000 * mem(6)) + (10 * mem(7)) + facing;
	}
}
