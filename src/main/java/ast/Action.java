package ast;

import parse.TokenType;

public class Action extends AbstractNode {
   
   public TokenType type;

   public Action(TokenType type) {
      this.type = type;
   }

   public Action(TokenType type, Expr expr) {
      this(type);
      expr.parent = this;
      children.add(expr);
   }
   
   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      sb.append(type.toString());
      if (hasChildren()) {
         sb.append('[');
         fst().prettyPrint(sb);
         sb.append(']');
      }
      return sb;
   }

}
