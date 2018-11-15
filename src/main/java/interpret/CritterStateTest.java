package interpret;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CritterStateTest {

	@Test
	public void testNearby() {
		World w = new World("Nearby world", 3,3);
		Critter c = new Critter(w.getHexAt(0,0), 0, 
				CritterTest.getMemArray(100), null);
		
		 // nothing ahead
		assertTrue(c.getState().nearby(0) == 0);
		
		// try to see the food
		w.getHexAt(0, 1).add(new Food(100, w));
		assertTrue(c.getState().nearby(0) == -101);
		
		// remove the food and try to see a rock
		w.getHexAt(0,1).removeOccupant();
		w.getHexAt(0,1).isRock = true;
		assertTrue(c.getState().nearby(0) == -1);
		
		// remove the rock and try to see another critter
		w.getHexAt(0,1).isRock = false;
		Critter c2 = new Critter(w.getHexAt(0,1), 0,
				new int[10], null);
		c2.getState().setMem(4, 100); // give it 100 energy
		c2.getState().setMem(7, 10); // posture of 10
		assertTrue(c.getState().nearby(0) == 
				c2.getState().getAppearance());
		
	}
	
	@Test
	public void testAhead() {
		World w = new World("Ahead world", 3,4);
		Critter c = new Critter(w.getHexAt(0,0), 0, 
				CritterTest.getMemArray(100), null);
		
		// see a rock
		w.getHexAt(0,1).isRock = true;
		assertTrue(c.getState().ahead(1) == -1);
		
		// see a critter
		Critter c2 = new Critter(w.getHexAt(0,2), 0,
				new int[10], null);
		c2.getState().setMem(4, 100); // give it 100 energy
		c2.getState().setMem(7, 10); // posture of 10
		assertTrue(c.getState().ahead(2) == 
				c2.getState().getAppearance());
		
		// see an empty space
		w.getHexAt(0,2).removeOccupant();
		assertTrue(c.getState().ahead(2) == 0);
		
		// 'see' a null hex
		assertTrue(c.getState().ahead(3) == -1);
		
		// 'see' off the edge of the world
		assertTrue(c.getState().ahead(5) == -1);
	}

	@Test
	public void testGetFacingHex() {
		World w = new World("Facing world", 5,5);
		Critter c = new Critter(w.getHexAt(0,0), 0,
				CritterTest.getMemArray(100), null);
		c.getState().setMem(3,1); // size = 1 so it doesn't die
		
		
		
		// hex in front is valid
		assertTrue(c.getState().getFacingHex() == w.getHexAt(0,1));
		
		c.turn(-1);
		
		// world border
		assertNull(c.getState().getFacingHex());
		
		// move so a hex in front is null
		c.turn(1);
		c.move(1);
		c.move(1); 
		
		// null hex
		assertNull(c.getState().getFacingHex());
	}
}
