package ast;

import parse.TokenType;

public class Update extends AbstractNode {

   public Update(Register reg, Expr expr) {
      reg.parent = this;
      expr.parent = this;
      children.add(reg);
      children.add(expr);
   }
   
   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      fst().prettyPrint(sb);
      sb.append(' ');
      sb.append(TokenType.ASSIGN.toString());
      sb.append(' ');
      return snd().prettyPrint(sb);
   }

}
