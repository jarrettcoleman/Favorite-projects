package ast;

/**
 * A representation of a critter rule.
 */
public class Rule extends AbstractNode {
   
   public Rule(Condition condition, Command command) {
      condition.parent = this;
      command.parent = this;
      children.add(condition);
      children.add(command);
   }
   
   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      fst().prettyPrint(sb);
      sb.append(" --> ");
      snd().prettyPrint(sb);
      return sb.append(';');
   }

}
