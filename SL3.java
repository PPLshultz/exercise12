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
//               Value value = exprRoot.evaluate();
               //This is only relative to the users input
//               String replOutput = value.toString();
//               System.out.println(replOutput);

               //This is the combo node I created putting together the def node and REPL node,
               // Def node is 1 and 2 and REPL is node 3
               //Node defsRoot = parser.parseDefs();

               //Node comboRoot = defsRoot.insertNode( defsRoot , exprRoot);
               Node comboRoot = defsRoot.insertNode( exprRoot );
               comboRoot.init(comboRoot);
               /////////////////////
               //This is what I need to get to work

               Value finalOutputList = comboRoot.evaluate();
               String finalOutput = finalOutputList.toString();
               System.out.println(finalOutput);
               ///////////////////////

             // New line command

         }

      }// full REPL

   }// main

}
