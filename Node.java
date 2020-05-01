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
  private static ValueTable valueTable = new ValueTable();
  private static String currentUDF;
//****************************************************************************************************************************** */
//Create the array to store user defined function trees 
  private static udfTreeArray udfArray = new udfTreeArray();
//****************************************************************************************************************************** */
  // create this so we vcan stroe a temporary list
  private static Value returnList = new Value();
  // stack of memories for all pending calls
//  private static ArrayList<MemTable> memStack = new ArrayList<MemTable>();
  // convenience reference to top MemTable on stack
//  private static MemTable table = new MemTable();

  // status flag that causes <stmts> nodes to abort asking second
  // to execute
//  private static boolean returning = false;

  // value being returned
//  private static double returnValue = 0;

  private static Node root;  // root of the entire parse tree

  public static Node udfNodeToChange;

  public static Node master;

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

// public Node insertNode( Node defNode , Node replNode){ 

//    Node first = defNode.first;
//    Node third = replNode.first;

//    if (defNode.second != null){
//       Node second = defNode.second;
//       return new Node( "root" , first , second, third );
//    }
   
//    else {
//       return new Node( "root" , first , null, third );
//    }
// }
public Node insertNode( Node replNode){ 

   Node third = replNode.first;

   if (second != null){
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


















   // public Value execute() {

   //    Value deleteMe = new Value("0");
   //    System.out.println("Executing node " + id + " of kind " + kind );

   //    if (kind.equals("root")){
   //       first.execute();
   //       if(second != null){
   //          second.execute();  
   //       }
   //       third.execute();
   //       return deleteMe;
   //    }

   //    //if ( kind.equals("program") ) {
   //    if ( kind.equals("defs") ) {
   //       root = this;  // note the root node of entire tree
   //       first.execute();
   //       return deleteMe;
   //    }// program

   //    else if( kind.equals("def") ) {
   //      first.execute();
   //      if ( second != null ) {
   //        second.execute();
   //      }
   //      return deleteMe;
   //    }

   //    else {
   //       error("Executing unknown kind of node [" + kind + "]");
   //       return deleteMe;
   //    }

   // }// execute


























 public void init( Node rootNode){
   master = rootNode;
 }







   // compute and return value produced by this node
   public Value evaluate() {


      System.out.println("Evaluating node " + id + " of kind " + kind );

//#####################################################################################



//##################################################################################
         if (kind.equals("root")){

            //build the user defined table
            //Value firstNode = first.evaluate();
         //Check to see if this is the first iteration of REPL,  dont build th eudf array list if it is  
         
         //Wipe all the static lists
         returnList = new Value();
         udfArray = new udfTreeArray();
         valueTable = new ValueTable();

         int numOfUDF = udfArray.getSize();
            if (numOfUDF == 0){
               first.udfBuilder();
               
               
         //     Node firstUDF = udfArray.getUdf(0);
               //If there is a second user defined function... then build the table
            if( second != null ){
               //Value secondNode = second.evaluate();  
               second.udfBuilder();
         //        Node secondUDF = udfArray.getUdf(1);
         //        Node thirdUDF = udfArray.getUdf(2);
         //        Node fourthUDF = udfArray.getUdf(3);
            }
         }
            Value thirdNode = third.evaluate();
            return thirdNode;
         }




         else if ( kind.equals("RPAREN")){
            Value emptyList = new Value(); //this is a temporary list for evaluations
            return emptyList;
         }


         else if ( kind.equals("list") ) {

            Value r = first.evaluate();

            if( first.kind == "plus" || first.kind == "minus" ||
                first.kind == "times" || first.kind == "div" ||
                first.kind == "div" || first.kind == "lt" || 
                first.kind == "le" || first.kind == "eq" ||
                first.kind == "ne" || first.kind == "and" ||
                first.kind == "or" || first.kind == "not" ||
                first.kind == "quote" || first.kind == "rest" ||
                first.kind == "udf1"  || first.kind == "udf2" ||
                first.kind == "list"){ //remove list to fix

                  return r;
                }
            //returnList = new Value(); //this is a temporary list for evaluations
            returnList = new Value(); //reset the temp list
            

            return r;
  
         } // end list         

         
         else if( kind.equals("items") ){

            Value firstItem = first.evaluate();
            returnList = returnList.insert(firstItem);
               
            if (second != null){

                  second.evaluate();
                  return returnList;
                  } // end if "second node"
   
            else if (firstItem.getSize()  == 0  ||  firstItem.getSize() == 1){
               return firstItem;
            }

            else{
            Value temp = new Value(); //temp list to rearrange values of the list to output correctly
            int listSize = firstItem.getSize();   
               for ( int i = 0; i <= listSize-1; ++i) {
                  Value num = firstItem.getter(i);
                  temp = temp.insert(num);
                   } //end for "re-arrange"

            return temp;
                  }


           } // end items



           //number
           else if ( kind.equals("NUMBER") ) {
            Value r = new Value( info );
            return r;
            } // end number   
  

            else if( kind.equals("NAME") ) {
               System.out.println("NAME = " + info);
               //return new Value(info);

               //find the value in the ValueTable
               boolean searchTable = true;
               int iterator = 0;
               while(searchTable){
               
                     Value searcherValue = valueTable.getArgumentValue(iterator);
                     String arg = searcherValue.getArg();
                     String meth = searcherValue.getMeth();
                     System.out.println("NAME = " + arg);
                     System.out.println("NAME = " + meth);
                     //if (info.equals( arg ) && currentUDF.equals(meth)){
                        if (info.equals( arg )){   
                        Value argVal = searcherValue.getValue();
                        return argVal;
                     }
                     iterator = iterator + 1;
               }

               Value returnValue = new Value(info);
               // return new Value(value);
               return returnValue;
             }




         //REPL commands
        
         // read , wait for user input and display value
         else if(kind.equals("read") ) {

            String userInput = keys.nextLine();
            Value output = new Value(userInput);
            return output;

         } // end read  




//Test this one
         // write , display x
         else if( kind.equals("write") ) {

            //it can be a number
            if(first.kind.equals("NUMBER")){
               Value number = first.evaluate();
               return number;
            }
            //if its a list
            Value output = first.evaluate();

            return output;

         } // end write
      
         else if( kind.equals("quote") ) {

            //it can be a number
            if(first.kind.equals("NUMBER")){
               Value number = first.evaluate();
               return number;
            }
            //if its a list
            Value output = first.evaluate();
            return output;

         } // end write


         // if
         else if( kind.equals("if") ) { 
            
            Value expr1 = first.evaluate();
            double val1 = expr1.getNumber();

            if (val1 != 0){
               Value expr2 = second.evaluate();
               return expr2;
            }
            
            Value expr3 = third.evaluate();
            return expr3;

            }  //end if



         
         // ins , insert the first object into the second objects list
         else if( kind.equals("ins") ) {
            
            Value r = new Value();

            Value thingToInsert = first.evaluate();
            Value listToBeInserted = first.second.evaluate();

            r = listToBeInserted.insert( thingToInsert );

            return r;

         } // end ins


         // ins , insert the first object into the second objects list
         else if( kind.equals("first") ) {
            
            Value listToGrabItem = first.evaluate();

            if (listToGrabItem.getSize() == 0){
               System.out.println("\nError: (first () )can not retrieve from an empty list");
               System.exit(1);
            }

            Value firstItem = listToGrabItem.first();

            return firstItem;

         } // end ins

         // rest , delete the first item from the list
         else if( kind.equals("rest") ) {
            
            Value listToGrabItem = first.evaluate();

            if (listToGrabItem.getSize() == 0){
               System.out.println("\nError: (rest ()) can not delete from an empty list");
               System.exit(1);
            }

            Value firstItem = listToGrabItem.rest();

            return firstItem;

         } // end rest

         // null , 1 if empty list "()" ,0 if there is content 
         else if( kind.equals("null") ) {
            
            Value listToGrabItem = first.evaluate();

            if (listToGrabItem.isEmpty()){
               Value r = new Value("1");
               return r;
            }

            Value notNull = new Value("0");
            return notNull;

         } // end null

         // num , 1 if a number , 0 if not a number
         else if( kind.equals("num") ) {
            
            Value x = first.evaluate();
             
            if (x.isNumber()){
               Value r = new Value("1");
               return r;
            }

            Value notNumber = new Value("0");
            return notNumber;

         } // end num



         //plus
         else if( kind.equals("plus") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = val1 + val2;

            Value r = new Value( result );

            return r;
         } // end plus
      
         // minus
         else if( kind.equals("minus") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = val1 - val2;

            Value r = new Value( result );

            return r;
          } // end minus

          // times
          else if( kind.equals("times") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = val1 * val2;

            Value r = new Value( result );

            return r;
          }// end times

          // divide
          else if( kind.equals("div") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = val1 / val2;

            Value r = new Value( result );

            return r;
          } // end divide

          // less than
          else if( kind.equals("lt") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = 0;
            if( val1 < val2 ) {
               result = 1;
             }
            Value r = new Value( result );     
            
            return r;
          } // end less than
         
          // less than or equal
          else if( kind.equals("le") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = 0;
            if( val1 <= val2 ) {
               result = 1;
             }
            Value r = new Value( result );   
            
            return r;
          } // end less than or equal

          // equal (num == num)
          else if( kind.equals("eq") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = 0;
            if( val1 == val2 ) {
               result = 1;
             }
            Value r = new Value( result );    
            
            return r;
          } // end equal

          // not equal (num != num)
          else if( kind.equals("ne") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = 0;
            if( val1 != val2 ) {
               result = 1;
             }
            Value r = new Value( result ); 
            
            return r;
          } // end not equal

          // AND ( boolean AND boolean), ("is a value" AND "is a value")
          else if( kind.equals("and") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = 0;
            if( (val1 != 0) && (val2 != 0) ) {
              result = 1;
            }
            Value r = new Value( result ); 

            return r;
          } // end AND

          // or ( boolean OR boolean), ("is a value" OR "is a value")
          else if( kind.equals("or") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = 0;
            if( (val1 != 0) || (val2 != 0) ) {
              result = 1;
            }
            Value r = new Value( result ); 

            return r;
          } // end or

          // not , return 1 if x is 0 else 1
          else if( kind.equals("not") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            double result = 0;
            if( val1 == 0 ) {
              result = 1;
            }
            Value r = new Value( result ); 

            return r;
          } // end not



          else if( kind.equals("ins") ) {

            Value value1 = first.evaluate();
            double val1 = value1.getNumber();

            Value value2 = second.evaluate();
            double val2 = value2.getNumber();

            double result = 0;
            if( (val1 != 0) || (val2 != 0) ) {
              result = 1;
            }
            Value r = new Value( result ); 

            return r;
          } // end ins


          // if it is a user defined function one parameter
          else if (info.equals("udf1") ){
            //read the udf off of the table and then run the node at that location
            //you also need to pass the parameters for the udf 
            Value r = new Value( 5 ); //Please kill me
            
            int amountOfUDF = udfArray.getSize();
            String searchKey = kind;
            Boolean keepSearching = true;
            int index = 0;
/////////////////////////////////////////////////////////////
            //currentUDF = searchKey;
        //    Node udfSelect = udfArray.getUdf(index);
        //    String x = udfSearcher.first.info;
        //    Value argEvaluatedX = first.evaluate();
            //Value tableStoreX = new Value( x , searchKey , argEvaluatedX);
            
           // boolean argExistsX = valueTable.isin( x , searchKey );
            
            //update the table
        //    if (argExistsX && currentUDF.equals(searchKey)){
               //remove the arg from the list
        //       valueTable.removeArg( x );
        //    }
           // valueTable.storeValue( tableStoreX ) ;

////////////////////////////////////////////////////////////////////
            while( keepSearching ){
               Node udfSearcher = udfArray.getUdf(index);

               //If you find the udf Node in the array list "udfSearcher"
               if (udfSearcher.info.equals(searchKey)){

                  // There is only 1 argument
////////////////////////////////////////////////////////////
                  String x = udfSearcher.first.info;
                  currentUDF = searchKey;
                  Value argEvaluatedX = first.evaluate();
                  Value tableStoreX = new Value( x , searchKey ,argEvaluatedX);
                  
                  boolean argExistsX = valueTable.isin( x , searchKey );
                  
                  //update the table
                  if (argExistsX && currentUDF.equals(searchKey)){
                     //remove the arg from the list
                     valueTable.removeArg( x );
                  }
                  valueTable.storeValue( tableStoreX ) ;
///////////////////////////////////////////////////////////
                  //Lets try to take the kind.equals(name) approach, this other way isnt working
                  Value udfEvaluated = udfSearcher.second.evaluate();

                  return udfEvaluated;
                  //keepSearching = false;
               }
               // If you searched through all the UDF and didnt find a match
               else if (amountOfUDF == index){
                  System.out.print("The UDF function was not found");
                  keepSearching = false;
               }

               index = index + 1;
            } // end while (udf searcher)

            return r;
          }






          // if it is a user defined function two parameter
          else if (info.equals("udf2") ){
            //read the udf off of the table and then run the node at that location
            //you also need to pass the parameters for the udf 
            Value r = new Value( 5 ); //Please kill me
            
            int amountOfUDF = udfArray.getSize();
            String searchKey = kind;
            Boolean keepSearching = true;
            int index = 0;

            while( keepSearching ){
               Node udfSearcher = udfArray.getUdf(index);

               //If you find the udf Node in the array list "udfSearcher"
               if (udfSearcher.info.equals(searchKey)){

                  if(udfSearcher.first.first != null){
                     //get the first variable argument
                     String x = udfSearcher.first.info;
                     String y = udfSearcher.first.first.info;
                     Value argEvaluatedX = first.evaluate();
                     Value argEvaluatedY = second.evaluate();

                     Value tableStoreX = new Value( x , searchKey, argEvaluatedX);
                     Value tableStoreY = new Value( y , searchKey, argEvaluatedY);

                     boolean argExistsX = valueTable.isin( x , searchKey);
                     boolean argExistsY = valueTable.isin( y , searchKey);
                     if (argExistsX){
                        //remove the arg from the list
                        valueTable.removeArg( x );
                     }
                     valueTable.storeValue( tableStoreX ) ;
                     //get the second variable argument
                     if (argExistsY){
                        //remove the arg from the list
                        valueTable.removeArg( y );
                     }
                     valueTable.storeValue( tableStoreY ) ;
                     } // end if second argument
                  
                  // There is only 1 argument
                  else{
                     String x = udfSearcher.first.info;
                     Value argEvaluatedX = first.evaluate();
                     Value tableStoreX = new Value( x , searchKey, argEvaluatedX);
                     boolean argExistsX = valueTable.isin( x , searchKey);
                     
                     //update the table
                     if (argExistsX){
                        //remove the arg from the list
                        valueTable.removeArg( x );
                     }
                     valueTable.storeValue( tableStoreX ) ;
                  }

                  //Lets try to take the kind.equals(name) approach, this other way isnt working
                  Value udfEvaluated = udfSearcher.second.evaluate();

                  return udfEvaluated;
                  //keepSearching = false;
               }
               // If you searched through all the UDF and didnt find a match
               else if (amountOfUDF == index){
                  System.out.print("The UDF function was not found");
                  keepSearching = false;
               }

               index = index + 1;
            } // end while (udf searcher)

            return r;

          }








          else{
            System.out.println("If you got here something went terribly wrong");
            Value r = new Value( "placeholder" ); 

            return r;
          }

      } //end evaluate



      public void udfBuilder(){
         //THe only job of this is to record where shit is in the master tree and save 
         // it to a single argument table or to a two argument table
         if ( kind.equals("def") ) { //do user defined func like (foo 2)
           //this else if block updates the variable values within def parse tree
           udfArray.storeUdf(this);
        }
        else if( kind.equals("defs") ){
           udfArray.storeUdf(first);
           if (second != null){
              second.udfBuilder();
           }
        }
        else{
           System.out.println("error creating node array list");
        }
     } //end udf builder




     // Create a method to go through the tree and insert a argument node with the same "info"
   //   public Node nodeInserter(Node udfNode , Node argumentNode , String arg){

   //    //udfNodeToChange
   //    Node tempNode = udfNode;

   //    // if (udfNode == null)  {
   //    //    return;
   //    // }
   //    // if(udfNode.info.equals( arg )){
   //    //    udfNode = argumentNode;
   //    // }
   //    // nodeInserter(udfNode.first , argumentNode , arg);
   //    // nodeInserter(udfNode.second , argumentNode , arg); 
   //    // nodeInserter(udfNode.third , argumentNode , arg);


   //    {  
          
   //        if (udfNode.first == null && udfNode.second == null && udfNode.third == null)  {
   //          System.out.print(udfNode.info+" \n"); 
   //          if(udfNode.info.equals( arg )){
   //             System.out.print(udfNode.id + " you have isolated node id with arg\n");
   //             udfNode = argumentNode;
   //             return udfNode;
   //          }
   //        }
   //       Node firstScan = nodeInserter(udfNode.first , argumentNode , arg);      
   //       Node secondScan = nodeInserter(udfNode.second , argumentNode , arg); 
   //       Node thirdScan = nodeInserter(udfNode.third , argumentNode , arg);
   //       Node argsChanged = new Node( arg , firstScan, secondScan, thirdScan);
   //       return argsChanged;
   //    }  
   //    return udfNode;
   // }// end Node inserter



      //       // if( second != null ) { //evaluate and expression2 if available
      //       //   expr2 = second.evaluate();
      //       // }
      //       //Node node = SL3.root;
      //       Node node = this;

      //       Node fdnode = null; //fdnode means function definition node
      //       boolean isStillSearchingTree = true;
      //       String paramName1;
      //       String paramName2 = null;
      //       while ( node != null && isStillSearchingTree ) { //find user function
      //         if ( node.first.info.equals(functionName) ) { // found it
      //           fdnode = node.first;
      //           paramName1 = fdnode.first.info;
      //           //NOTE: change functionality to store/find node id and change info
      //           //that way. current way overrides values and doesnt work for
      //           //multiple repl calls
      //           System.out.println("located " + functionName + " at node " + fdnode.id );
      //           node = fdnode.second.first; //change node to list node child node
      //           if(expr2 != null) { //update variable values
      //             paramName2 = fdnode.first.first.info;
      //           }
      //           else {
      //             Value r = new Value( "PlaceHolder" ); 
      //             return r;
      //           }
      //           isStillSearchingTree = false;
      //         }
      //         else {
      //          //node = node.second;
      //           node = node.first;
      //         }
      //       }
      //       System.out.println("fdnode.second.kind = " + fdnode.second.kind);
      //       Value result = fdnode.second.evaluate(); //evaluate list of define function
      //       System.out.println();
      //       System.out.println("result = " + result.toString());
      //       System.out.println();
      //       return result;
      //    }
      
      // else{
      //    Value r = new Value( "There is an error reading the Node, Nothing found" );
      //    return r;
      // } // else 




   // private void updateVariableValues(Node node, String name1, String name2, String value1, String value2) {
   //    if(node != null) {
   //      System.out.println();
   //      System.out.println("updateVariableValues(): ");
   //      System.out.println("node.kind = " + node.kind);
   //      System.out.println("node.info = " + node.info);
   //      System.out.println("name1 = " + name1);
   //      System.out.println("name2 = " + name2);
   //      //System.out.println("new value = " + value.toString());
   //      System.out.println("entry1 = " + value1.toString());
   //      if(value2 != null) {
   //        System.out.println("entry2 = " + value2.toString());
   //      }
   //      System.out.println();
   //      if( value2 != null ) { //if there are two parameters
   //        if( node.info.equals(name1) ) {
   //          System.out.println("node.value: " + node.info + "  to  " + value1);
   //          //node.value = value1.toString();
   //          node.info = value1;

   //        }
   //        else if( node.info.equals(name2) ) {
   //          //node.value = value2.toString();
   //          node.info = value2;
   //        }
   //        updateVariableValues(node.first, name1, name2, value1, value2);
   //        updateVariableValues(node.second, name1, name2, value1, value2);
   //      }
   //      else { //else there is only one parameter
   //        if( node.info.equals(name1) ) {
   //          //node.value = value1.toString();
   //          node.info = value1;
   //        }
   //        updateVariableValues(node.first, name1, name2, value1, value2);
   //        updateVariableValues(node.second, name1, name2, value1, value2);
   //      }
   //    }
   //  }





//           else if( first.kind.equals("null") ) {

//                double value = first.first.evaluate();
               
//                if(first.first.kind.equals("list") && value == 1 ){
//                   return value;
//                }
//                else{ 
//                   return 0;
//                }

//           }

//           else if (first.kind.equals("RPAREN")){
//             double result = 1;
//             return result;
//          }

//          else if( first.kind.equals("num") ) {
            
//             double value = first.first.evaluate();

//             if (first.first.kind.equals("NUMBER")){
//                return 1;
//             }
//             else{
//                return 0;
//             }
//          }
//          else if( first.kind.equals("list") ) {

//             if (first.first.kind.equals("list")){
//                return 1;
//             }
//             else{
//                return 0;
//             }

//          }
         
//          else if( first.kind.equals("items") ) { 

//             if( first.first.first.info.equals("write")){
//                double value1 = first.first.second.first.evaluate();
//                return value1;
//             }
//             //Not sure what the QUOTE does Derek
//             // if( first.first.first.info.equals("quote")){
//             //    double value1 = first.first.second.first.evaluate();
//             //    return value1;
//             // }

//             double value1 = first.first.evaluate();
            
//             return value1;
//             }

//             else if( first.kind.equals("if") ) { 
               
//                double expr1 = first.first.evaluate();

//                if (expr1 != 0){
//                   double expr2 = first.second.evaluate();
//                   return expr2;
//                }
               
//                double expr3 = first.third.evaluate();
//                return expr3;

//                }            





//          double deleteMe = 100000;
//          return deleteMe;
//       } //list


//       else if( kind.equals("items") ) { 

//          double value1 = first.evaluate();
         
//          return value1;
//          }
// //#########################################################

//       else if( kind.equals("NAME") ) {

//          if (info.equals("read")){ //REPL command to read the next integerfrom the user
//          String userInputNumber = keys.nextLine();
//          double value1 = Double.parseDouble(userInputNumber);

//          return value1;
//          }

//          else if(info.equals("quit")){ //REPL command to quit
//             double value1 = 999999;
   
//             return value1;
//             }

//          else if(info.equals("nl")){ //REPL command to quit
//             double value1 = 999998;
   
//             return value1;
//             }
// //##############################################################################










//          double deleteMe = 100000;
//          return deleteMe;
//       }




//       else if ( kind.equals("NUMBER") ) {
//          return Double.parseDouble( info );
//       }










// /***************************************************************************
// START: predefined functions that take numeric inputs and produce a numeric result
// ***************************************************************************/
//       else if( kind.equals("if") ) {
//          double value1 = first.evaluate();
//          double result;
//          double value3;
//          if( value1 != 0 ) {
//             result = second.evaluate();
//          }
//          else {
//             result = third.evaluate();
//          }
//          return result;
//       }
//       else if( kind.equals("plus") ) {
//          double value1 = first.evaluate();
//          double value2 = second.evaluate();
//          return value1 + value2;
//       }

// /***************************************************************************
// END: predefined functions that take numeric inputs and produce a numeric result
// ***************************************************************************/



//       else if ( kind.equals("+") || kind.equals("-") ) {
//          double value1 = first.evaluate();
//          double value2 = second.evaluate();
//          if ( kind.equals("+") )
//             return value1 + value2;
//          else
//             return value1 - value2;
//       }

//       else if ( kind.equals("*") || kind.equals("/") ) {
//          double value1 = first.evaluate();
//          double value2 = second.evaluate();
//          if ( kind.equals("*") )
//             return value1 * value2;
//          else
//             return value1 / value2;
//        }

//        else if ( kind.equals("opp") ) {
//           double value = first.evaluate();
//           return -value;
//        }

//        else if ( kind.equals("funcCall") ) {
//           // execute a function call to produce a value

//          String funcName = info;

//          double value;  // have all function calls put their value here
//                         // to return once at the bottom

//          // handle bifs

//          if ( member( funcName, bif0 ) ) {
//             if ( funcName.equals("input") )
//                value =  keys.nextDouble();
//             else {
//                error("unknown bif0 name [" + funcName + "]");
//                value = -1;
//             }
//          }
//          else if ( member( funcName, bif1 ) ) {
//             double arg1 = first.first.evaluate();

//             if ( funcName.equals("sqrt") )
//                value = Math.sqrt( arg1 );
//             else if ( funcName.equals("cos") )
//                value = Math.cos( Math.toRadians( arg1 ) );
//             else if ( funcName.equals("sin") )
//                value = Math.sin( Math.toRadians( arg1 ) );
//             else if ( funcName.equals("atan") )
//                value = Math.toDegrees( Math.atan( arg1 ) );
//             else if ( funcName.equals("round") )
//                value = Math.round( arg1 );
//             else if ( funcName.equals("trunc") )
//                value = (int) arg1;
//             else if ( funcName.equals("not") )
//                value = arg1 == 0 ? 1 : 0;
//             else {
//                error("unknown bif1 name [" + funcName + "]");
//                value = -1;
//             }
//          }
//          else if ( member( funcName, bif2 ) ) {
//             double arg1 = first.first.evaluate();
//             double arg2 = first.second.first.evaluate();

//             if ( funcName.equals("lt") )
//                value = arg1 < arg2 ? 1 : 0;
//             else if ( funcName.equals("le") )
//                value = arg1 <= arg2 ? 1 : 0;
//             else if ( funcName.equals("eq") )
//                value = arg1 == arg2 ? 1 : 0;
//             else if ( funcName.equals("ne") )
//                value = arg1 != arg2 ? 1 : 0;
//             else if ( funcName.equals("pow") )
//                value = Math.pow( arg1 , arg2 );
//             else if ( funcName.equals("and") )
//                value = arg1!=0 && arg2!=0 ? 1 : 0;
//             else if ( funcName.equals("or") )
//                value = arg1!=0 || arg2!=0 ? 1 : 0;
//             else {
//                error("unknown bif2 name [" + funcName + "]");
//                value = -1;
//             }
//          }

//          else {// user-defined function

//             Node body = passArgs( this, funcName );
//             body.second.execute();

//             value = returnValue;

//             returning = false;

//          }// user-defined function call

//          // uniformly finish
//          return value;

//        }// funcCall

//        else {
//           error("Evaluating unknown kind of node [" + kind + "]" );
//           return -1;
//        }

//    }// evaluate

   // private final static String[] bif0 = { "input", "nl" };
   // private final static String[] bif1 = { "sqrt", "cos", "sin", "atan", "round", "trunc", "not" };
   // private final static String[] bif2 = { "lt", "le", "eq", "ne", "pow", "or", "and" };

   // // return whether target is a member of array
   // private static boolean member( String target, String[] array ) {
   //    for (int k=0; k<array.length; k++) {
   //       if ( target.equals(array[k]) ) {
   //          return true;
   //       }
   //    }
   //    return false;
   // }

   // // given a funcCall node, and for convenience its name,
   // // locate the function in the function defs and
   // // create new memory table with arguments values assigned
   // // to parameters
   // // Also, return root node of body of the function being called
   // private static Node passArgs( Node funcCallNode, String funcName ) {

   //    // locate the function in the function definitions

   //    Node node = root;  // the program node
   //    node = node.second;  // now is the funcDefs node
   //    Node fdnode = null;
   //    while ( node != null && fdnode == null ) {
   //       if ( node.first.info.equals(funcName) ) {// found it
   //          fdnode = node.first;
   //          System.out.println("located " + funcName + " at node " + fdnode.id );
   //       }
   //       else
   //         node = node.second;
   //    }

   //    MemTable newTable = new MemTable();

   //    if ( fdnode == null ) {// function not found
   //       error( "Function definition for [" + funcName + "] not found" );
   //       return null;
   //    }
   //    else {// function name found
   //       Node pnode = fdnode.first; // current params node
   //       Node anode = funcCallNode.first;  // current args node
   //       while ( pnode != null && anode != null ) {
   //          // store argument value under parameter name
   //          newTable.store( pnode.first.info,
   //                          anode.first.evaluate() );
   //          // move ahead
   //          pnode = pnode.second;
   //          anode = anode.second;
   //       }

   //       // detect errors
   //       if ( pnode != null ) {
   //          error("there are more parameters than arguments");
   //       }
   //       else if ( anode != null ) {
   //          error("there are more arguments than parameters");
   //       }

   //       System.out.println("at start of call to " + funcName + " memory table is:\n" + newTable );

   //       // manage the memtable stack
   //       memStack.add( newTable );
   //       table = newTable;

   //       return fdnode;

   //    }// function name found

   // }// passArguments

}// Node
