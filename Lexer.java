import java.util.*;
import java.io.*;
public class Lexer {

   public static String margin = "";
   private Stack<Token> stack;
   private BufferedReader input;
   private int lookahead;

   public static void main(String[] args) throws Exception {
     System.out.print("Enter file name: ");
     Scanner keys = new Scanner( System.in );
     String name = keys.nextLine();
     Lexer lex = new Lexer( name );
     Token token;
     do {
       token = lex.getNext();
       System.out.println( token.toString() );
     }
     while( !token.getKind().equals( "eof" ) );
   }

   public Lexer( String fileName ) {
     try {
       input = new BufferedReader( new FileReader( fileName ) );
     }
     catch(Exception e) {
       error("Problem opening file named [" + fileName + "]" );
     }
     stack = new Stack<Token>();
     lookahead = 0;  //indicates no lookahead symbol present
   }

   private Token getNext() {
      if( !stack.empty() ) {
         Token token = stack.pop();
         return token;
      }
      else {
         int state = 1;  //state of finite automaton, initial state is 1
         String data = "";  //collective info for the current token
         boolean done = false;
         int sym;  //holds current symbol, values are in ASCII
         do {
            sym = getNextSymbol();
            //System.out.println("current symbol: " + sym + " state = " + state );
            if ( state == 1 ) {
               //sym == 9 is tab, sym == 10 is new line, sym == 13 is carriage return, sym == 32 is whitespace
               if ( sym == 9 || sym == 10 || sym == 13 || sym == 32 ) {
                  state = 1;
               }
               else if ( sym == '(' || sym == ')' ) { //"(" and ")" symbols are tokens
                  data += (char) sym; //add '(' or ')' symbol to current data
                  state = 2;
                  done = true; //this is an accepting state, so done = true, exit loop to create and return token
               }
               else if ( sym == '(' ) { // ASCII 40 is "(" 
                  data += (char) sym;

                  state = 3;
               }
               else if ( digit( sym ) ) { //symbol is any digit
                  data += (char) sym; //add digit symbol to current data
                  state = 4;
               }
               else if ( sym == ';' ) { //symbol for beginning of comment
                  state = 8;
               }
               else if ( sym == -1 ) { //symbol is EOF, which means end of file
                  state = 6;
                  done = true; //this is an accepting state, so done = true, exit loop to create and return EOF token
               }
               else if ( letter(sym) ) { //any letter (not just lowercase)
                  data += (char) sym;
                  state = 7;
               }
               else {
                 error("Error in lexical analysis phase with symbol " + sym + " in state " + state );
               }
            }

            else if ( state == 3 ) {
               if ( digit(sym) ) { //check if next symbol is a digit
                  data += (char) sym;
                  state = 4; //go to state 4 if symbol was a digit
               }
               else {
                 error("Error in lexical analysis phase with symbol " + sym + " in state " + state ); //error if we dont get a digit after "-"
               }
            }

            else if ( state == 4 ) {
               if ( digit(sym) ) { //check if next symbol is a digit
                  data += (char) sym; //add digit symbol/character to current data
                  state = 4; //stay in state 4 (technically dont need this line since its already in state 4)
               }
               else if ( sym == '.' ) { //check if next symbol is a decimal symbol
                  data += (char) sym; //add decimal symbol/character to current data
                  state = 5; //go to state 5, which should find more digit(s)
               }
               else { //there are no more digits or '.' decimal symbols
                  putBackSymbol( sym ); //put digit symbol back on stack
                  done = true;
               }
            }

            else if ( state == 5 ) {
               if ( digit(sym) ) { //check if next symbol is a digit
                  data += (char) sym; //add digit symbol/character to current data
                  state = 5; //stay in state 5 (technically dont need this line since its already in state 5)
               }
               else { //there are no more digit symbols
                  putBackSymbol( sym ); //put digit symbol back on stack
                  done = true;
               }
            }

            else if ( state == 8 ) {
               if ( sym == 10 || sym == 13 ) { //check if symbol is a new line (10) or carriage return (13)
                  state = 1; //if above line is true, we've reached the end of the comment line, set state to 1
                  data = ""; //these are comments so empty the data
               }
               else { //still part of the comment line
                  state = 8; //stay in state 8
               }
            }

            else if ( state == 7 ) {
               if ( letter(sym) ) { //check if next symbol is a letter
                  data += (char) sym; //add letter symbol/character to current data
                  state = 7; //stay in state 7 (technically dont need this line since its already in state 5)
               }
               else if ( digit(sym) ) { //check if next symbol is a digit
                  data += (char) sym; //add digit symbol/character to current data
                  state = 7; //stay in state 5 (technically dont need this line since its already in state 5)
               }
               else { //there are no more letters or digit symbols
                  putBackSymbol( sym ); //put letter or digit symbol back on stack
                  done = true;
               }
            }

         }while( !done );

         // generate token depending on stopping state
         Token token;

         if ( state == 2 ) {
            if ( sym == '(' ) {
              return new Token( "LPAREN", data );
            }
            else { //implies ")"
              return new Token( "RPAREN", data );
            }
         }
         else if ( state == 4 || state == 5 ) {
            return new Token( "NUMBER", data );
         }
         else if ( state == 6 ) {
            return new Token( "eof", data );
         }
         else if ( state == 7 ) {
            // now anything starting with letter is either
            // a NAME keyword or variable
            if ( data.equals("define") || data.equals("if") ||
                 data.equals("plus") || data.equals("minus") ||
                 data.equals("times") || data.equals("div") ||
                 data.equals("lt") || data.equals("le") ||
                 data.equals("eq") || data.equals("ne") ||
                 data.equals("and") || data.equals("or") ||
                 data.equals("not") || data.equals("ins") ||
                 data.equals("first") || data.equals("rest") ||
                 data.equals("null") || data.equals("num") ||
                 data.equals("list") || data.equals("read") ||
                 data.equals("write") || data.equals("nl") ||
                 data.equals("quote") || data.equals("quit")
               ) {
               return new Token( data, "" ); //NAME or "define" token
            }
            else {
               return new Token( "NAME", data ); // DEREK: changed this to NAME, it was "var" in Corgi
            }
         }
         else { //Lexer error
           error("somehow Lexer FA halted in bad state " + state );
           return null;
         }
      } //else generate token from input
   } //getNext

   public Token getNextToken() {
     Token token = getNext();
     System.out.println("                     got token: " + token );
     return token;
   }

   public void putBackToken( Token token ) {
     System.out.println( margin + "put back token " + token.toString() );
     stack.push( token );
   }

   // next physical symbol is the lookahead symbol if there is one,
   // otherwise is next symbol from file
   private int getNextSymbol() {
     int result = -1;
     if( lookahead == 0 ) {// is no lookahead, use input
       try{  result = input.read();  }
       catch(Exception e){}
     }
     else {// use the lookahead and consume it
       result = lookahead;
       lookahead = 0;
     }
     return result;
   }

   private void putBackSymbol( int sym ) {
     if( lookahead == 0 ) {// sensible to put one back
       lookahead = sym;
     }
     else {
       System.out.println("Oops, already have a lookahead " + lookahead + " when trying to put back symbol " + sym );
       System.exit(1);
     }
   }// putBackSymbol

   private boolean letter( int code ) {
      return 'a'<=code && code<='z' ||
             'A'<=code && code<='Z';
   }

   private boolean digit( int code ) {
     return '0'<=code && code<='9';
   }

   // private boolean printable( int code ) {
   //   return ' '<=code && code<='~';
   // }

   private static void error( String message ) {
     System.out.println( message );
     System.exit(1);
   }

}
