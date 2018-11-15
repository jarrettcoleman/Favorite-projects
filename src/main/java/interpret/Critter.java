package interpret;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ast.Node;
import ast.ProgramImpl;
import ast.Rule;
import model.Constants;

/**
 * A controller for a critter state.
 * This separation allows for critters to take
 * on appearances in GUI form later on, if needed.
 */
public class Critter implements SimObject {

	private CritterState cs;
	private Rule lastRule;
	
	public Critter(Hex l, int f, int[] mem, ProgramImpl p) {
		cs = new CritterState(l,f,mem,p,this);
		if(!l.add(this)) {
			throw new IllegalArgumentException("Can't add a " +
					"critter to this hex");
		}
		l.getWorld().addCritter(this);
	}
	
	public Critter(String species, Hex l, int f, int[] mem, ProgramImpl p) {
		this(l,f,mem,p);
		cs.species = species;
	}
	
	public Critter(String fileName, Hex l) {
		// TODO implement
	}
	
	public CritterState getState() {
		return cs;
	}
	
	
	// actions
	
	/**
	 * Waits, letting this critter absorb solar energy.
	 * This method would normally be named wait(), but
	 * Object already has such a method.
	 */
	public void pause() {
		// gain energy
		// energy = energy + (solar flux * size)
		cs.setMem(4, Constants.solarFlux * cs.mem(3) + cs.mem(4));
	}
	
	/**
	 * Move this critter one hex in the specified direction.
	 * Attempting to move this critter to an invalid location
	 * removes energy, but does nothing to the location.
	 * 
	 * @param dir: the direction to move in. 1 for forwards,
	 * and -1 for backwards. Does nothing and doesn't change
	 * energy if any other number is inputed.
	 */
	public void move(int dir) {
		if(dir != -1 && dir != 1) return;

		// spend the energy
		// energy = energy - (move cost * size)
		cs.setMem(4, cs.mem(4) - Constants.moveCost * cs.mem(3));
		
		if (!isDead()) {
			if (cs.ahead(dir) != 0) return; // path blocked
			
			int[] delta = cs.nextHex(cs.facing);
			if(dir == -1) {
				// go backwards
				delta[0] *= -1;
				delta[1] *= -1;
			}
			// have to do this manually because of different
			// delta computation - needed to do multiplcation
			// for backwards, which getFacingHex() can't do.
			
			// add this to the new hex
			getWorld().getHexAt(cs.location.getCol() + delta[0],
					cs.location.getRow() + delta[1]).add(this);
			
			// remove this from the old hex
			cs.location.removeOccupant();
			
			// update the location
			cs.location = getWorld().getHexAt(cs.location.getCol() 
					+ delta[0], cs.location.getRow() + delta[1]);
		}
		
	}
	
	/**
	 * Changes this critter's direction by 1 unit in
	 * the specified direction.
	 * 
	 * @param dir: the way to turn. 1 for right and -1 for left.
	 * This method does nothing if its argument is neither 1
	 * nor -1.
	 */
	public void turn(int dir) {
		
		if(dir != 1 && dir != -1) return;
		// spend the energy
		// energy = energy - size
		cs.setMem(4, cs.mem(4) - cs.mem(3));
		
		if(!isDead())
			cs.facing = Math.floorMod(cs.facing + dir, 6); // wraparound from 0 to 5
		
	}
	
	/**
	 * Eat the food in front of this critter.
	 * If there's no food there, or if there's something
	 * there that isn't food, or if the
	 * critter is full, this method does nothing.
	 * 
	 * This method spends energy equal to the
	 * critter's size.
	 */
	public void eat() {
		
		Hex target = getState().getFacingHex();
		if(cs.mem(4) == Constants.energyPerSize * cs.mem(3)) return; // critter is full
		if(target == null) return; // no hex here
		if(target.getStatus() >= -1) return; // no food here
		
		// spend the energy
		// energy = energy - size
		cs.setMem(4, cs.mem(4) - cs.mem(3));
		if(!isDead()) {
			Food f = (Food)target.getOccupant();
			int limit = Constants.energyPerSize * cs.mem(3);
			
			if (f.getValue() > limit - cs.mem(4)) {
				// food has more than critter can absorb
				f.setValue(f.getValue() - (limit - cs.mem(4)));
			} else {
				cs.setMem(4, cs.mem(4) + f.getValue());
				target.removeOccupant();
			}
		}
		
		
	}
	
	public void serve(int amount) {
		
		Hex target = cs.getFacingHex();
		if(target == null) return;
		
		if(target.getStatus() < -1) {
			// there's already food here - add on to it
			Food f = (Food) target.getOccupant();
			if (amount >= cs.mem(4)) {
				// critter will die
				f.setValue(f.getValue() + cs.mem(4));
				cs.setMem(4, 0);
				isDead();
			} else {
				// critter won't die
				f.setValue(f.getValue() + amount);
				cs.setMem(4, cs.mem(4) - amount);
			}
			
		} else if(target.getStatus() == 0) {
			// there's no food here - make some
			if(amount >= cs.mem(4)) {
				target.add(new Food(cs.mem(4), getWorld()));
				cs.setMem(4, 0);
				isDead();
			} else {
				target.add(new Food(amount, getWorld()));
				cs.setMem(4, cs.mem(4) - amount);
			}
			
		} else return; // no space for food
	}
	
	public void attack() {
		// TODO test this method with JUnit
		
		// TODO document this decision: An attack that fails
		// still uses energy. This is like punching the air or a 
		// punching bag (i.e. a rock) - 
		// it takes energy, but doesn't hurt anyone.
		
		// lose energy based on size
		cs.setMem(4, cs.mem(4) - 
				(cs.mem(3) * Constants.attackCost));
		
		if(!isDead()) {
			
			Hex targetHex = cs.getFacingHex();
			if (targetHex == null) return;
			if (targetHex.getStatus() > 0) {
				// critter is attacked
				Critter victim = (Critter)targetHex.getOccupant();
				
				// calculate damage
				double damage = Constants.baseDamage * cs.mem(3);
				damage *= logisticFunc(Constants.damageInc * 
						((cs.mem(3)* cs.mem(2)) - 
						(victim.getState().mem(3) 
								* victim.getState().mem(1))));
				damage = Math.round(damage);
				
				// subtract damage
				victim.getState().setMem(4,
						victim.getState().mem(4) - (int)damage);
				victim.isDead(); // handle the victim's death
			}
		}
		
	}
	
	/**
	 * Tag the critter in front of this critter
	 * with a number. Costs {@code SIZE} energy.
	 * 
	 * @param tagNum: the number in which to tag
	 * the critter in front with. Must be from 0 to 99,
	 * inclusive of both endpoints. If it's not, this
	 * method does nothing.
	 */
	public void tag(int tagNum) {
		// tag values can only be from 0 to 99.
		if(tagNum <= 0 || tagNum > 100) return;
		
		if(cs.ahead(1) <= 0) return; // because no critter
		
		// spend the energy
		// energy = energy - size
		cs.setMem(4, cs.mem(4) - cs.mem(3));
		
		if(!isDead()) {
			int[] delta = cs.nextHex(cs.facing);
			Hex target = getWorld().getHexAt(cs.location.getCol()
					+ delta[0], cs.location.getRow() + delta[1]);
			
			((Critter)target.getOccupant()).getState().setMem(6, tagNum);
		}
		
	}
	
	public void grow() {
		
		// spend the energy
		// energy = energy - (complexity * grow cost)
		cs.setMem(4, cs.mem(4) - 
				(getComplexity() * Constants.growCost));

		if(!isDead()) {
			cs.setMem(3, cs.mem(3) + 1);
		}
	}
	
	public void setLastRule(Rule r) {
		lastRule = r;
	}
	
	public void bud() {
		// TODO implement
		
		// TODO document this decision: a critter can die while trying
		// to bud. This is the critter equivalent of a mother dying
		// in labor. If there's no space and a critter tries to bud,
		// it will still lose energy.

		// spend the bud cost based on complexity
		// energy = energy - (bud cost * complexity)
		cs.setMem(4, cs.mem(4) - (Constants.budCost * getComplexity()));
		
		if(!isDead()) {
			int[] delta = cs.nextHex(cs.facing);
			delta[0] *= -1;
			delta[1] *= -1;
			
			// invalid space
			Hex target = getWorld().getHexAt(cs.location.getCol() 
					+ delta[0], cs.location.getRow() + delta[1]);
			if(target == null) return;
			if(target.getStatus() != 0) return;
			
			// make the mem array
			int[] newMem = Arrays.copyOf(cs.mem, cs.mem.length);
			for(int i = 8; i < newMem.length; i++) {
				newMem[i] = 0; 
			}
			newMem[3] = 1; // size
			newMem[4] = Constants.initialEnergy; // energy
			newMem[6] = 0; // tag
			newMem[7] = 0; // posture
			
			// apply mutations, if any	
			ProgramImpl newPrgm = cs.p;
			if(newPrgm != null) {
				int numMutations = numMutations();
				for(int i = 0; i < numMutations; i++) {
					newPrgm = (ProgramImpl) newPrgm.mutate();
				}
			}
			
			target.add(new Critter(target, cs.facing, newMem,
					newPrgm));
			
		}
	}
	
	public void mate() {
		
		// spend the cost of failure
		// energy = energy - size
		cs.setMem(4, cs.mem(4) - cs.mem(3));
		if(isDead()) return;
		
		Hex target = cs.getFacingHex();
		if(target == null) return;
		if(target.getStatus() <= 0) return;
		
		cs.partner = (Critter) target.getOccupant();
		
		if((Critter) target.getOccupant() == cs.partner) {
			// it's a match!
			
			// regain the cost of failure
			cs.setMem(4, cs.mem(4) + cs.mem(3));
			cs.partner.getState().setMem(4, cs.mem(4) + 
					cs.partner.getState().mem(3));
			
			// spend the cost of successful mating
			// if either dies in the process, don't reproduce.
			cs.setMem(4, cs.mem(4) - (getComplexity() * Constants.mateCost));
			cs.partner.getState().setMem(4, cs.partner.getState().mem(4) 
					- (cs.partner.getComplexity() * Constants.mateCost));
			
			if(cs.partner.isDead()) {
				// have to remove partner before removing this
				if(isDead()) return;
			}
			if(isDead()) return; 
			
			Random r = new Random();
			int[] delta;
			Hex behind;
			
			if(r.nextInt(2) == 0) {
				// appear behind this critter
				delta = cs.nextHex(cs.facing);
				delta[0] *= -1;
				delta[1] *= -1;
				
				// invalid space
				behind = getWorld().getHexAt(cs.location.getCol() 
						+ delta[0], cs.location.getRow() + delta[1]);
				if(behind == null) return;
				if(behind.getStatus() != 0) return;
				
			} else {
				// appear behind other critter
				delta = cs.nextHex(cs.partner.getState().facing);
				delta[0] *= -1;
				delta[1] *= -1;
				
				// invalid space
				behind = getWorld().getHexAt(cs.partner.getState().location.getCol() 
						+ delta[0], cs.partner.getState().location.getRow() + delta[1]);
				if(behind == null) return;
				if(behind.getStatus() != 0) return;
			}
			
			// make the critter
			
			// form the memory
			int[] newMem; 
			int rand = r.nextInt(2);
			if(rand == 0) {
				newMem = new int[cs.mem(0)];
			} else {
				newMem = new int[cs.partner.getState().mem(0)];
			}
			newMem[0] = newMem.length;
			rand = r.nextInt(2);
			newMem[1] = (rand == 0) ? cs.mem(1) : cs.partner.getState().mem(1);
			rand = r.nextInt(2);
			newMem[2] = (rand == 0) ? cs.mem(2) : cs.partner.getState().mem(2);
			newMem[3] = 1; // size
			newMem[4] = Constants.initialEnergy; // energy
			newMem[6] = 0; // tag
			newMem[7] = 0; // posture
			// rest of the values are initialized to 0
			
			// pick a rule set length
			int numRules = 0;
			rand = r.nextInt(2);
			
			
			if(rand == 0) {
				if(cs.p == null) {
					numRules = 0;
				} else {
					numRules = cs.p.getChildren().size();
				}
				
			} else {
				if(cs.p == null) {
					numRules = 0;
				} else {
					numRules = cs.partner.getState().p.
						getChildren().size();
				}
			}
			
			// add the rules
			ProgramImpl newPrgm = null;
			List<Node> ruleSet1 = null;
			List<Node> ruleSet2 = null;
			
			if(numRules == 0) {
				newPrgm = null;
			} else {
				newPrgm = new ProgramImpl();
				ruleSet1 = cs.p.getChildren();
				ruleSet2 = cs.partner.getState()
						.p.getChildren();
			}
			for(int i = 0; i < numRules; i++) {
				if(i >= ruleSet1.size()) {
					newPrgm.add((Rule)ruleSet2.get(i).clone());
				} else if (i >= ruleSet2.size()) {
					newPrgm.add((Rule)ruleSet1.get(i).clone());
				} else {
					rand = r.nextInt(2);
					if(rand == 0) {
						newPrgm.add((Rule)ruleSet1.get(i).clone());
					} else {
						newPrgm.add((Rule)ruleSet2.get(i).clone());
					}
				}
			}
			
			// mutate, if necessary
			if(newPrgm != null) {
				int numMutations = numMutations();
				for(int i = 0; i < numMutations; i++) {
					newPrgm = (ProgramImpl) newPrgm.mutate();
				}
			}
			
			
			target.add(new Critter(behind, cs.facing, newMem,
					newPrgm));
		}
	}
	
	/**
	 * Check if this critter is dead, and remove it if it is.
	 * A 'dead' critter has 0 or less energy.
	 * 
	 * WARNING: DO NOT attempt to modify a critter
	 * after it's dead. Doing so may throw uncaught exceptions!
	 * 
	 * @return true if this critter is dead,
	 * false otherwise.
	 */
	public boolean isDead() {
		if(cs.mem(4) <= 0) {
			cs.location.removeOccupant();
			cs.location.add(new Food(Constants.foodPerSize * 
					cs.mem(3), getWorld()));
			getWorld().removeCritter(this);
			return true;
		}
		return false;
	}
	
	private double logisticFunc(double d) {
		return (1 / (1 + Math.pow(Math.E, -d)));
	}
	
	private int numMutations() {
		Random r = new Random();
		int count = 0;
		while(r.nextInt(4) == 0) {
			count++;
		}
		return count;
	}
	
	/**
	 * Get the complexity of this critter, which is 
	 * (# of rules * rule cost) + 
	 * 	((offense + defense) * ability cost)
	 * 
	 * @return the complexity of this critter.
	 */
	public int getComplexity() {
		int ruleCount = 0;
		if(cs.p == null) {
			ruleCount = 0; // zero rules bc no program.
		} else if(!cs.p.hasChildren()) {
			ruleCount = 0; // has no rules
		} else {
			ruleCount = cs.p.getChildren().size();
		}
		
		
		return ruleCount * Constants.ruleCost +
			((cs.mem(1) + cs.mem(2)) * Constants.abilityCost);
	}
	
	// Methods from SimObject
	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Species: " + cs.mem(7) + "\n");
		sb.append("Contents of first 8 memory locations: ");
		sb.append("\n");
		for(int i = 0; i < 8; i++) {
			sb.append("mem[" + i + "] = " + cs.mem(i) + "\n");
		}
		sb.append("Rule set:" + "\n");
		
		// TODO add pretty-printed program
		if(cs.p == null) {
			sb.append("This critter doesn't have a program.");
		} else {
			cs.p.prettyPrint(sb); // pretty-printed program
			
		}
		
		if(lastRule != null) {
			// Add the last rule executed 
			sb.append("Last rule executed: " + lastRule.toString());
		}
		
		
		return sb.toString();
	}

	@Override
	public World getWorld() {
		return cs.location.getWorld();
	}
	
	public String toString() {
		return "" + cs.facing;
	}
}
