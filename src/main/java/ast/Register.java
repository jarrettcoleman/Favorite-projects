package ast;

import parse.TokenType;

public class Register extends Expr {

   private String abbrev;

   /**
    * Constructor for registers specified by syntactic sugar; MEMSIZE, etc.
    * @param tt the type of the sugar token
    */
   public Register(TokenType tt) {
      this.abbrev = tt.toString();
   }
   
   public String getAbbrev() {
	   return abbrev;
   }

   /**
    * Constructor for registers specified by mem[expr]
    * @param expr the index expression
    */
   public Register(Expr expr) {
      expr.parent = this;
      abbrev = null; // no sugar
      children.add(expr);
   }
   
   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      if (abbrev != null) {
         sb.append(abbrev);
      } else {
         sb.append("mem[");
         fst().prettyPrint(sb);
         sb.append(']');
      }
      return sb;
   }

}
