package ast;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode implements Node, Cloneable {

   protected Node parent;
   protected List<Node> children = new ArrayList<>();
   
   @Override
   public String toString() {
      return prettyPrint(new StringBuilder()).toString();
   }
   
   /**
    * Create a preorder list of AST nodes
    */
   public List<Node> preorder() {
      return preorder(new ArrayList<Node>());
   }
   
   private List<Node> preorder(List<Node> list) {
      list.add(this);
      for (Node n : children) {
         ((AbstractNode)n).preorder(list);
      }
      return list;
   }
   
   public int size() {
      int s = 1;
      for (Node n : children) s += n.size();
      //assert s == preorder().size(); // sanity check
      return s;
   }
   
   public Node nodeAt(int index) {
      return preorder().get(index);
   }
   
   public Node fst() {
      return children.get(0);
   }

   public Node snd() {
      return children.get(1);
   }
   
   public boolean hasChildren() {
      return children.size() > 0;
   }
   
   public List<Node> getChildren() {
      return children;
   }
   
   public void setChildren(List<Node> c) {
      children = c;
   }
   
   public Node getParent() {
      return parent;
   }
   
   public void setParent(Node p) {
      parent = p;
   }
   
   /**
    * Called by subclasses to clone the object and its children.
    * Other specialized cloning operations are handled in the overriding
    * methods of subclasses if necessary.
    */
   @Override
   public Node clone() {
      Node copy = null;
      try {
         copy = (Node)super.clone();
      } catch (CloneNotSupportedException e) {
         assert false; // should never happen
      }
      
      // clone children
      copy.setChildren(new ArrayList<>());
      for (Node n : children) {
         Node child = n.clone();
         child.setParent(copy);
         copy.getChildren().add(child);
      }
      return copy;
   }
   
}
