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

   public Node parseProgram() {
      System.out.println("-----> parsing <program>:");
      Node first = parseFuncCall();
      Token token = lex.getNextToken();
      if ( token.isKind("eof") ) {
         return new Node( "program", first, null, null );
      }
      else {// have a funcDef
         lex.putBackToken( token );
         Node second = parseFuncDefs();
        return new Node("program", first, second, null );
      }
   }

   public Node parseFuncDefs() {
      System.out.println("-----> parsing <funcDefs>:");

      Node first = parseFuncDef();

      // look ahead to see if there are more funcDef's
      Token token = lex.getNextToken();

      if ( token.isKind("eof") ) {
         return new Node( "funcDefs", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseFuncDefs();
         return new Node( "funcDefs", first, second, null );
      }
   }

   public Node parseFuncDef() {
      System.out.println("-----> parsing <funcDef>:");

      Token token = lex.getNextToken();
      errorCheck( token, "def" );

      Token name = lex.getNextToken();  // the function name
      errorCheck( name, "var" );

      token = lex.getNextToken();
      errorCheck( token, "single", "(" );

      token = lex.getNextToken();

      if ( token.matches("single", ")" )) {// no params

         token = lex.getNextToken();
         if ( token.isKind("end") ) {// no statements
            return new Node("funcDef", name.getDetails(), null, null, null );
         }
         else {// have a statement
            lex.putBackToken( token );
            Node second = parseStatements();
            token = lex.getNextToken();
            errorCheck( token, "end" );
            return new Node("funcDef", name.getDetails(), null, second, null );
         }
      }// no params
      else {// have params
         lex.putBackToken( token );
         Node first = parseParams();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         
         token = lex.getNextToken();

         if ( token.isKind( "end" ) ) {// no statements
            return new Node( "funcDef", name.getDetails(), first, null, null );
         }
         else {// have statements
            lex.putBackToken( token );
            Node second = parseStatements();
            token = lex.getNextToken();
            errorCheck( token, "end" );
            return new Node("funcDef", name.getDetails(), first, second, null );
         }

      }// have params

   }// parseFuncDef
   
   
   private Node parseParams() {
      System.out.println("-----> parsing <params>:");
   
      Token token = lex.getNextToken();
      errorCheck( token, "var" );
 
      Node first = new Node( "var", token.getDetails(), null, null, null );

      token = lex.getNextToken();
      
      if ( token.matches( "single", ")" ) ) {// no more params
         lex.putBackToken( token );  // funcCall handles the )
         return new Node( "params", first, null, null );
      }
      else if ( token.matches( "single", "," ) ) {// have more params
         Node second = parseParams();
         return new Node( "params", first, second, null );
      }
      else {// error
         System.out.println("expected , or ) and saw " + token );
         System.exit(1);
         return null;
      }

   }// <params>

   private Node parseStatements() {
      System.out.println("-----> parsing <statements>:");
 
      Node first = parseStatement();
 
      // look ahead to see if there are more statement's
      Token token = lex.getNextToken();
 
      if ( token.isKind("eof") ) {
         return new Node( "stmts", first, null, null );
      }
      else if ( token.isKind("end") || 
                token.isKind("else")
              ) {
         lex.putBackToken( token );
         return new Node( "stmts", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseStatements();
         return new Node( "stmts", first, second, null );
      }
   }// <statements>

   private Node parseFuncCall() {
      System.out.println("-----> parsing <funcCall>:");

      Token token = lex.getNextToken();
      errorCheck( token, "LPAREN" , "(");

      Token name = lex.getNextToken(); // function name
      errorCheck( name, "define" );


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

   private Node parseStatement() {
      System.out.println("-----> parsing <statement>:");
 
      Token token = lex.getNextToken();
 
      // --------------->>>  <str>
      if ( token.isKind("string") ) {
         return new Node( "str", token.getDetails(),
                          null, null, null );
      }
      // --------------->>>   <var> = <expr> or funcCall
      else if ( token.isKind("var") ) {
         String varName = token.getDetails();
         token = lex.getNextToken();
 
         if ( token.matches("single","=") ) {// assignment
            Node first = parseExpr();
            return new Node( "sto", varName, first, null, null );
         }
         else if ( token.matches("single","(")) {// funcCall
            lex.putBackToken( token );
            lex.putBackToken( new Token("var",varName) );
            Node first = parseFuncCall();
            return first;
         }

// once you hit a variable then you are now referencing an array
         // If you are referencing an array
         else if ( token.matches("single","[")) {// funcCall
            
            Node first = new Node( "arrayIndex" , null, null , null );

            Node third = parseExpr(); // will be the array index to reference 
            // Third will be a "double" will have to change it to an int in the node class
            
            token = lex.getNextToken(); // get the ]
            errorCheck( token, "single", "]" ); 

            token = lex.getNextToken(); // will be the "="
            errorCheck( token, "single", "=" ); 

            Node second = parseExpr(); // This will be a double.  
            // the value will be to put into the array

            // Create the new Node for storing a variable by refernecing an array
            return new Node( "sto" , varName, first, second, third );
         }         


         else {
            System.out.println("<var> must be followed by = or (, [ "
                  + " not " + token );
            System.exit(1);
            return null;
         }
      }
      // --------------->>>   if ...
      else if ( token.isKind("if") ) {
         Node first = parseExpr();

         token = lex.getNextToken();
         
         if ( token.isKind( "else" ) ) {// no statements for true case
            token = lex.getNextToken();
            if ( token.isKind( "end" ) ) {// no statements for false case
               return new Node( "if", first, null, null );
            }
            else {// have statements for false case
               lex.putBackToken( token );
               Node third = parseStatements();
               token = lex.getNextToken();
               errorCheck( token, "end" );
               return new Node( "if", first, null, third );               
            }
         }
         else {// have statements for true case
            lex.putBackToken( token );
            Node second = parseStatements();

            token = lex.getNextToken();
            errorCheck( token, "else" );

            token = lex.getNextToken();
            
            if ( token.isKind( "end" ) ) {// no statements for false case
               return new Node( "if", first, second, null );
            }
            else {// have statements for false case
               lex.putBackToken( token );
               Node third = parseStatements();
               token = lex.getNextToken();
               errorCheck( token, "end" );
               return new Node( "if", first, second, third );
            }
         }

      }// if ... 
/******************************************************************************************* 
 * DEREK: This is where the for loop will be read
**********************************************************************************************/
      // --------------->>>   for ...
      else if ( token.isKind("for") ) {

         // This is before it hit the var
         token = lex.getNextToken();
         errorCheck( token, "var" );
         String varName = token.getDetails();

         token = lex.getNextToken();  // this is the "=" sign
         errorCheck( token, "single" , "=");

         Node first = parseExpr(); // store the iterator number as a node

         token = lex.getNextToken(); // this is the "to" of the loop
         errorCheck( token, "var" ,"to");

         Node second = parseExpr(); // store the max as a node
         Node third = parseStatements(); // run the print statement and save as a node

         token = lex.getNextToken();
         errorCheck(token, "end", "");

         return new Node( "for", varName, first, second, third); // return the for loop node

      }// for ...
/*******************************************************************************************
 * DEREK: This is the end of the for loop to create a usable node
 *******************************************************************************************/

      else if ( token.isKind( "return" ) ) {
         Node first = parseExpr();
         return new Node( "return", first, null, null );
      }// return

      else {
         System.out.println("Token " + token + 
                             " can't begin a statement");
         System.exit(1);
         return null;
      }
 
   }// <statement>

   private Node parseExpr() {
      System.out.println("-----> parsing <expr>");

      Node first = parseTerm();

      // look ahead to see if there's an addop
      Token token = lex.getNextToken();
 
      if ( token.matches("single", "+") ||
           token.matches("single", "-") 
         ) {
         Node second = parseExpr();
         return new Node( token.getDetails(), first, second, null );
      }
      else {// is just one term
         lex.putBackToken( token );
         return first;
      }

   }// <expr>

   private Node parseTerm() {
      System.out.println("-----> parsing <term>");

      Node first = parseFactor();

      // look ahead to see if there's a multop
      Token token = lex.getNextToken();
 
      if ( token.matches("single", "*") ||
           token.matches("single", "/") 
         ) {
         Node second = parseTerm();
         return new Node( token.getDetails(), first, second, null );
      }
      else {// is just one factor
         lex.putBackToken( token );
         return first;
      }
      
   }// <term>

   private Node parseFactor() {
      System.out.println("-----> parsing <factor>");

      Token token = lex.getNextToken();

      if ( token.isKind("num") ) {
         return new Node("num", token.getDetails(), null, null, null );
      }
      else if ( token.isKind("NAME") ) {
         // must be a vairable
         String name = token.getDetails();

         token = lex.getNextToken();

         if ( token.matches( "single", "(" ) ) {// is a funcCall
            lex.putBackToken( new Token( "single", "(") );  // put back the (
            lex.putBackToken( new Token( "var", name ) );  // put back name 
            Node first = parseFuncCall();
            return first;
         }

//************************************************************************************************************************************** */
         // If the parse Expression is trying to get a value from an array
         else if ( token.matches( "single", "[" ) ) {// is a array index

            Node first = parseExpr();

            token = lex.getNextToken(); // this is the ")"
            errorCheck( token, "single" ,"]");

            //put the name of the array in there it should be "a"
            return new Node( "arrayGetter", name , first , null, null); // return the for loop node
         }

//*************************************************************************************************************************************** */


         else {// is just a <var>
            lex.putBackToken( token );  // put back the non-( token
            return new Node("var", name, null, null, null );
         }
      }

/*******************************************************************************************
 * DEREK: This is the beginning if the next token is an  [array,]
 *******************************************************************************************/

      else if ( token.isKind("array") ) {

         // This is before it hit the var
         token = lex.getNextToken();
         errorCheck( token, "single" , "(" );

         Node first = parseExpr(); // store the number of how large the array will be

         token = lex.getNextToken(); // this is the ")"
         errorCheck( token, "single" ,")");

         return new Node( "array", first, null, null); // return the for loop node

      } // array 


      else if ( token.matches("LPAREN","(") ) {
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         return first;
      }
/*******************************************************************************************
 * DEREK: This is assignming a value to the array token [single, ] ] or [single, [ ]
 *******************************************************************************************/
      else if ( token.matches("single","[") ) {
         Node first = parseExpr(); // get the content you want to put in the array
         token = lex.getNextToken();  // the next token should end with "]"
         errorCheck( token, "single", "]" ); // error check to make sure this is "]"
         return first;
      }
/*******************************************************************************************
 * DEREK: This is the end of parse expression when you want to set the value inside an array
 *******************************************************************************************/
      else if ( token.matches("single","-") ) {
         Node first = parseFactor();
         return new Node("opp", first, null, null );
      }
      else {
         System.out.println("Can't have a factor starting with " + token );
         System.exit(1);
         return null;
      }
      
   }// <factor>

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
