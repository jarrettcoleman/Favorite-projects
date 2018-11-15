package ast;

import java.util.List;
import java.util.Random;

import ast.Action;
import ast.Command;
import ast.Node;
import ast.Program;
import ast.Rule;
import ast.Update;

/**
 * 6. Duplicate mutation:
 * For nodes with a variable number of children, a randomly selected
 * subtree of the right type (as in mutation type 3) is appended to the
 * end of the list of children. This applies to the root node, where
 * a new rule can be added, and also to command nodes, where the
 * sequence of updates can be extended with another update.
 */
public class DuplicateMutation implements Mutation {
   
   Random rand = new Random();

   @Override
   public boolean equals(Mutation m) {
      return m instanceof DuplicateMutation;
   }

   @Override
   public boolean apply(Node node) {
      if (node instanceof Program) {
         Program prog = (Program)node;
         List<Node> children = prog.getChildren();
         if (children.size() == 0) return false; // no rule to add
         Rule dup = (Rule)children.get(rand.nextInt(children.size()));
         dup = (Rule)dup.clone();
         children.add(dup);
         assert dup.parent == prog; // sanity check - clone should do this
         return true;
      }
      if (node instanceof Command) {
         Command command = (Command)node;
         assert (command.hasChildren()); // should always have an update or action
         List<Node> children = command.getChildren();
         boolean hasAction = children.get(children.size() - 1) instanceof Action;
         int range = hasAction ? children.size() - 1 : children.size();
         if (range == 0) return false; // no updates
         Update dup = (Update)children.get(rand.nextInt(range));
         dup = (Update)dup.clone();
         children.add(range, dup);
         assert dup.parent == command; // sanity check - clone should do this         
         return true;
      }
      return false;
   }

   @Override
   public boolean applies(Node node) {
      return node instanceof Program || node instanceof Command;
   }

}
