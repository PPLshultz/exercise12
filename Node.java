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
  private Value value = new Value(); //additional "info", use to edit NAME values

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

  public String toString() {
    return "#" + id + "[" + kind + "," + info + "]<" + nice(first) +
      " " + nice(second) + ">";
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


  // ===============================================================
  //   `execute
  // ===============================================================
  // ask this node to execute itself
  // (for nodes that don't return a value)
  public void execute() {

    System.out.println("Executing node " + id + " of kind " + kind );

    if( kind.equals("defs") ) {
      System.out.println("doing defs");
    }
    else if( kind.equals("def") ) {
      System.out.println("doing def");
    }
    else if( kind.equals("param") ) {
      System.out.println("doing param");
    }
    else if( kind.equals("NAME") ) {
      System.out.println("doing NAME");
    }
    else if( kind.equals("NUMBER") ) {
      System.out.println("doing NUMBER");
    }
    else if( kind.equals("list") ) {
      System.out.println("doing list");
    }
    else if( kind.equals("items") ) {
      System.out.println("doing items");
    }
    else if( kind.equals("userfunction") ) {
      System.out.println("doing userfunction");
    }
    else if( kind.equals("userfunction") ) {
      System.out.println("doing userfunction");
    }

    else if ( kind.equals("program") ) {
      root = this;  // note the root node of entire tree
      first.execute();  // execute the "main" funcCall
    }// program

    else if ( kind.equals("stmts") ) {
      first.execute();
      // returning is a flag saying that first
      // wants to return, so don't do this node's second
      if ( second != null && !returning ) {
        second.execute();
      }
    }// stmts

    //else if ( kind.equals("funcCall") ) {
    else if ( kind.equals("userfunction") ) {
      String funcName = info;
      // user-defined function
      //Node body = passArgs( this, funcName );
      //body.second.execute();
      //returning = false;
    }// funcCall

    else if ( kind.equals("str") ) {
      System.out.print( info );
    }// str

    // else if ( kind.equals("sto") ) {
    //   double value = first.evaluate();
    //   table.store( info, value );
    // }// sto

    else if ( kind.equals("if") ) {
      Value question = first.evaluate();
      if ( !question.is0() ) {
        second.execute();
      }
      else {
        third.execute();
      }
    }// if

    // else if ( kind.equals("return") ) {
    //   returnValue = first.evaluate();
    //   System.out.println("return value is set to " + returnValue );

    //   returning = true;

    //   // manage memtables
    //   // pop the top mem table
    //   memStack.remove( memStack.size()-1 );

    //   // convenience note new top (if any)
    //   if ( memStack.size() > 0 )
    //     table = memStack.get( memStack.size()-1 );
    //   else {// notice program is over
    //     System.out.println(".......execution halting");
    //     System.exit(0);
    //   }

    // }// return

    else {
      error("Executing unknown kind of node [" + kind + "]");
    }

  }// execute

  // ===============================================================
  //   `evaluate nodes
  // ===============================================================
  // compute and return value produced by this node
  //public double evaluate() {
  public Value evaluate() {

    System.out.println("Evaluating node " + id + " of kind " + kind );

    if( kind.equals("NUMBER") ) {
      //return new Value(Double.parseDouble(info));
      return new Value(info);
    }

    else if( kind.equals("NAME") ) {
      //return new Value(info);
      return new Value(value);
    }

    else if( kind.equals("list") ) {
      return first.evaluate();
    }

    else if( kind.equals("if") ) {
      System.out.println("-----------------------------------");
      System.out.println("if function");
      System.out.println("-----------------------------------");
      System.out.println("first = " + first.info);
      System.out.println("second = " + second.info);
      System.out.println("third = " + third.info);
      Value valueReturning;
      Value value1 = first.evaluate();
      System.out.println("if -> value1 = " + value1);
      if( value1.number != 0 ) {
        valueReturning = second.evaluate();
        System.out.println("if -> value2 = " + valueReturning);
      }
      else {
        valueReturning = third.evaluate();
        System.out.println("if -> value3 = " + valueReturning);
      }
      return valueReturning;
    }

    else if( kind.equals("plus") ) {
      Value value1 = first.evaluate();
      Value value2 = second.evaluate();
      System.out.println("first expr = " + value1);
      System.out.println("second expr = " + value2);
      double sum = value1.number + value2.number;
      return new Value(sum);
    }

    else if( kind.equals("minus") ) {
      Value value1 = first.evaluate();
      Value value2 = second.evaluate();
      double sum = value1.number - value2.number;
      return new Value(sum);
    }

    else if( kind.equals("div") ) {
      Value value1 = first.evaluate();
      Value value2 = second.evaluate();
      double sum = value1.number / value2.number;
      return new Value(sum);
    }

    else if( kind.equals("eq") ) {
      Value value1 = first.evaluate();
      Value value2 = second.evaluate();
      Value value;
      if( value1.number == value2.number ) {
        value = new Value(1);
      }
      else {
        value = new Value(0);
      }
      return value;
    }

    else if( kind.equals("or") ) {
      Value value1 = first.evaluate();
      Value value2 = second.evaluate();
      Value value;
      if( !value1.is0() || !value2.is0() ) {
        value = new Value(1);
      }
      else {
        value = new Value(0);
      }
      return value;
    }

    else if( kind.equals("ins") ) {
      Value value1 = first.evaluate();
      System.out.println("ins -> value1 = " + value1);
      Value value2 = second.evaluate();
      System.out.println("ins -> value2 = " + value2);
      value2.insert(value1);
      System.out.println("ins -> value2 = " + value2);
      return value2;
    }

    else if( kind.equals("first") ) {
      Value value = first.evaluate();
      return value.first();
    }

    else if( kind.equals("rest") ) {
      Value value = first.evaluate();
      System.out.println("rest -> value = " + value);
      return value.rest();
    }

    else if( kind.equals("null") ) {
      Value value = first.evaluate();
      System.out.println("null -> value (pre) = " + value);
      //String temp = value.toString();
      // if( value == 1 ) {
      //   value = new Value(1);
      // }
      if( value.number != 1 ) {
        value = new Value(0);
      }
      System.out.println("null -> value (post) = " + value);
      return value;
    }

    else if( kind.equals("quote") ) {
      Value value = new Value(); //make new Value of type list
      if( info.isEmpty() ) { //if quote is an empty list
        System.out.println("quote is empty!!!");
        return new Value(1);
      }
      else {
        String[] listOfNumbers = info.split("\\s");
        for(int i=listOfNumbers.length - 1; i>=0; i-- ) {
          value.insert(new Value(listOfNumbers[i]));
        }
        System.out.println("Value as string = " + value.toString());
        // Value value = new Value(info, "create a list"); //create a list of numbers
        // System.out.println("value.listContents = " + value.listContents);
        return value;
      }
    }

    else if ( kind.equals("userfunction") ) { //do user defined func like (foo 2)
      //this else if block updates the variable values within def parse tree
      String functionName = info; //save function name
      int totalArgs = 1; //default 1, can be up to 2
      Value expr1 = first.evaluate(); //evaluate and save expression1
      Value expr2 = null;
      if( second != null ) { //evaluate and expression2 if available
        expr2 = second.evaluate();
      }
      Node node = SL3.root;
      Node fdnode = null; //fdnode means function definition node
      boolean isStillSearchingTree = true;
      String paramName1;
      String paramName2 = null;
      while ( node != null && isStillSearchingTree ) { //find user function
        if ( node.first.info.equals(functionName) ) { // found it
          fdnode = node.first;
          paramName1 = fdnode.first.info;
          //NOTE: change functionality to store/find node id and change info
          //that way. current way overrides values and doesnt work for
          //multiple repl calls
          System.out.println("located " + functionName + " at node " + fdnode.id );
          node = fdnode.second.first; //change node to list node child node
          if(expr2 != null) { //update variable values
            paramName2 = fdnode.first.first.info;
            updateVariableValues(node, paramName1, paramName2, expr1, expr2);
          }
          else {
            updateVariableValues(node, paramName1, null, expr1, null);
          }
          isStillSearchingTree = false;
        }
        else {
          node = node.second;
        }
      }
      System.out.println("fdnode.second.kind = " + fdnode.second.kind);
      Value result = fdnode.second.evaluate(); //evaluate list of define function
      System.out.println();
      System.out.println("result = " + result.toString());
      System.out.println();
      return result;

      //returning = false;


      // handle bifs
      // if ( funcName.equals("print") ) {
      //   // evaluate the single <expr>
      //   double value = first.first.evaluate();
      //   if ( (int) value == value )
      //     System.out.print( (int) value );
      //   else
      //     System.out.print( value );
      // }
      // else if ( funcName.equals("nl") ) {
      //   System.out.println();
      // }

      // else {// user-defined function

      //   Node body = passArgs( this, funcName );
      //   body.second.execute();

      //   returning = false;

      // }// user-defined function

    } //userfunction



    // else if ( kind.equals("+") || kind.equals("-") ) {
    //   double value1 = first.evaluate();
    //   double value2 = second.evaluate();
    //   if ( kind.equals("+") )
    //     return value1 + value2;
    //   else
    //     return value1 - value2;
    // }

    // else if ( kind.equals("*") || kind.equals("/") ) {
    //   double value1 = first.evaluate();
    //   double value2 = second.evaluate();
    //   if ( kind.equals("*") )
    //     return value1 * value2;
    //   else
    //     return value1 / value2;
    // }

    // else if ( kind.equals("opp") ) {
    //   double value = first.evaluate();
    //   return -value;
    // }

    // else if ( kind.equals("funcCall") ) {
    //   // execute a function call to produce a value

    //   String funcName = info;

    //   double value;  // have all function calls put their value here
    //   // to return once at the bottom

    //   // handle bifs

    //   if ( member( funcName, bif0 ) ) {
    //     if ( funcName.equals("input") )
    //       value =  keys.nextDouble();
    //     else {
    //       error("unknown bif0 name [" + funcName + "]");
    //       value = -1;
    //     }
    //   }
    //   else if ( member( funcName, bif1 ) ) {
    //     double arg1 = first.first.evaluate();

    //     if ( funcName.equals("sqrt") )
    //       value = Math.sqrt( arg1 );
    //     else if ( funcName.equals("cos") )
    //       value = Math.cos( Math.toRadians( arg1 ) );
    //     else if ( funcName.equals("sin") )
    //       value = Math.sin( Math.toRadians( arg1 ) );
    //     else if ( funcName.equals("atan") )
    //       value = Math.toDegrees( Math.atan( arg1 ) );
    //     else if ( funcName.equals("round") )
    //       value = Math.round( arg1 );
    //     else if ( funcName.equals("trunc") )
    //       value = (int) arg1;
    //     else if ( funcName.equals("not") )
    //       value = arg1 == 0 ? 1 : 0;
    //     else {
    //       error("unknown bif1 name [" + funcName + "]");
    //       value = -1;
    //     }
    //   }
    //   else if ( member( funcName, bif2 ) ) {
    //     double arg1 = first.first.evaluate();
    //     double arg2 = first.second.first.evaluate();

    //     if ( funcName.equals("lt") )
    //       value = arg1 < arg2 ? 1 : 0;
    //     else if ( funcName.equals("le") )
    //       value = arg1 <= arg2 ? 1 : 0;
    //     else if ( funcName.equals("eq") )
    //       value = arg1 == arg2 ? 1 : 0;
    //     else if ( funcName.equals("ne") )
    //       value = arg1 != arg2 ? 1 : 0;
    //     else if ( funcName.equals("pow") )
    //       value = Math.pow( arg1 , arg2 );
    //     else if ( funcName.equals("and") )
    //       value = arg1!=0 && arg2!=0 ? 1 : 0;
    //     else if ( funcName.equals("or") )
    //       value = arg1!=0 || arg2!=0 ? 1 : 0;
    //     else {
    //       error("unknown bif2 name [" + funcName + "]");
    //       value = -1;
    //     }
    //   }

      // else {// user-defined function

      //   Node body = passArgs( this, funcName );
      //   body.second.execute();

      //   value = returnValue;

      //   returning = false;

      // }// user-defined function call

      // // uniformly finish
      // return value;

    // }// funcCall

    else {
      error("Evaluating unknown kind of node [" + kind + "]" );
      return new Value(0);
    }

  }// evaluate

  private void updateVariableValues(
    Node node, String name1, String name2, Value value1, Value value2
  ) {
    if(node != null) {
      System.out.println();
      System.out.println("updateVariableValues(): ");
      System.out.println("node.kind = " + node.kind);
      System.out.println("node.info = " + node.info);
      System.out.println("name1 = " + name1);
      System.out.println("name2 = " + name2);
      //System.out.println("new value = " + value.toString());
      System.out.println("entry1 = " + value1.toString());
      if(value2 != null) {
        System.out.println("entry2 = " + value2.toString());
      }
      System.out.println();
      if( value2 != null ) { //if there are two parameters
        if( node.info.equals(name1) ) {
          System.out.println("node.value: " + node.value + "  to  " + value1);
          //node.value = value1.toString();
          node.value = value1;
        }
        else if( node.info.equals(name2) ) {
          //node.value = value2.toString();
          node.value = value2;
        }
        updateVariableValues(node.first, name1, name2, value1, value2);
        updateVariableValues(node.second, name1, name2, value1, value2);
      }
      else { //else there is only one parameter
        if( node.info.equals(name1) ) {
          //node.value = value1.toString();
          node.value = value1;
        }
        updateVariableValues(node.first, name1, name2, value1, value2);
        updateVariableValues(node.second, name1, name2, value1, value2);
      }
    }
  }

  private final static String[] bif0 = { "input", "nl" };
  private final static String[] bif1 = { "sqrt", "cos", "sin", "atan",
    "round", "trunc", "not" };
  private final static String[] bif2 = { "lt", "le", "eq", "ne", "pow",
    "or", "and"
  };

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
  // private static Node passArgs( Node funcCallNode, String funcName ) {

  //   // locate the function in the function definitions

  //   Node node = SL3.root;  // the program node
  //   //node = node.second;  // now is the funcDefs node
  //   Node fdnode = null;
  //   while ( node != null && fdnode == null ) {
  //     if ( node.first.info.equals(funcName) ) {// found it
  //       fdnode = node.first;
  //       System.out.println("located " + funcName + " at node " + fdnode.id );
  //     }
  //     else
  //       node = node.second;
  //   }

  //   MemTable newTable = new MemTable();

  //   if ( fdnode == null ) {// function not found
  //     error( "Function definition for [" + funcName + "] not found" );
  //     return null;
  //   }
  //   else {// function name found
  //     Node pnode = fdnode.first; // current params node
  //     Node anode = funcCallNode.first;  // current args node
  //     while ( pnode != null && anode != null ) {
  //       // store argument value under parameter name
  //       newTable.store( pnode.first.info,
  //           anode.first.evaluate() );
  //       // move ahead
  //       pnode = pnode.second;
  //       anode = anode.second;
  //     }

  //     // detect errors
  //     if ( pnode != null ) {
  //       error("there are more parameters than arguments");
  //     }
  //     else if ( anode != null ) {
  //       error("there are more arguments than parameters");
  //     }

  //     System.out.println("at start of call to " + funcName + " memory table is:\n" + newTable );

  //     // manage the memtable stack
  //     memStack.add( newTable );
  //     table = newTable;

  //     return fdnode;

  //   }// function name found

  // }// passArguments

}// Node
