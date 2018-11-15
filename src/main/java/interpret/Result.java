package interpret;

import ast.Action;
import ast.Rule;
import parse.TokenType;

/**
 * The result of interpreting a critter program in a given state. This is just a
 * starting point and may be changed as much as you like.
 */
public class Result {

	private Rule r;
	private Action a;
	
   public Result(Rule rule, Action a) {
      // TODO: implement
	   this.r = rule;
	   this.a = a;
   }
   
   public Rule getRule() {
	   return r;
   }
   
   public Action getAction() {
	   if (a == null) return new Action(TokenType.WAIT);
	   return a;
   }
}
