import java.util.Scanner;
import java.io.*;

public class SL3 {

   public static void main(String[] args) throws Exception {

      TreeViewer viewer;

      Scanner keys = new Scanner( System.in );
      String sourceName;

      if ( args.length == 1 ) {
         sourceName = args[0];
      }
      else {
         System.out.print("Enter name of SL3 program file: ");
         sourceName = keys.nextLine();
      }

      Lexer lex = new Lexer( sourceName );
      Parser parser = new Parser( lex );

      Node defsRoot = parser.parseDefs();

      // display parse tree for debugging/testing:
/*
      viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, 
                                              defsRoot );
      while ( 2 < Math.sqrt(9) ){}
*/

      // REPL





//I got rid of this.  I figure I can put defRoot into the third slot of the Node


   //   Node.init( defsRoot );    // inform Node of the defs tree

      if ( false ) {// view single expression tree
         System.out.print("?  ");
         String expression = keys.nextLine();
         PrintWriter out = new PrintWriter( new File( "etemp" ) );
         out.println( expression );
         out.close();

         lex = new Lexer( "etemp" );
         parser = new Parser( lex );
         Node exprRoot = parser.parseExpr();

         if ( true ) {// see the tree
      //     viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, 
      //                                         exprRoot );
         }
         else {// evaluate it
      //      Value value = exprRoot.evaluate();  // DEREK: change the evaluate method to be a linked list
      //      System.out.println( value );
         }

       }
       else {// do the full REPL with no tree viewing

         // if the outputValue = 999999 then REPL will halt
         double outputValue = 0;
         while( 999999 != outputValue ) {

            System.out.print("\n?  ");
            String expression = keys.nextLine();
            PrintWriter out = new PrintWriter( new File( "etemp" ) );
            out.println( expression );
            out.close();

            if (expression.equals("(nl)")){ //New line command
               System.out.println("\n\n\n");
            }
            else if (expression.equals("(quit)")){ //New line command
               System.out.println("Quitting...\n\n\n");
               System.exit(1);
            }
            else{
               lex = new Lexer( "etemp" );
               parser = new Parser( lex );
               Node exprRoot = parser.parseExpr();
               
               //This is good for see what will happen when only in the REPL node (third)
               Value value = exprRoot.evaluate();
               //This is only relative to the users input
               String replOutput = value.toString();
               System.out.println(replOutput);


               //This is the combo node I created putting together the def node and REPL node,
               // Def node is 1 and 2 and REPL is node 3
               Node comboRoot = defsRoot.insertNode( defsRoot , exprRoot);
               /////////////////////
               //This is what I need to get to work

     //          Value finalOutputList = comboRoot.evaluate();
      //         String finalOutput = finalOutputList.toString();
      //         System.out.println(finalOutput);
               ///////////////////////

            } // New line command

         }

      }// full REPL

   }// main

}
