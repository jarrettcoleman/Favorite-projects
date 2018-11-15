package ast;

/**
 * A factory that produces the Mutation objects corresponding to each mutation
 */
public class MutationFactory {
   public static Mutation getRemove() {
      return new RemoveMutation();
   }

   public static Mutation getSwap() {
      return new SwapMutation();
   }

   public static Mutation getReplace() {
      return new ReplaceMutation();
   }

   public static Mutation getTransform() {
      return new TransformMutation();
   }

   public static Mutation getInsert() {
      return new InsertMutation();
   }

   public static Mutation getDuplicate() {
      return new DuplicateMutation();
   }
   
   public static Mutation getMutation(int index) {
      assert 0 <= index && index < 6;
      switch (index) {
      case 0: return MutationFactory.getRemove();
      case 1: return MutationFactory.getSwap();
      case 2: return MutationFactory.getReplace();
      case 3: return MutationFactory.getTransform();
      case 4: return MutationFactory.getInsert();
      default: return MutationFactory.getDuplicate();
      }
   }
   
}
