package interpret;

import java.util.*;
import ast.*;
import ast.Number;
import model.Constants;
import parse.TokenType;

/**
 * A class for interpreting a critter program. This is just a starting
 * point and may be changed as much as you like.
 */
public class Interpreter {

	private CritterState cs;

	/**
	 * Execute program {@code p} starting from state {@code s} until either the
	 * maximum number of rules per turn is reached or some rule whose command
	 * contains an action is executed. Returns a result containing the action to
	 * be performed; the action may be null if the maximum number of rules per
	 * turn was exceeded.
	 * 
	 * @param p
	 *           the program to execute
	 * @param s
	 *           the state in which to execute it
	 * @return a {@code Result} containing the action to be performed and the
	 *         rule in which the action occurred; either of these may be null if
	 *         the maximum number of rules per turn was exceeded.
	 */
	Result interpret(Program p, State s) {
		cs = (CritterState) s;
		Rule resultRule = null;
		Action act = null;

		List<Node> rules = p.getChildren();
		for (cs.mem[5] = 1; cs.mem[5] < rules.size(); cs.mem[5]++) {
			Rule r = (Rule) rules.get(cs.mem[5] - 1);
			if (isValid((Condition) r.fst())) {
				Command cmd = (Command) r.snd();
				act = process(cmd);
				if (act != null) {
					resultRule = r;
					return new Result(resultRule, act);
				}
			}
			if (cs.mem[5] >= Constants.maxRulesPerTurn) break;
		}
		return new Result(null, new Action(TokenType.WAIT));
	}
	
	/**
	 * Determines if the condition {@code cd} is true
	 */
	boolean isValid(Condition cd) {
		if (cd instanceof Relation) {
			return checkRelation((Relation) cd);
		}
		else if (cd instanceof BinaryCondition) {
			TokenType binaryOp = ((BinaryCondition) cd).getBinaryOp();
			if (binaryOp.equals(TokenType.AND))
				return (isValid((Condition) cd.fst()) 
						&& isValid((Condition) cd.snd()));
			else if (binaryOp.equals(TokenType.OR))
				return (isValid((Condition) cd.fst()) 
						|| isValid((Condition) cd.snd()));
		}
		else
			throw new IllegalStateException("Invalid Condition");
		return false;
	}

	/**
	 * Determines if the relation {@code r} is true
	 */
	private boolean checkRelation(Relation r) {
		int firstExp = evaluateExpr((Expr) r.fst());
		int secondExp = evaluateExpr((Expr) r.snd());
		TokenType relOp = r.getRelOp();
		switch(relOp) {
		case LT:
			return (firstExp < secondExp);
		case LE:
			return (firstExp <= secondExp);
		case EQ:
			return (firstExp == secondExp);
		case GE:
			return (firstExp >= secondExp);
		case GT:
			return (firstExp > secondExp);
		case NE:
			return (firstExp != secondExp);
		default:
			throw new IllegalStateException("Invalid Relational Operator");
		}
	}

	/**
	 * Evaluates the integer value of the expression {@code e}
	 */
	public int evaluateExpr(Expr e) {
		if (!(e instanceof BinaryExpression)) {
			return evaluateFactor(e);
		}
		int firstExp = evaluateExpr((Expr) e.fst());
		int secondExp = evaluateExpr((Expr) e.snd());
		TokenType op = ((BinaryExpression) e).getOp();
		switch(op) {
		case PLUS:
			return (firstExp + secondExp);
		case MINUS:
			return (firstExp - secondExp);
		case MUL:
			return (firstExp * secondExp);
		case DIV:
			if (secondExp == 0) return 0;
			return (firstExp / secondExp);
		case MOD:
			if (secondExp == 0) return 0;
			return (firstExp % secondExp);
		default:
			throw new IllegalStateException("Invalid Operator");
		}
	}

	/**
	 * Evaluates the integer value of the factor {@code e}
	 */
	private int evaluateFactor(Expr e) {
		if (e instanceof Number)
			return ((Number) e).getValue();
		else if (e instanceof Register) {
			Register reg = (Register) e;
			if (reg.getAbbrev() != null) {
				String memSug = reg.getAbbrev();
				return getCritterStateData(memSug);
			}
			int n = evaluateExpr((Expr) reg.fst());
			if (n >= cs.mem.length)
				return 0;
			return cs.mem[n];
		}
		else if (e instanceof Sensor) {
			Sensor s = (Sensor) e;
			TokenType sensorType = s.getSensorType();
			int n = 0;
			if (s.hasChildren())
				n = evaluateExpr((Expr) s.fst());
			return getSensorData(sensorType, n);
		}
		else if (e instanceof NegatedFactor) {
			NegatedFactor ne = (NegatedFactor) e;
			int n = evaluateExpr((Expr) ne.fst());
			return (-1 * n);
		}
		else
			throw new IllegalStateException("Invalid Factor");
	}

	/**
	 * Retrieves the critter state's data from its syntactic sugar
	 */
	private int getCritterStateData(String memSugar) {
		switch(memSugar) {
		case "MEMSIZE":
			return cs.mem(0);
		case "DEFENSE":
			return cs.mem(1);
		case "OFFENSE":
			return cs.mem(2);
		case "SIZE":
			return cs.mem(3);
		case "ENERGY":
			return cs.mem(4);
		case "PASS":
			return cs.mem(5);
		case "TAG":
			return cs.mem(6);
		case "POSTURE":
			return cs.mem(7);
		default:
			throw new IllegalStateException("Invalid Syntactic Sugar");
		}
	}
	
	private int getSensorData(TokenType sensor, int n) {
		if (sensor.equals(TokenType.SMELL)) return 0;
		if (sensor.equals(TokenType.NEARBY)) {
			int direction = n % 6;
			return cs.nearby(direction);
		}
		if (sensor.equals(TokenType.AHEAD)) {
			if (n < 0) n = 0;
			return cs.ahead(n);
		}
		if (sensor.equals(TokenType.RANDOM)) {
			if (n < 2) return 0;
			return (new Random()).nextInt(n);
		}
		throw new IllegalArgumentException("Invalid Sensor");
	}
	
	private Action process(Command cmd) {
		List<Node> tasks = cmd.getChildren();
		for (int ix = 0; ix < tasks.size(); ix++) {
			Node n = tasks.get(ix);
			if (n instanceof Update) {
				Update u = (Update) n;
				update(u);
			}
			else if (n instanceof Action) {
				return (Action) n;
			}
		}
		return null;
	}
	
	private void update(Update u) {
		Register reg = (Register) u.fst();
		Expr ex = (Expr) u.snd();
		int value = evaluateExpr(ex);
		if (reg.getAbbrev() != null) {
			String s = reg.getAbbrev();
			switch(s) {
			case "MEMSIZE":
				return;
			case "DEFENSE":
				return;
			case "OFFENSE":
				return;
			case "SIZE":
				return;
			case "ENERGY":
				return;
			case "PASS":
				return;
			case "TAG":
				return;
			case "POSTURE":
				if (value < 0 || value > 99) return;
				cs.mem[7] = value;
			}
		}
		else {
			int n = evaluateExpr((Expr) reg.fst());
			if (n >= cs.mem.length)
				return;
			cs.mem[n] = value;
		}
	}

}
