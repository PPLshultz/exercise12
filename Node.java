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






}// Node
