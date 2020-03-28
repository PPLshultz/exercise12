import java.util.Scanner;

public class SL3 {

   public static void main(String[] args) throws Exception {

      String name;

      if ( args.length == 1 ) {
         name = args[0];
      }
      else {
         System.out.print("Enter name of SL3 program file: ");
         Scanner keys = new Scanner( System.in );
         name = keys.nextLine();
      }

      Lexer lex = new Lexer( name );
      Parser parser = new Parser( lex );

      // start with <defs>
      Node root = parser.parseDefs();

      //display parse tree for debugging/testing
      TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 1200, 800, root);

      root.execute(); //execute the parse tree

   } //main

}
