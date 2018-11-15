package interpret;

/**
 * A SimObject is anything part of a World that
 * a user is able to see on the screen, namely
 * Critters, Hexes, Food, and even a world itself.
 * 
 */
public interface SimObject {

	/**
	 * Get information about this object in string form.
	 * Useful for printing.
	 * @return the string representation (including details)
	 * about this object.
	 */
	String getInfo();
	
	/**
	 * Get the world this object currently inhabits.
	 * @return the world that this object is in.
	 */
	World getWorld();
	
}
