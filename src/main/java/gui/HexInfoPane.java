package gui;

/**
 * A controller for this GUI's Hex info pane
 *
 */
public class HexInfoPane {
	private Main m;
	public int col, row;
	public HexInfoPane(Main m) {
		this.m = m;
	}
	
	/**
	 * Update the box containing the current hex's info
	 * @param col: the column of the selected hex
	 * @param row: the row of the selected hex
	 */
	public void updateHexInfo(int col, int row) {
		m.getHexInfo().setText(
			m.getWorld().getHexAt(col,row).getInfo());
	}
	
	public void update() {
		if( ( (2*row)- col >= 0) && ( (2*row)- col < (2*m.getWorld().getRows())-m.getWorld().getCols() )) {
			m.getHexInfo().setText(
					m.getWorld().getHexAt(col, row).getInfo());
		} else {
			m.getHexInfo().setText("Click out of bounds");
		}
		
	}
}
