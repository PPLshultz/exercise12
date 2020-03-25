/*
    This class provides a recursive descent parser
    for SL3 creating a parse tree which can be
    interpreted to simulate execution of an SL3 program
*/

import java.util.*;
import java.io.*;

public class Parser {

   private Lexer lex;

   public Parser( Lexer lexer ) {
      lex = lexer;
   }

   public Node parseDefs() {
      System.out.println("parsing <defs>:");
      Node first = parseDef();

      Token token = lex.getNextToken(); //see if there are more def's
      if ( token.isKind("eof") ) { //we've reached the "end of file"
         return new Node( "defs", first, null, null );
      }
      else { //else there are more defs
         lex.putBackToken( token );
         Node second = parseDefs();
         return new Node( "defs", first, second, null );
      }
   } //parseDefs()

   public Node parseDef() {
      System.out.println("  parsing <def>:");

      Token token = lex.getNextToken();
      errorCheck( token, "LPAREN" );

      Token name = lex.getNextToken();
      errorCheck( name, "define" );

      token = lex.getNextToken();
      errorCheck( token, "LPAREN" );

      token = lex.getNextToken();
      errorCheck( token, "NAME" ); //NAME token

      token = lex.getNextToken();
      if ( token.matches("RPAREN", ")" )) {
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "RPAREN" );
         System.out.println("  //--- end of a define");

         return new Node( "def", first, null, null );
      }
      else { //else its a <def> with parameters
         lex.putBackToken( token );
         Node first = parseParams();

         token = lex.getNextToken();
         errorCheck( token, "RPAREN" );

         Node second = parseExpr();

         token = lex.getNextToken();
         errorCheck( token, "RPAREN" );
         System.out.println("  //--- end of a define");

         return new Node( "def", first, second, null );
      }
   } //parseDef()


   private Node parseParams() {
      System.out.println("  parsing <params>:");

      Token token = lex.getNextToken(); //get the NAME token
      errorCheck( token, "NAME" ); //check NAME

      token = lex.getNextToken();
      if( token.matches("RPAREN", ")") ) {
         lex.putBackToken( token );
         //return new Node( "NAME", null, null, null );
         return new Node( "NAME", token.getDetails(), null, null, null );
      }
      else {
         lex.putBackToken( token );
         Node first = parseParams();
         //return new Node( "NAME", first, null, null );
         return new Node( "NAME", token.getDetails(), first, null, null );
      }
   } //parseParams()

   private Node parseExpr() {
      System.out.println("    parsing <expr>");

      Token token = lex.getNextToken();

      if ( token.isKind("NUMBER") ) {
        return new Node("NUMBER", token.getDetails(), null, null, null );
      }
      else if ( token.isKind("NAME") ) { //check if NAME NAME
        errorCheck( token, "NAME" );
        return new Node( "NAME", token.getDetails(), null, null, null );
      }
      else { //else its a <list>
        lex.putBackToken( token ); //put back "(" token to parse as entire list
        Node first = parseList();
        return new Node( "list", first, null, null );
      }
   } //parseExpr()

   private Node parseList() {
      System.out.println("      parsing <list>:");

      Token token = lex.getNextToken();
      errorCheck( token, "LPAREN" );

      token = lex.getNextToken();
      if( token.matches("RPAREN", ")") ) {
        errorCheck( token, "RPAREN" );
        return new Node( token );
      }
      else { //else its a list with items
        lex.putBackToken( token );
        Node first = parseItems();

        token = lex.getNextToken();
        errorCheck( token, "RPAREN" );
        System.out.println("      //--- end of a list");

        return new Node( "items", first, null, null );
      }
   } //parseList()

   private Node parseItems() {
      System.out.println("        parsing <items>:");
      Node first = parseExpr();

      Token token = lex.getNextToken();
      if( token.matches("RPAREN", ")") ) { //if ")", its the end of the list
        lex.putBackToken( token ); //put back ")" token, handled in parseList()
        return new Node("items", first, null, null);
      }
      else {
        lex.putBackToken( token ); //put back token to parse <items>
        Node second = parseItems();
        return new Node("items", first, second, null);
      }
   } //parseItems()

  // check whether token is correct kind
  private void errorCheck( Token token, String kind ) {
    if( ! token.isKind( kind ) ) {
      System.out.println("Error:  expected " + token + " to be of kind " + kind );
      System.exit(1);
    }
  } //parseItems()

  // check whether token is correct kind and details
  private void errorCheck( Token token, String kind, String details ) {
    if( ! token.isKind( kind ) ||
        ! token.getDetails().equals( details ) ) {
      System.out.println("Error:  expected " + token + " to be kind= " + kind + " and details= " + details );
      System.exit(1);
    }
  } //errorCheck()

}
