/*
    This class provides a recursive descent parser
    for Corgi (the new version),
    creating a parse tree which can be interpreted
    to simulate execution of a Corgi program
*/

import java.util.*;
import java.io.*;

public class Parser {

   private Lexer lex;

   public Parser( Lexer lexer ) {
      lex = lexer;
   }

   public Node parseDefs() {
      System.out.println("-----> parsing <defs>:");
      Node first = parseDef();
      Token token = lex.getNextToken();
      if( token.isKind("eof") ) {
        return new Node("def", first, null, null);
      }
      else {
        lex.putBackToken( token );
        Node second = parseDefs();
        return new Node("defs", first, second, null);
      }
   }

   public Node parseDef() {
      System.out.println("-----> parsing <def>:");
      Token token = lex.getNextToken();
      errorCheck( token, "LPAREN", "(" );
      token = lex.getNextToken();
      errorCheck( token, "define");
      token = lex.getNextToken();
      errorCheck( token, "LPAREN", "(" );
      token = lex.getNextToken();
      errorCheck( token, "NAME");
      String functionName = token.getDetails();
      token = lex.getNextToken();
      if( token.matches("RPAREN", ")") ) {
        Node first = parseExpr();
        token = lex.getNextToken();
        errorCheck( token, "RPAREN");
        return new Node("def", functionName, first, null, null);
      }
      else {
        lex.putBackToken(token);
        Node first = parseParams();
        token = lex.getNextToken();
        errorCheck( token, "RPAREN", ")");
        Node second = parseExpr();
        token = lex.getNextToken();
        errorCheck( token, "RPAREN", ")");
        return new Node("def", functionName, first, second, null);
      }
   }

   public Node parseParams() {
      System.out.println("-----> parsing <params>:");
      Token token = lex.getNextToken();
      String paramName = token.getDetails();
      errorCheck(token, "NAME");
      token = lex.getNextToken();
      if( token.matches("RPAREN", ")") ) {
        lex.putBackToken(token);
        return new Node("param", paramName, null, null, null);
      }
      else {
        lex.putBackToken(token);
        Node first = parseParams();
        return new Node("param", paramName, first, null, null);
      }
   }// parseParams


   public Node parseExpr() {
      System.out.println("-----> parsing <expr>:");
      Token token = lex.getNextToken();
      if( token.isKind("NUMBER") ) {
        return new Node("NUMBER", token.getDetails(), null, null, null);
      }
      else if( token.isKind("NAME") ) {
        //return new Node("NAME", token.getDetails(), null, null, null);
        return new Node("NAME", token.getDetails(), null, null, null);
      }
      else {
        lex.putBackToken(token);
        Node first = parseList();
        token = lex.getNextToken(); //get closing ")" for list
        return new Node("list", first, null, null);
      }
   }// <params>

   private Node parseList() {
      System.out.println("-----> parsing <list>:");
      Token token = lex.getNextToken();
      errorCheck(token, "LPAREN", "(");
      token = lex.getNextToken();
      if( token.matches("RPAREN", ")") ) {
        lex.putBackToken(token);
        //return new Node(token);
        return null;
      }
      else if( token.matches("NUMBER") ) { //else if a list of NUMBERs
        String theList = token.getDetails(); //add first NUMBER to list
        //while( token.matches("NUMBER") ) { //while token is a NUMBER
        boolean isStillCreatingList = true;
        while( isStillCreatingList ) { //while token is a NUMBER
          token = lex.getNextToken();
          if(token.matches("RPAREN", ")")) {
            isStillCreatingList = false;
            lex.putBackToken(token);
          }
          else {
            theList = theList + " " + token.getDetails();
          }
        }
        return new Node("listnumbers", theList, null, null, null);
      }
      else if( token.matches("NAME", "if") ) {
        Node first = parseExpr();
        Node second = parseExpr();
        Node third = parseExpr();
        return new Node("if", first, second, third);
      }
      else if( token.matches("NAME", "plus") ) {
        Node first = parseExpr();
        Node second = parseExpr();
        return new Node("plus", first, second, null);
      }
      else if( token.matches("NAME", "minus") ) {
        Node first = parseExpr();
        Node second = parseExpr();
        return new Node("minus", first, second, null);
      }
      else if( token.matches("NAME", "div") ) {
        Node first = parseExpr();
        Node second = parseExpr();
        return new Node("div", first, second, null);
      }
      else if( token.matches("NAME", "eq") ) {
        Node first = parseExpr();
        Node second = parseExpr();
        return new Node("eq", first, second, null);
      }
      else if( token.matches("NAME", "or") ) {
        Node first = parseExpr();
        Node second = parseExpr();
        return new Node("or", first, second, null);
      }
      else if( token.matches("NAME", "ins") ) {
        Node first = parseExpr();
        Node second = parseExpr();
        return new Node("ins", first, second, null);
      }
      else if( token.matches("NAME", "first") ) {
        Node first = parseExpr();
        return new Node("first", first, null, null);
      }
      else if( token.matches("NAME", "rest") ) {
        Node first = parseExpr();
        return new Node("rest", first, null, null);
      }
      else if( token.matches("NAME", "null") ) {
        Node first = parseExpr();
        return new Node("null", first, null, null);
      }
      else if( token.matches("NAME", "quote") ) {
        token = lex.getNextToken(); //get starting "(" after "quote"
        errorCheck(token, "LPAREN", "("); //check its "("
        int leftParenCount = 1; //starts at 1 to account for opening "("
        int rightParenCount = 0;
        //String theList = "("; //include first NUMBER
        String theList = "";
        while(leftParenCount > rightParenCount) {
          token = lex.getNextToken();
          if(token.matches("LPAREN", "(")) {
            leftParenCount++;
          }
          else if(token.matches("RPAREN", ")")) {
            rightParenCount++;
          }
          else { //else its a NUMBER list
            theList = theList + token.getDetails() + " ";
          }
        }
        //theList = theList + ")";
        return new Node("quote", theList, null, null, null);
      }
      // else if( token.matches("NAME", "quote") ) {
      //   Node first = parseExpr(); //parse as list of NUMBERs (see below else if)
      //   return new Node("quote", first, null, null);
      // }
      // else if( token.matches("NUMBER") ) { //possibly a list of numbers
      //   int leftParenCount = 1; //starts at 1 to account for opening "("
      //   int rightParenCount = 0;
      //   String theList = "(" + token.getDetails(); //include first NUMBER
      //   while(leftParenCount > rightParenCount) {
      //     token = lex.getNextToken();
      //     if(token.matches("LPAREN", "(")) {
      //       leftParenCount++;
      //     }
      //     else if(token.matches("RPAREN", ")")) {
      //       rightParenCount++;
      //     }
      //     else { //else its a NUMBER
      //       theList = theList + " " + token.getDetails();
      //     }
      //   }
      //   lex.putBackToken(token); //put back closing ")" for list parsing
      //   theList = theList + ")";
      //   return new Node("quote", theList, null, null, null);
      // }
      else if( token.isKind("NAME") ) { //user defined function
        System.out.println("!!!!!!!!!!! user function");
        String functionName = token.getDetails();
        Node first = parseExpr();
        token = lex.getNextToken();
        if( token.matches("RPAREN", ")") ) { //only 1 expression
          lex.putBackToken(token);
          return new Node("userfunction", functionName, first, null, null);
        }
        else {
          lex.putBackToken(token);
          Node second = parseExpr();
          return new Node("userfunction", functionName, first, second, null);
        }
      }

      // possible code for user functions with no parameters
      // else if( token.isKind("NAME") ) { //user defined function
      //   System.out.println("!!!!!!!!!!! user function");
      //   String functionName = token.getDetails();
      //   token = lex.getNextToken();
      //   if( token.matches("RPAREN", ")") ) { //no expressions
      //     return new Node("userfunction", functionName, null, null, null);
      //   }
      //   else {
      //     lex.putBackToken(token);
      //     Node first = parseExpr();
      //     token = lex.getNextToken();
      //     if( token.matches("RPAREN", ")") ) { //only 1 expression
      //       lex.putBackToken(token);
      //       return new Node("userfunction", functionName, first, null, null);
      //     }
      //     else { //else there are 2 expressions
      //       lex.putBackToken(token);
      //       Node second = parseExpr();
      //       return new Node("userfunction", functionName, first, second, null);
      //     }
      //   }
      // }

      else {
        System.out.println("!!!!!! unknown function");
        return null;
      }
   } //parseList

   private Node parseItems() {
      System.out.println("-----> parsing <items>:");
      Node first = parseExpr();
      Token token = lex.getNextToken();
      if( token.matches("RPAREN", ")") ) {
        lex.putBackToken(token);
        return new Node("items", first, null, null);
      }
      else {
        lex.putBackToken(token);
        Node second = parseItems();
        return new Node("items", first, second, null);
      }
   } //parseItems

   private Node parseFuncCall() {
      System.out.println("-----> parsing <funcCall>:");

      Token name = lex.getNextToken(); // function name
      errorCheck( name, "var" );

      Token token = lex.getNextToken();
      errorCheck( token, "single", "(" );

      token = lex.getNextToken();

      if ( token.matches( "single", ")" ) ) {// no args
         return new Node( "funcCall", name.getDetails(), null, null, null );
      }
      else {// have args
         lex.putBackToken( token );
         Node first = parseArgs();
         return new Node( "funcCall", name.getDetails(), first, null, null );
      }

   }// <funcCall>

   private Node parseArgs() {
      System.out.println("-----> parsing <args>:");
      Node first = parseExpr();
      Token token = lex.getNextToken();
      if ( token.matches( "single", ")" ) ) {// no more args
         return new Node( "args", first, null, null );
      }
      else if ( token.matches( "single", "," ) ) {// have more args
         Node second = parseArgs();
         return new Node( "args", first, second, null );
      }
      else {// error
         System.out.println("expected , or ) and saw " + token );
         System.exit(1);
         return null;
      }
   }// <args>

  // check whether token is correct kind
  private void errorCheck( Token token, String kind ) {
    if( ! token.isKind( kind ) ) {
      System.out.println("Error:  expected " + token +
                         " to be of kind " + kind );
      System.exit(1);
    }
  }

  // check whether token is correct kind and details
  private void errorCheck( Token token, String kind, String details ) {
    if( ! token.isKind( kind ) ||
        ! token.getDetails().equals( details ) ) {
      System.out.println("Error:  expected " + token +
                          " to be kind= " + kind +
                          " and details= " + details );
      System.exit(1);
    }
  }

}
