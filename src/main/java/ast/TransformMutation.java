package ast;

import java.util.Random;

import ast.Action;
import ast.BinaryCondition;
import ast.BinaryExpression;
import ast.Node;
import ast.Number;
import ast.Relation;
import ast.Sensor;
import parse.TokenType;

/**
 * 4. Transform mutation:
 * The node is replaced with a randomly chosen node of the same kind
 * (for example, replacing attack with eat, or + with *), but its
 * children remain the same. Literal integer constants are adjusted
 * up or down by the value of java.lang.Integer.MAX_VALUE/r.nextInt(),
 * where legal, and where r is a java.util.Random object.
 */
public class TransformMutation implements Mutation {
   
   Random rand = new Random();

   @Override
   public boolean equals(Mutation m) {
      return m instanceof TransformMutation;
   }

   @Override
   public boolean apply(Node node) {
      
      if (node instanceof Sensor) {
         Sensor s = (Sensor)node;
         final TokenType[] sensorTypes = { TokenType.RANDOM, TokenType.AHEAD, TokenType.NEARBY };
         if (s.type == TokenType.SMELL) return false;
         int i = rand.nextInt(sensorTypes.length);
         while (s.type == sensorTypes[i]) i = rand.nextInt(sensorTypes.length);
         s.type = sensorTypes[i];
         return true;         
      }

      if (node instanceof BinaryExpression) {
         BinaryExpression b = (BinaryExpression)node;
         final TokenType[] exprTypes = { TokenType.PLUS, TokenType.MINUS,
                  TokenType.MUL, TokenType.DIV, TokenType.MOD };
         int i = rand.nextInt(exprTypes.length);
         while (b.op == exprTypes[i]) i = rand.nextInt(exprTypes.length);
         b.op = exprTypes[i];
         return true;         
      }

      if (node instanceof BinaryCondition) {
         BinaryCondition b = (BinaryCondition)node;
         if (b.op == TokenType.AND) b.op = TokenType.OR;
         else b.op = TokenType.AND;
         return true;         
      }

      if (node instanceof Action) {
         Action a = (Action)node;
         if (!a.hasChildren()) {
            final TokenType[] actionTypes = { TokenType.WAIT, TokenType.FORWARD, TokenType.BACKWARD,
                     TokenType.LEFT, TokenType.RIGHT, TokenType.EAT, TokenType.ATTACK,
                     TokenType.GROW, TokenType.BUD, TokenType.MATE };
            int i = rand.nextInt(actionTypes.length);
            while (a.type == actionTypes[i]) i = rand.nextInt(actionTypes.length);
            a.type = actionTypes[i];
         } else {
            if (a.type == TokenType.TAG) a.type = TokenType.SERVE;
            else a.type = TokenType.TAG;
         }
         return true;
      }

      if (node instanceof Relation) {
         Relation r = (Relation)node;
         final TokenType[] relTypes = { TokenType.LE, TokenType.LT,
                  TokenType.GE, TokenType.GT, TokenType.EQ, TokenType.NE };
         int i = rand.nextInt(relTypes.length);
         while (r.rel == relTypes[i]) i = rand.nextInt(relTypes.length);
         r.rel = relTypes[i];
         return true;         
      }
      
      if (node instanceof Number) {
         Number n = (Number)node;
         int adjust = java.lang.Integer.MAX_VALUE/(1 + rand.nextInt());
         int newValue = n.value + (rand.nextBoolean() ? adjust : -adjust);
         if (newValue < 0) newValue = 0;
         n.value = newValue;
         return true;         
      }
      
      return false;
   }

   @Override
   public boolean applies(Node node) {
      return node instanceof Sensor
          || node instanceof BinaryExpression
          || node instanceof BinaryCondition
          || node instanceof Action
          || node instanceof Relation
          || node instanceof Number;
   }

}
