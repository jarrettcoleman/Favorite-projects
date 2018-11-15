package ast;

import parse.TokenCategory;
import parse.TokenType;

public class BinaryExpression extends Expr {

   protected TokenType op;

   public BinaryExpression(TokenType op, Expr left, Expr right) {
      this.op = op;
      left.parent = this;
      right.parent = this;
      children.add(left);
      children.add(right);
   }
   
   public TokenType getOp() {
	   return op;
   }

   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      // check precedence to see if should output parens
      boolean protect = protect();
      if (protect) sb.append('(');
      fst().prettyPrint(sb);
      sb.append(' ');
      sb.append(op.toString());
      sb.append(' ');
      snd().prettyPrint(sb);
      if (protect) sb.append(')');
      return sb;
   }
   
   private boolean protect() {
      assert parent != null;
      if (!(parent instanceof Expr)) return false;
      if (parent instanceof NegatedFactor) return true;
      if (!(parent instanceof BinaryExpression)) return false;
      BinaryExpression par = (BinaryExpression)parent;
      if (op.category() == TokenCategory.ADDOP && par.op.category() == TokenCategory.MULOP) return true;
      if (op.category() != par.op.category()) return false;
      // same precedence - check whether this is right child of parent
      return this == par.snd();
   }

}
