package ast;

import java.util.List;
import java.util.Random;

import ast.Action;
import ast.BinaryCondition;
import ast.Expr;
import ast.Node;
import ast.Register;
import ast.Rule;
import ast.Update;

/**
 * 1. Remove mutation:
 * The node, along with its descendants, is removed. If the parent
 * of the node being removed needs a replacement child, one of the node’s
 * children of the correct kind is randomly selected. For example,
 * a rule node is simply removed, while a binary operation node would be
 * replaced with either its left or its right child.
 */
public class RemoveMutation implements Mutation {
   
   Random rand = new Random();

   @Override
   public boolean equals(Mutation m) {
      return m instanceof RemoveMutation;
   }

   @Override
   public boolean apply(Node node) {
      Node parent = node.getParent();
      if (parent == null) return false;
      List<Node> siblings = parent.getChildren();
      assert siblings.contains(node); // sanity check
      int index = siblings.indexOf(node);

      if (node instanceof Rule || node instanceof Update || node instanceof Action) {
         if (siblings.size() <= 1) return false; // do not delete last
         siblings.remove(index);
         return true;
      }
      
      // check for left-hand side of an update
      if (node instanceof Register && parent instanceof Update
               && node == ((Update)parent).fst()) return false;

      if (node instanceof Expr || node instanceof BinaryCondition) {
         List<Node> children = node.getChildren();
         if (children.size() == 0) return false;
         Node n = children.get(rand.nextInt(children.size()));
         n.setParent(parent);
         siblings.set(index, n);
         return true;
      }
      return false;
   }

   @Override
   public boolean applies(Node node) {
      return node instanceof Rule
          || node instanceof Update
          || node instanceof Action
          || node instanceof BinaryCondition
          || node instanceof Expr;
   }

}
