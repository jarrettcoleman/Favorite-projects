package ast;

public class Number extends Expr {
   
   int value; // package visibility - can be reset by mutations

   public Number(int value) {
      this.value = value;
   }
   
   public int getValue() {
	   return value;
   }

   @Override
   public StringBuilder prettyPrint(StringBuilder sb) {
      return sb.append(String.valueOf(value));
   }

}
