package ast;

import java.util.Random;

/**
 * A data structure representing a critter program.
 */
public class ProgramImpl extends AbstractNode implements Program {
   
   private Random rand = new Random();
   private int memSize; // dummies for A5
   private int offense;
   private int defense;
   public int successes = 0; // number of successful mutations
   public int attempts = 0; // number of attempted mutations
   
   public void add(Rule rule) {
      rule.parent = this;
      children.add(rule);
   }

   @Override
   public Program mutate() {
      while (null == (rand.nextBoolean() ? changeAttribute() : changeRuleSet())) {};
      return this; // always succeeds
   }

   /**
    * Change an attribute (memory size, offense, defense), each with
    * equal probability. Always succeeds.
    * @return
    */
   private Program changeAttribute() {
      switch (rand.nextInt(3)) {
      case 0: // change size of memory
         if (memSize == 8 || rand.nextBoolean()) memSize++;
         else memSize--;
         break;
      case 1: // change offense
         if (offense == 1 || rand.nextBoolean()) offense++;
         else offense--;
         break;
      case 2: // change defense
         if (defense == 1 || rand.nextBoolean()) defense++;
         else defense--;
         break;
      };
      successes++; // always succeeds
      attempts++;
      return this;
   }

   private Program changeRuleSet() {
      int index = rand.nextInt(size()); // pick a random node
      Mutation m = MutationFactory.getMutation(rand.nextInt(6)); // and a random mutation
      return mutate(index, m);
   }

   @Override
   public Program mutate(int index, Mutation m) {
      int s = successes;
      Node target = preorder().get(index);
      if (m.applies(target) && m.apply(target)) successes++;
      attempts++;
      return successes > s ? this : null;
   }

   @Override
   public int possiblyMutate() {
      int s = successes;
      while (rand.nextInt(4) == 0) mutate();
      return successes - s;
   }

   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      for (Node n : children) {
         n.prettyPrint(sb);
         sb.append('\n');
      }
      return sb;
   }

}
