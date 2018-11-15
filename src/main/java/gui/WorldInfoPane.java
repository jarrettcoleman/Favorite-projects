package gui;

/**
 * A way to control the info boxes to the right
 * of the critter world display.
 *
 */
public class WorldInfoPane {
	private Main m;
	
	public WorldInfoPane(Main m) {
		this.m = m;
	}
	
	/**
	 * Update the box containing the world's info.
	 */
	public void update() {
		m.getWorldInfo().setText(m.getWorld().getInfo()
				+ "\n" + "Speed: " + m.getSpeed());
	}
	
}
