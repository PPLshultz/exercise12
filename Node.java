/*  a Node holds one node of a parse tree
    with several pointers to children used
    depending on the kind of node
*/

import java.util.*;
import java.io.*;
import java.awt.*;

public class Node {

  public static int count = 0;  // maintain unique id for each node

  private int id;

  private String kind;  // non-terminal or terminal category for the node
  private String info;  // extra information about the node such as
                        // the actual identifier for an I

  // references to children in the parse tree
  private Node first, second, third;

  // stack of memories for all pending calls
  private static ArrayList<MemTable> memStack = new ArrayList<MemTable>();
  // convenience reference to top MemTable on stack
  private static MemTable table = new MemTable();

  // status flag that causes <stmts> nodes to abort asking second
  // to execute
  private static boolean returning = false;

  // value being returned
  private static double returnValue = 0;

  private static Node root;  // root of the entire parse tree

  private static Scanner keys = new Scanner( System.in );

  // construct a common node with no info specified
  public Node( String k, Node one, Node two, Node three ) {
    kind = k;  info = "";
    first = one;  second = two;  third = three;
    id = count;
    count++;
    System.out.println( this );
  }

  // construct a node with specified info
  public Node( String k, String inf, Node one, Node two, Node three ) {
    kind = k;  info = inf;
    first = one;  second = two;  third = three;
    id = count;
    count++;
    System.out.println( this );
  }

  // construct a node that is essentially a token
  public Node( Token token ) {
    kind = token.getKind();  info = token.getDetails();
    first = null;  second = null;  third = null;
    id = count;
    count++;
    System.out.println( this );
  }

public Node insertNode( Node defNode , Node replNode){ 

   Node first = defNode.first;
   Node third = replNode.first;

   if (defNode.second != null){
      Node second = defNode.second;
      return new Node( "root" , first , second, third );
   }
   
   else {
      return new Node( "root" , first , null, third );
   }
}






  public String toString() {
    return "                                                  = Node #" + id + "[" + kind + "," + info + "]<" + nice(first) + " " + nice(second) + ">";
  }

  public String nice( Node node ) {
     if ( node == null ) {
        return "-";
     }
     else {
        return "" + node.id;
     }
  }

  // produce array with the non-null children
  // in order
  private Node[] getChildren() {
    int count = 0;
    if( first != null ) count++;
    if( second != null ) count++;
    if( third != null ) count++;
    Node[] children = new Node[count];
    int k=0;
    if( first != null ) {  children[k] = first; k++; }
    if( second != null ) {  children[k] = second; k++; }
    if( third != null ) {  children[k] = third; k++; }
    return children;
  }

  //******************************************************
  // graphical display of this node and its subtree
  // in given camera, with specified location (x,y) of this
  // node, and specified distances horizontally and vertically
  // to children
  public void draw( Camera cam, double x, double y, double h, double v ) {

//System.out.println("draw node " + id );

    // set drawing color
    cam.setColor( Color.black );

    String text = kind;
    if( ! info.equals("") ) text += "(" + info + ")";
    cam.drawHorizCenteredText( text, x, y );

    // positioning of children depends on how many
    // in a nice, uniform manner
    Node[] children = getChildren();
    int number = children.length;
//System.out.println("has " + number + " children");

    double top = y - 0.75*v;

    if( number == 0 ) {
      return;
    }
    else if( number == 1 ) {
      children[0].draw( cam, x, y-v, h/2, v );     cam.drawLine( x, y, x, top );
    }
    else if( number == 2 ) {
      children[0].draw( cam, x-h/2, y-v, h/2, v );     cam.drawLine( x, y, x-h/2, top );
      children[1].draw( cam, x+h/2, y-v, h/2, v );     cam.drawLine( x, y, x+h/2, top );
    }
    else if( number == 3 ) {
      children[0].draw( cam, x-h, y-v, h/2, v );     cam.drawLine( x, y, x-h, top );
      children[1].draw( cam, x, y-v, h/2, v );     cam.drawLine( x, y, x, top );
      children[2].draw( cam, x+h, y-v, h/2, v );     cam.drawLine( x, y, x+h, top );
    }
    else {
      //System.out.println("no Node kind has more than 3 children???");
      System.exit(1);
    }

  }// draw

  public static void error( String message ) {
    System.out.println( message );
    System.exit(1);
  }



   //Added this from the parser to return a Node

  
  
  
  
  
  
  
  
  
  
  
  // ===============================================================
   //   execute/evaluate nodes
   // ===============================================================

  // ask this node to execute itself
  // (for nodes that don't return a value)
   public void execute() {

      System.out.println("Executing node " + id + " of kind " + kind );

      if (kind.equals("root")){
         first.execute();
         if(second != null){
            second.execute();  
         }
         third.execute();
      }

      //if ( kind.equals("program") ) {
      if ( kind.equals("defs") ) {
         root = this;  // note the root node of entire tree
         first.execute();
      }// program

      else if( kind.equals("def") ) {
        first.execute();
        if ( second != null ) {
          second.execute();
        }
      }

      else if( kind.equals("NAME") ) {
        System.out.println("NAME info = " + info);
      }

      else if( kind.equals("list") ) {
        first.execute();
      }

      else if( kind.equals("items") ) {
        first.execute();
        if ( second != null ) {
          second.execute();
        }
      }

      else if ( kind.equals("stmts") ) {
        first.execute();
        // returning is a flag saying that first
        // wants to return, so don't do this node's second
        if ( second != null && !returning ) {
          second.execute();
        }
      }// stmts

      else if ( kind.equals("funcCall") ) {
         // execute a function call as a statement

         String funcName = info;

         // handle bifs
         if ( funcName.equals("print") ) {
            // evaluate the single <expr>
            double value = first.first.evaluate();
            if ( (int) value == value )
               System.out.print( (int) value );
            else
               System.out.print( value );
         }
         else if ( funcName.equals("nl") ) {
            System.out.println();
         }

         else {// user-defined function

            Node body = passArgs( this, funcName );
            body.second.execute();

            returning = false;

         }// user-defined function

      }// funcCall

      else if ( kind.equals("str") ) {
         System.out.print( info );
      }// str

      else if ( kind.equals("sto") ) {
         double value = first.evaluate();
         table.store( info, value );
      }// sto

      else if ( kind.equals("if") ) {
         double question = first.evaluate();
         if ( question != 0 ) {
            second.execute();
         }
         else {
            third.execute();
         }
      }// if

      else if ( kind.equals("return") ) {
         returnValue = first.evaluate();
         System.out.println("return value is set to " + returnValue );

         returning = true;

         // manage memtables
            // pop the top mem table
            memStack.remove( memStack.size()-1 );

            // convenience note new top (if any)
            if ( memStack.size() > 0 )
               table = memStack.get( memStack.size()-1 );
            else {// notice program is over
               System.out.println(".......execution halting");
               System.exit(0);
            }

      }// return

      else {
         error("Executing unknown kind of node [" + kind + "]");
      }

   }// execute

   // compute and return value produced by this node
   public double evaluate() {

      System.out.println("Evaluating node " + id + " of kind " + kind );

//#####################################################################################



//##################################################################################

      if ( kind.equals("list") ) {
         
         if( first.kind.equals("list") ) {
            double value1 = first.first.evaluate();
            if( first.info.equals("listCheck")){
               if (first.first.kind.equals("list")){
                  return 1; // The next kind is a list
               }
               return 0; // Not a list from the list check command  
            }

            if (first.second != null){
            double value2 = first.second.evaluate();
            return value1 + value2;
            }
            else{
               return value1;
            }



         }        

         if( first.kind.equals("plus") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            return value1 + value2;
         }
         else if( first.kind.equals("minus") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            return value1 - value2;
          }
          else if( first.kind.equals("times") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            return value1 * value2;
          }
          else if( first.kind.equals("div") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            return value1 / value2;
          }
          else if( first.kind.equals("lt") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            double result = 0;
            if( value1 < value2 ) {
              result = 1;
            }
            return result;
          }
          else if( first.kind.equals("le") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            double result = 0;
            if( value1 <= value2 ) {
              result = 1;
            }
            return result;
          }
          else if( first.kind.equals("eq") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            double result = 0;
            if( value1 == value2 ) {
              result = 1;
            }
            return result;
          }
          else if( first.kind.equals("ne") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            double result = 0;
            if( value1 != value2 ) {
              result = 1;
            }
            return result;
          }
          else if( first.kind.equals("and") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            double result = 0;
            if( (value1 != 0) && (value2 != 0) ) {
              result = 1;
            }
            return result;
          }
          else if( first.kind.equals("or") ) {
            double value1 = first.first.evaluate();
            double value2 = first.second.evaluate();
            double result = 0;
            if( (value1 != 0) || (value2 != 0) ) {
              result = 1;
            }
            return result;
          }
          else if( first.kind.equals("not") ) {
            double value1 = first.first.evaluate();
            double result = 0;
            if( value1 == 0 ) {
              result = 1;
            }
            return result;
          }
          else if( first.kind.equals("null") ) {

               double value = first.first.evaluate();
               
               if(first.first.kind.equals("list") && value == 1 ){
                  return value;
               }
               else{ 
                  return 0;
               }

          }

          else if (first.kind.equals("RPAREN")){
            double result = 1;
            return result;
         }

         else if( first.kind.equals("num") ) {
            
            double value = first.first.evaluate();

            if (first.first.kind.equals("NUMBER")){
               return 1;
            }
            else{
               return 0;
            }
         }
         else if( first.kind.equals("list") ) {

            if (first.first.kind.equals("list")){
               return 1;
            }
            else{
               return 0;
            }

         }
         
         else if( first.kind.equals("items") ) { 

            if( first.first.first.info.equals("write")){
               double value1 = first.first.second.first.evaluate();
               return value1;
            }
            //Not sure what the QUOTE does Derek
            // if( first.first.first.info.equals("quote")){
            //    double value1 = first.first.second.first.evaluate();
            //    return value1;
            // }

            double value1 = first.first.evaluate();
            
            return value1;
            }

            else if( first.kind.equals("if") ) { 
               
               double expr1 = first.first.evaluate();

               if (expr1 != 0){
                  double expr2 = first.second.evaluate();
                  return expr2;
               }
               
               double expr3 = first.third.evaluate();
               return expr3;

               }            





         double deleteMe = 100000;
         return deleteMe;
      } //list


      else if( kind.equals("items") ) { 

         double value1 = first.evaluate();
         
         return value1;
         }
//#########################################################

      else if( kind.equals("NAME") ) {

         if (info.equals("read")){ //REPL command to read the next integerfrom the user
         String userInputNumber = keys.nextLine();
         double value1 = Double.parseDouble(userInputNumber);

         return value1;
         }

         else if(info.equals("quit")){ //REPL command to quit
            double value1 = 999999;
   
            return value1;
            }

         else if(info.equals("nl")){ //REPL command to quit
            double value1 = 999998;
   
            return value1;
            }
//##############################################################################










         double deleteMe = 100000;
         return deleteMe;
      }




      else if ( kind.equals("NUMBER") ) {
         return Double.parseDouble( info );
      }










/***************************************************************************
START: predefined functions that take numeric inputs and produce a numeric result
***************************************************************************/
      else if( kind.equals("if") ) {
         double value1 = first.evaluate();
         double result;
         double value3;
         if( value1 != 0 ) {
            result = second.evaluate();
         }
         else {
            result = third.evaluate();
         }
         return result;
      }
      else if( kind.equals("plus") ) {
         double value1 = first.evaluate();
         double value2 = second.evaluate();
         return value1 + value2;
      }

/***************************************************************************
END: predefined functions that take numeric inputs and produce a numeric result
***************************************************************************/



      else if ( kind.equals("+") || kind.equals("-") ) {
         double value1 = first.evaluate();
         double value2 = second.evaluate();
         if ( kind.equals("+") )
            return value1 + value2;
         else
            return value1 - value2;
      }

      else if ( kind.equals("*") || kind.equals("/") ) {
         double value1 = first.evaluate();
         double value2 = second.evaluate();
         if ( kind.equals("*") )
            return value1 * value2;
         else
            return value1 / value2;
       }

       else if ( kind.equals("opp") ) {
          double value = first.evaluate();
          return -value;
       }

       else if ( kind.equals("funcCall") ) {
          // execute a function call to produce a value

         String funcName = info;

         double value;  // have all function calls put their value here
                        // to return once at the bottom

         // handle bifs

         if ( member( funcName, bif0 ) ) {
            if ( funcName.equals("input") )
               value =  keys.nextDouble();
            else {
               error("unknown bif0 name [" + funcName + "]");
               value = -1;
            }
         }
         else if ( member( funcName, bif1 ) ) {
            double arg1 = first.first.evaluate();

            if ( funcName.equals("sqrt") )
               value = Math.sqrt( arg1 );
            else if ( funcName.equals("cos") )
               value = Math.cos( Math.toRadians( arg1 ) );
            else if ( funcName.equals("sin") )
               value = Math.sin( Math.toRadians( arg1 ) );
            else if ( funcName.equals("atan") )
               value = Math.toDegrees( Math.atan( arg1 ) );
            else if ( funcName.equals("round") )
               value = Math.round( arg1 );
            else if ( funcName.equals("trunc") )
               value = (int) arg1;
            else if ( funcName.equals("not") )
               value = arg1 == 0 ? 1 : 0;
            else {
               error("unknown bif1 name [" + funcName + "]");
               value = -1;
            }
         }
         else if ( member( funcName, bif2 ) ) {
            double arg1 = first.first.evaluate();
            double arg2 = first.second.first.evaluate();

            if ( funcName.equals("lt") )
               value = arg1 < arg2 ? 1 : 0;
            else if ( funcName.equals("le") )
               value = arg1 <= arg2 ? 1 : 0;
            else if ( funcName.equals("eq") )
               value = arg1 == arg2 ? 1 : 0;
            else if ( funcName.equals("ne") )
               value = arg1 != arg2 ? 1 : 0;
            else if ( funcName.equals("pow") )
               value = Math.pow( arg1 , arg2 );
            else if ( funcName.equals("and") )
               value = arg1!=0 && arg2!=0 ? 1 : 0;
            else if ( funcName.equals("or") )
               value = arg1!=0 || arg2!=0 ? 1 : 0;
            else {
               error("unknown bif2 name [" + funcName + "]");
               value = -1;
            }
         }

         else {// user-defined function

            Node body = passArgs( this, funcName );
            body.second.execute();

            value = returnValue;

            returning = false;

         }// user-defined function call

         // uniformly finish
         return value;

       }// funcCall

       else {
          error("Evaluating unknown kind of node [" + kind + "]" );
          return -1;
       }

   }// evaluate

   private final static String[] bif0 = { "input", "nl" };
   private final static String[] bif1 = { "sqrt", "cos", "sin", "atan", "round", "trunc", "not" };
   private final static String[] bif2 = { "lt", "le", "eq", "ne", "pow", "or", "and" };

   // return whether target is a member of array
   private static boolean member( String target, String[] array ) {
      for (int k=0; k<array.length; k++) {
         if ( target.equals(array[k]) ) {
            return true;
         }
      }
      return false;
   }

   // given a funcCall node, and for convenience its name,
   // locate the function in the function defs and
   // create new memory table with arguments values assigned
   // to parameters
   // Also, return root node of body of the function being called
   private static Node passArgs( Node funcCallNode, String funcName ) {

      // locate the function in the function definitions

      Node node = root;  // the program node
      node = node.second;  // now is the funcDefs node
      Node fdnode = null;
      while ( node != null && fdnode == null ) {
         if ( node.first.info.equals(funcName) ) {// found it
            fdnode = node.first;
            System.out.println("located " + funcName + " at node " + fdnode.id );
         }
         else
           node = node.second;
      }

      MemTable newTable = new MemTable();

      if ( fdnode == null ) {// function not found
         error( "Function definition for [" + funcName + "] not found" );
         return null;
      }
      else {// function name found
         Node pnode = fdnode.first; // current params node
         Node anode = funcCallNode.first;  // current args node
         while ( pnode != null && anode != null ) {
            // store argument value under parameter name
            newTable.store( pnode.first.info,
                            anode.first.evaluate() );
            // move ahead
            pnode = pnode.second;
            anode = anode.second;
         }

         // detect errors
         if ( pnode != null ) {
            error("there are more parameters than arguments");
         }
         else if ( anode != null ) {
            error("there are more arguments than parameters");
         }

         System.out.println("at start of call to " + funcName + " memory table is:\n" + newTable );

         // manage the memtable stack
         memStack.add( newTable );
         table = newTable;

         return fdnode;

      }// function name found

   }// passArguments

}// Node
