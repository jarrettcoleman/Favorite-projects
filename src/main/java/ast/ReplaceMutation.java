package ast;

import java.util.List;
import java.util.Random;

import ast.Node;
import ast.Program;

/**
 * 3. Replace mutation:
 * The node and its children are replaced with a randomly selected subtree
 * of the right kind. Randomly selected subtrees are chosen from
 * somewhere in the current AST. The entire AST subtree rooted at the
 * selected node is copied.
 */
public class ReplaceMutation extends MutationImpl {
   
   Random rand = new Random();

   @Override
   public boolean equals(Mutation m) {
      return m instanceof ReplaceMutation;
   }

   @Override
   public boolean apply(Node node) {
      Node n = getSimilar(node, m -> m.getClass() == node.getClass());
      if (n == null) return false;
      n = (Node)n.clone();
      Node parent = node.getParent();
      n.setParent(parent);
      List<Node> siblings = parent.getChildren();
      assert siblings.contains(node);
      int index = siblings.indexOf(node);
      siblings.set(index, n);      
      return true;
   }

   @Override
   public boolean applies(Node node) {
      return !(node instanceof Program);
   }

}
