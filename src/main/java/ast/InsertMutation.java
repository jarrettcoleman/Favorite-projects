package ast;

import java.util.Random;

import ast.BinaryCondition;
import ast.BinaryExpression;
import ast.Condition;
import ast.Expr;
import ast.NegatedFactor;
import ast.Node;
import ast.Register;
import ast.Sensor;
import ast.Update;
import parse.TokenType;

/**
 * 5. Insert mutation:
 * A newly created node is inserted as the parent of the mutated node. The old
 * parent of the mutated node becomes the parent of the inserted node, and the
 * mutated node becomes a child of the inserted node. If the inserted node has
 * more than one child, the children that are not the original node are copies
 * of randomly chosen nodes of the right kind from the entire rule set.
 */
public class InsertMutation extends MutationImpl {

	Random rand = new Random();

	@Override
	public boolean equals(Mutation m) {
		return m instanceof InsertMutation;
	}

	@Override
	public boolean apply(Node node) {
		Node parent = node.getParent();
		assert parent.getChildren().contains(node); // sanity check
		int index = parent.getChildren().indexOf(node);
		Node c = null;
		Node n = null;
		// lhs of an assignment?
		boolean assg = parent instanceof Update && node == ((Update) parent).fst();

		if (node instanceof Condition) {
			n = getSimilar(node, m -> m instanceof Condition);
			if (n == null) return false;
			Condition c1 = (Condition) n.clone();
			Condition c2 = (Condition) node;
			TokenType op = rand.nextBoolean() ? TokenType.AND : TokenType.OR;
			c = rand.nextBoolean() ?
					new BinaryCondition(op, c1, c2) : new BinaryCondition(op, c2, c1);
		}

		else if (node instanceof Expr && !assg && rand.nextBoolean()) {
			n = getSimilar(node, m -> m instanceof Expr);
			if (n == null) return false;
			final TokenType[] ops = { TokenType.PLUS, TokenType.MINUS, TokenType.MUL,
					TokenType.DIV, TokenType.MOD };
			TokenType op = ops[rand.nextInt(ops.length)];
			Expr c1 = (Expr)n.clone();
			Expr c2 = (Expr)node;
			c = rand.nextBoolean() ?
					new BinaryExpression(op, c1, c2) : new BinaryExpression(op, c2, c1);
		}

		else {
			final TokenType[] ops = { TokenType.MEM, TokenType.NEARBY, TokenType.AHEAD,
					TokenType.RANDOM, TokenType.MINUS };
			TokenType op = assg ? TokenType.MEM : ops[rand.nextInt(ops.length)];
			switch (op) {
			case NEARBY:
			case AHEAD:
			case RANDOM:
				c = new Sensor(op, (Expr)node);
				break;
			case MEM:
				c = new Register((Expr)node);
				break;
			case MINUS:
				c = new NegatedFactor((Expr)node);
				break;
			default:
				return false;
			}
		}

		c.setParent(parent);
		parent.getChildren().set(index, c);
		return true;
	}

	@Override
	public boolean applies(Node node) {
		return node instanceof Expr || node instanceof Condition;
	}

}
