package interpret;

/**
 * A representation of food in a critter world.
 *
 */
public class Food implements SimObject {

	int value;
	World wd;
	
	public Food(int value, World w) {
		this.value = value;
		wd = w;
	}
	
	@Override
	public String getInfo() {
		return "There are " + value + " units of food here.";
	}

	@Override
	public World getWorld() {
		return wd;
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}
