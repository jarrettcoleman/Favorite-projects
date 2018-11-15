package ast;

public class NegatedFactor extends Expr {

   public NegatedFactor(Expr expr) {
      expr.parent = this;
      children.add(expr);
   }
   
   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      sb.append("-");
      return fst().prettyPrint(sb);
   }

}
