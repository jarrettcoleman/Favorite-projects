package parse;

import java.io.IOException;
import java.io.Reader;

import ast.Action;
import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.NegatedFactor;
import ast.Program;
import ast.ProgramImpl;
import ast.Register;
import ast.Rule;
import ast.Sensor;
import ast.Update;
import ast.BinaryCondition;
import ast.BinaryExpression;
import ast.Relation;
import ast.Number;
import exceptions.SyntaxError;

class ParserImpl implements Parser {

   @Override
   public Program parse(Reader r) {
      Tokenizer t = new Tokenizer(r);
      Program p = null;
      try {
         p = parseProgram(t);
      } catch (SyntaxError e) {
         String msg = String.format("Syntax error at line %d: %s\n", t.lineNumber(), e.getMessage());
         System.out.println(msg);
         p = null;
      } finally {
         try {
            r.close();
         } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
         }
      }
      return p;
   }

   /**
    * Parses a program from the stream of tokens provided by the Tokenizer,
    * consuming tokens representing the program. All following methods with a
    * name "parseX" have the same spec except that they parse syntactic form X.
    * 
    * @return the created AST
    * @throws SyntaxError
    *            if there the input tokens have invalid syntax
    */
   public static ProgramImpl parseProgram(Tokenizer t) throws SyntaxError {
      ProgramImpl program = new ProgramImpl();
      while (t.hasNext()) {
         Rule rule = parseRule(t);
         program.add(rule);
      }
      return program;
   }

   public static Rule parseRule(Tokenizer t) throws SyntaxError {
      Condition condition = parseCondition(t);
      consume(t, TokenType.ARR);
      Command command = parseCommand(t);
      consume(t, TokenType.SEMICOLON);
      return new Rule(condition, command);
   }

   private static Command parseCommand(Tokenizer t) throws SyntaxError {
      Command command = new Command();
      Token token = t.peek();
      while (token.isMemSugar() || token.getType() == TokenType.MEM) {
         command.addUpdate(parseUpdate(t));
         token = t.peek();
      }
      if (token.isAction()) {
         command.addAction(parseAction(t));
      }
      if (!command.hasChildren()) throw new SyntaxError("Update or action expected");
      return command;
   }

   private static Update parseUpdate(Tokenizer t) throws SyntaxError {
      Token token = t.next();
      Register reg;
      if (token.isMemSugar()) {
         reg = new Register(token.getType());
      } else {
         assert token.getType() == TokenType.MEM;
         reg = new Register(parseBracketExpr(t));
      }
      consume(t, TokenType.ASSIGN);
      Expr arg = parseExpression(t);
      return new Update(reg, arg);
   }

   private static Action parseAction(Tokenizer t) throws SyntaxError {
      Token token = t.next();
      assert token.isAction();
      if (token.getType() == TokenType.TAG || token.getType() == TokenType.SERVE)
         return new Action(token.getType(), parseBracketExpr(t));
      return new Action(token.getType());
   }

   public static Condition parseCondition(Tokenizer t) throws SyntaxError {
      Condition c = parseConjunction(t);
      while (t.peek().getType() == TokenType.OR) {
         consume(t, TokenType.OR); // scan past the or
         c = new BinaryCondition(TokenType.OR, c, parseConjunction(t));
      }
      return c;
   }

   private static Condition parseConjunction(Tokenizer t) throws SyntaxError {
      Condition c = parseRelation(t);
      while (t.peek().getType() == TokenType.AND) {
         consume(t, TokenType.AND); // scan past the and
         c = new BinaryCondition(TokenType.AND, c, parseRelation(t));
      }
      return c;
   }

   private static Condition parseRelation(Tokenizer t) throws SyntaxError {
      if (t.peek().getType() == TokenType.LBRACE) {
         consume(t, TokenType.LBRACE); // scan past the brace
         Condition c = parseCondition(t);
         consume(t, TokenType.RBRACE);
         return c;
      }
      Expr e1 = parseExpression(t);
      if (!t.hasNext() || !t.peek().isRelation()) throw new SyntaxError("Relational operator expected");
      Token rel = t.next();
      Expr e2 = parseExpression(t);
      return new Relation(rel.getType(), e1, e2);
   }

   public static Expr parseExpression(Tokenizer t) throws SyntaxError {
      Expr term = parseTerm(t);
      while (t.peek().isAddOp()) {
         term = new BinaryExpression(t.next().getType(), term, parseTerm(t));
      }
      return term;
   }

   public static Expr parseTerm(Tokenizer t) throws SyntaxError {
      Expr factor = parseFactor(t);
      while (t.peek().isMulOp()) {
         factor = new BinaryExpression(t.next().getType(), factor, parseFactor(t));
      }
      return factor;
   }

   public static Expr parseFactor(Tokenizer t) throws SyntaxError {
      Token token = t.next();
      if (token.isNum()) {
         return new Number(token.toNumToken().getValue());
      }
      if (token.isSensor()) {
         Expr e = token.getType() == TokenType.SMELL ? null : parseBracketExpr(t);
         return new Sensor(token.getType(), e);
      }
      if (token.isMemSugar()) {
         return new Register(token.getType());
      }
      if (token.getType() == TokenType.MEM) {
         return new Register(parseBracketExpr(t));
      }
      if (token.getType() == TokenType.LPAREN) {
         Expr e = parseExpression(t);
         consume(t, TokenType.RPAREN);
         return e;
      }
      if (token.getType() == TokenType.MINUS) {
         Expr e = parseFactor(t);
         return new NegatedFactor(e);
      }
      throw new SyntaxError("Expression expected");
   }

   private static Expr parseBracketExpr(Tokenizer t) throws SyntaxError {
      consume(t, TokenType.LBRACKET);
      Expr e = parseExpression(t);
      consume(t, TokenType.RBRACKET);
      return e;
   }

   /**
    * Consumes a token of the expected type.
    * 
    * @throws SyntaxError
    *            if the wrong kind of token is encountered.
    */
   public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {
      if (!t.hasNext() || t.next().getType() != tt)
         throw new SyntaxError(String.format("%s expected", tt.toString()));
   }
}
