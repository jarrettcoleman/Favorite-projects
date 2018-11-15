package ast;

import parse.TokenType;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */
public class BinaryCondition extends Condition {

   protected TokenType op;

   /**
    * Create an AST representation of left op right.
    * 
    * @param left
    * @param op
    * @param right
    */
   public BinaryCondition(TokenType op, Condition left, Condition right) {
      this.op = op;
      left.parent = this;
      right.parent = this;
      children.add(left);
      children.add(right);
   }
   
   public TokenType getBinaryOp() {
	   return op;
   }
   
   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      boolean protect = protect();
      if (protect) sb.append('{');
      fst().prettyPrint(sb);
      sb.append(' ');
      sb.append(op.toString().toLowerCase());
      sb.append(' ');
      snd().prettyPrint(sb);
      if (protect) sb.append('}');
      return sb;
   }
   
   private boolean protect() {
      assert parent != null;
      if (!(parent instanceof BinaryCondition)) return false;
      BinaryCondition par = (BinaryCondition)parent;
      if (op == TokenType.OR && par.op == TokenType.AND) return true;
      if (op != par.op) return false;
      // same precedence - check whether this is right child of parent
      return this == par.snd();
   }

}
