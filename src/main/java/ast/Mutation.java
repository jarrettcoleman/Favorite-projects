package ast;

import ast.Node;

/**
 * A mutation to the AST
 */
public interface Mutation {

   /**
    * Compares the type of this mutation to {@code m}
    * 
    * @param m
    *           The mutation to compare with
    * @return Whether this mutation is the same type as {@code m}
    */
   boolean equals(Mutation m);

   /**
    * Apply this mutation to the given {@code Node}.
    * 
    * @param node
    *           The {@code Node} to apply the mutation to.
    * @return Whether the mutation was successful.
    */
   boolean apply(Node node);

   /**
    * Test whether this mutation applies to the given {@code Node}.
    * 
    * @param node
    *           The {@code Node} to apply the mutation to.
    * @return Whether the mutation applies.
    */
   boolean applies(Node node);
}
