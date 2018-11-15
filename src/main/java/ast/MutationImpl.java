package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import ast.Node;

public abstract class MutationImpl implements Mutation {

	Random rand = new Random();

	/**
	 * Get a node similar to the given node, where similarity
	 * is defined by the given {@code Predicate}.
	 * 
	 * @param node A prototypical node
	 * @param f The {@code Predicate} that determines similarity.
	 * @return A node similar to but not == the given node.
	 */
	protected Node getSimilar(Node node, Predicate<Node> f) {
		Node root = node;
		while (root.getParent() != null) root = root.getParent();
		List<Node> preorder = ((AbstractNode)root).preorder();
		List<Node> filtered = filter(preorder, f);
		assert 0 < filtered.size();
		assert filtered.remove(node);
		assert filtered.size() < preorder.size();
		if (filtered.isEmpty()) return null;
		return filtered.get(rand.nextInt(filtered.size()));
	}

	/**
	 * A generic method to filter out all elements of a {@code List}
	 * that do not satisfy the property specified by the given
	 * {@code Predicate}.
	 * 
	 * @param list The initial {@code List} to filter
	 * @param f The {@code Predicate} that tells whether to keep an element
	 * @return The output list of all elements in the original list
	 * satisfying the {@code Predicate}.
	 * 
	 * Example: Given a list of integers, filter out the odd elements:
	 * 
	 * filter(list, n -> n%2 == 0);
	 */
	private <T> List<T> filter(List<T> list, Predicate<T> f) {
		List<T> filtered = new ArrayList<>();
		for (T x : list) {
			if (f.test(x)) filtered.add(x);
		}
		return filtered;
	}

}

