package ast;

public class Command extends AbstractNode {
   
   public void addUpdate(Update update) {
      update.parent = this;
      children.add(update);
   }

   public void addAction(Action action) {
      action.parent = this;
      children.add(action);
   }

   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      for (Node n : children) {
         n.prettyPrint(sb);
         sb.append(' ');
      }
      if (hasChildren()) {
         // discard trailing space
         sb.setLength(sb.length() - 1);
      }
      return sb;
   }

}
