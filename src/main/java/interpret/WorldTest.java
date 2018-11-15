package interpret;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class WorldTest {
	
	@Test
	public void testPrintWithCritter() {
		World w = new World("Lonely World", 5,5);
		Critter c = new Critter(w.getHexAt(0,0), 0, null, null);
		System.out.println(w.toString());
	}
	
	@Test
	public void testPrint() {
		World w = new World("Test", 7, 8);
		System.out.println(w.toString());
	}
	
	@Test
	public void testPrint3() {
		World w = new World("4x5", 4,5);
		System.out.println(w);
	}
	
	@Test
	public void testPrint4() {
		World w = new World("5,4", 5, 4);
		System.out.println(w);
	}
}
