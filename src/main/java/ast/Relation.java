package ast;

import parse.TokenType;

public class Relation extends Condition {

   protected TokenType rel;

   public Relation(TokenType rel, Expr left, Expr right) {
      this.rel = rel;
      left.parent = this;
      right.parent = this;
      children.add(left);
      children.add(right);
   }
   
   public TokenType getRelOp() {
	   return rel;
   }
   
   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      fst().prettyPrint(sb);
      sb.append(' ');
      sb.append(rel.toString());
      sb.append(' ');
      return snd().prettyPrint(sb);
   }

}
