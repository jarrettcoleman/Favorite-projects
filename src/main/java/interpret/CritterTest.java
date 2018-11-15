package interpret;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CritterTest {

	@Test
	/**
	 * Test whether this critter turns properly, including 'wraprarounds',
	 * i.e. turning 1 unit when facing direction 5 causes the critter
	 * to face direction 0, and turning -1 units when facing direction 0
	 * causes the critter to face direction 5.
	 */
	void testTurn() {
		World w = new World("Turning world", 5,5);
		Critter c = new Critter(w.getHexAt(0,0), 0, getMemArray(100), null);
		c.getState().setMem(3,1); // size = 1 so it doesn't die
		
		assertTrue(c.getState().facing == 0); // initialization OK
		
		c.turn(-1); // turn left
		assertTrue(c.getState().facing == 5);
		
		for(int i = 4; i >= 0; i--) {
			// count down to turning to direction 0
			c.turn(-1);
			assertTrue(c.getState().facing == i);
		}
		
		for(int i = 1; i <= 5; i++) {
			// count up to turning right until direction 5
			c.turn(1); // turn right
			assertTrue(c.getState().facing == i);
		}
		
		c.turn(1); // turn right
		assertTrue(c.getState().facing == 0);
		
	}
	
	@Test
	/**
	 * Test whether this critter can move in a straight line.
	 * This accounts for forwards, backwards, and if the
	 * critter wants to move outside the world boundaries.
	 */
	public void testMove() {
		World w = new World("Moving world", 4,5);
		Critter c = new Critter(w.getHexAt(0,0), 0, getMemArray(100), null);
		c.getState().setMem(3,1); // size = 1 so it doesn't die
		
		c.move(-1); // can't do that - this is a world boundary
		assertTrue(c.getState().location == w.getHexAt(0,0));
		
		c.move(1);
		assertTrue(c.getState().location == w.getHexAt(0,1));
		
		c.move(1);
		c.move(1);
		c.move(1); // is an invalid (null) hex
		assertTrue(c.getState().location == w.getHexAt(0,2));
	}

	@Test
	/**
	 * Tests moving in conjunction with turning.
	 */
	public void testMoveTurn() {
		World w = new World("Movey-Turney World", 4,7);
		Critter c = new Critter(w.getHexAt(0,0), 0, getMemArray(100), null);
		c.getState().setMem(3,1); // size = 1 so it doesn't die
		
		c.turn(1);
		c.move(1);
		assertTrue(c.getState().location == w.getHexAt(1,1));
	}
	
	@Test
	public void testTag() {
		World w = new World("Tag World", 4,4);
		Critter c1 = new Critter(w.getHexAt(0,0), 0, getMemArray(100), null);
		Critter c2 = new Critter(w.getHexAt(0,1), 0, getMemArray(100), null);
		int oldTag = c2.getState().mem(6);
		
		c1.tag(-1); // can't tag negatively
		assertTrue(c2.getState().mem(6) == oldTag);
		
		c1.tag(500); // can't tag too high either
		assertTrue(c2.getState().mem(6) == oldTag);
		
		c1.tag(3); // valid tag
		assertTrue(c2.getState().mem(6) == 3);
	}
	
	@Test
	public void testAppearance() {
		World w = new World("Appearance world", 3,3);
		Critter c = new Critter(w.getHexAt(0, 0), 0, 
				getMemArray(100), null);
		
		c.getState().setMem(3, 12);
		c.getState().setMem(6, 34);
		c.getState().setMem(7, 56);
		
		assertTrue(c.getState().getAppearance() == 1234560);
	}
	
	@Test
	public void testEat() {
		World w = new World("Dream land", 4,4);
		Critter c = new Critter(w.getHexAt(0,0), 0, 
				getMemArray(100), null);
		c.getState().setMem(3,1); // size = 1
		c.eat(); // should do nothing
		
		// so energy should be the same
		assertTrue(c.getState().mem(4) == 100);
		
		// critter should eat all of this food because it
		// gets it to the max (1 * 500 = 500);
		w.getHexAt(0,1).add(new Food(401, w));
		c.eat();
		assertTrue(c.getState().mem(4) == 500);
		
		// food should also disappear because
		// it has been fully eaten.
		assertTrue(c.getState().getFacingHex().getStatus() == 0);
		
		// can't eat any more, so nothing should change.
		w.getHexAt(0,1).add(new Food(450, w));
		c.eat();
		System.out.println(c.getState().mem(4));
		assertTrue(c.getState().mem(4) == 500);
		assertTrue(c.getState().getFacingHex().getStatus() == -451);
		
	}
	
	@Test
	public void testServe() {
		World w = new World("Waiter!", 4,4);
		Critter c = new Critter(w.getHexAt(0,0), 0, 
				getMemArray(100), null);
		c.getState().setMem(3, 1); // size is 1
		
		// try to serve on a rock
		w.getHexAt(0,1).isRock = true;
		c.serve(50);
		assertTrue(c.getState().mem(4) == 100);
		assertTrue(c.getState().getFacingHex().getOccupant() == null);
		w.getHexAt(0,1).isRock = false;
		
		// try to serve off the world boundary
		c.turn(-1);
		c.serve(10);
		c.getState().setMem(4, c.getState().mem(4) + 1); // to compensate for turn
		assertTrue(c.getState().mem(4) == 100);
		c.turn(1);
		c.getState().setMem(4, c.getState().mem(4) + 1); // to compensate for turn
		
		
		// try to serve 50 units, and see if the
		// subtraction and the new Food show up.
		c.serve(50);
		assertTrue(c.getState().mem(4) == 50);
		assertTrue(((Food) c.getState().getFacingHex()
				.getOccupant()).getValue() == 50);
		
		// try to put food on top of food
		c.serve(25);
		assertTrue(c.getState().mem(4) == 25);
		assertTrue(((Food) c.getState().getFacingHex().getOccupant()).
				getValue() == 75);
		
		// die from serving too much on top of existing food
		c.serve(25);
		assertTrue(((Food) c.getState().getFacingHex().getOccupant()).
				getValue() == 100);
		assertTrue(((Food) c.getState().location.getOccupant()).
				getValue() == 200);
		
		// die from serving too much for a new food object
		w.getHexAt(0, 0).removeOccupant();
		w.getHexAt(0, 1).removeOccupant();
		Critter c2 = new Critter(w.getHexAt(0,0), 0,
				getMemArray(10), null);
		c2.getState().setMem(3, 1); // size is 1
		c2.serve(100);
		assertTrue(((Food) c2.getState().getFacingHex().getOccupant()).
				getValue() == 10);
		assertTrue(((Food) c2.getState().location.getOccupant()).
				getValue() == 200);
		
	}
	
	@Test
	public void testGrow() {
		World w = new World("World of Growth", 4,4);
		int[] mem = {1,1,1,1,60,1,1,1};
		Critter c = new Critter(w.getHexAt(0,0), 0, 
				mem, null);
		c.grow();
		assertTrue(c.getState().mem(3) == 2); // sufficient energy
		assertTrue(c.getState().mem(4) == 10);
		c.grow(); //insufficient energy - should die
		assertTrue(!(w.getHexAt(0,0).getOccupant() instanceof Critter));
	}
	
	@Test
	public void testAttack() {
		World w = new World("Hostile World", 4,4);
		int[] mem = {1,1,1,1,100,1,1,1};
		Critter c1 = new Critter(w.getHexAt(0,0), 0, 
				Arrays.copyOf(mem, mem.length), null);
		Critter c2 = new Critter(w.getHexAt(0,1), 0, 
				Arrays.copyOf(mem, mem.length), null);
		c1.attack();
		assertTrue(c1.getState().mem(4) == 95);
		assertTrue(c2.getState().mem(4) == 50);
		
		c1.attack();
		assertTrue(c1.getState().mem(4) == 90);
		// critter 2 should die
		assertTrue(!(w.getHexAt(0,1).getOccupant() instanceof Critter));
		
		c1.attack();
		assertTrue(c1.getState().mem(4) == 85); // attacking nothing

		w.getHexAt(0,1).removeOccupant();
		Critter c3 = new Critter(w.getHexAt(0,1), 0, 
				Arrays.copyOf(mem, mem.length), null);
		c1.getState().setMem(4, 5);
		c1.attack(); // c1 should die and c2 should be untouched
		assertTrue(!(w.getHexAt(0,0).getOccupant() instanceof Critter));
		assertTrue(c3.getState().mem(4) == 100);
	}
	
	@Test
	public void testMate() {
		World w = new World("World of Love", 4,6);
		int[] mem = {8,1,1,1,300,1,1,1};
		Critter c1 = new Critter(w.getHexAt(0,1), 0, 
				Arrays.copyOf(mem, mem.length), null);
		Critter c2 = new Critter(w.getHexAt(0,2), 3, 
				Arrays.copyOf(mem, mem.length), null);
		c1.getState().partner = null;
		c2.getState().partner = null;
		System.out.println(c1.getState().mem(4));
		System.out.println(c2.getState().mem(4));
		System.out.println(c1.getState().mem(3));
		System.out.println(c2.getState().mem(3));
		c1.mate();
		assertTrue(c1.getState().partner == c2);
		c2.mate();
		assertTrue(c2.getState().partner == c1);
		
	}
	
	@Test
	public void testBud() {
		World w = new World("World of Growth", 4,4);
		int[] mem = {1,1,1,1,460,1,1,1};
		// complexity of 50
		Critter c = new Critter(w.getHexAt(0,1), 0, 
				mem, null);
		c.bud();
		assertTrue(w.getHexAt(0,0).getOccupant() instanceof Critter);
		System.out.println(w.getHexAt(0,0).getOccupant().getInfo());
		// TODO add more asserts to check mem
	}
	
	
	/**
	 * Get an integer array that is valid if it were to be a critter
	 * memory array. 
	 * 
	 * @param energy: the energy in which to add. Useful to check
	 * if you want to make sure a critter does or doesn't die.
	 * 
	 * @return an integer array that is a valid critter memory array
	 * (see the CritterWorld spec)
	 */
	public static int[] getMemArray(int energy) {
		int[] mem = new int[8];
		Random r = new Random();
		for(int i = 0; i < mem.length; i++) {
			mem[i] = r.nextInt(99) + 1;
		}
		mem[0] = mem.length;
		mem[4] = energy;
		return mem;
	}

}
