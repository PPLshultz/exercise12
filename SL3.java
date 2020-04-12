import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

public class SL3 {

  public static Value result = new Value();
  public static Node root = null;

  public static void main(String[] args) throws Exception {

    Scanner keys = new Scanner( System.in );

    String filename; //name of functions file
    if ( args.length == 1 ) {
      filename = args[0];
    }
    else {
      System.out.print("Enter name of SL3 program file: ");
      filename = keys.nextLine();
    }

    Lexer lex = new Lexer( filename );
    Parser parser = new Parser( lex );

    // start with <defs>
    root = parser.parseDefs(); //parse function define file

    //display parse tree for debugging/testing
    //TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 1300, 900, root);
    //TreeViewer viewer;

    //root.execute(); //execute the parse tree for the user definitions file

    if( false ) { //if true, view expression tree
      System.out.print("REPL: ");
      String userInput = keys.nextLine();
      PrintWriter output = new PrintWriter( new File("temp") );
      output.println(userInput); //print entry to temp file
      output.close();

      lex = new Lexer("temp");
      parser = new Parser(lex);
      Node expressionRoot = parser.parseExpr(); //parse user input

      // if( true ) { //if true, view the tree
      //   viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, expressionRoot);
      // }
      // else { //else evaluate user input
      //   Value value = expressionRoot.evaluate();
      //   Value value = new Value(expressionRoot.evaluate());
      //   System.out.println(value); //print to command line
      // }
    }
    else  { //else just do REPL
      while(true) {
        System.out.print("REPL: ");
        String userInput = keys.nextLine();

        PrintWriter output = new PrintWriter( new File("temp") );
        output.println(userInput); //print entry to temp file
        output.close();

        lex = new Lexer("temp");
        Parser parser2 = new Parser(lex); //create parse tree for user input

        if(
          userInput.equalsIgnoreCase("(quit)") ||
          userInput.equalsIgnoreCase("quit") ||
          userInput.equalsIgnoreCase("exit")
        ) {
          System.exit(0); //exit program
        }
        else { //evaluate code entered by user
          Node expressionRoot = parser2.parseExpr(); //parse user input
          result = expressionRoot.evaluate();

          //TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 1300, 900, root);
          //parser.parseDefs(); //DEBUG: parse function define file again
          //root.execute(); //execute the parse tree for the user definitions file
          //Value resultTemp = expressionRoot.evaluate();
          // double temp = resultTemp.number;
          // if(temp != -999) {
          //   result = new Value(resultTemp);
          // }

          //expressionRoot.execute();
          System.out.println("evaluation: " + result);
        }
      }
    }
  } //main

} //SL3
