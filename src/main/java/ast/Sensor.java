package ast;

import parse.TokenType;

public class Sensor extends Expr {

   protected TokenType type;

   public Sensor(TokenType tt, Expr expr) {
      this.type = tt;
      if (expr != null) {
         expr.parent = this;
         children.add(expr);
      }
   }
   
   public TokenType getSensorType() {
	   return type;
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
