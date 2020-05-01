import java.util.ArrayList;

class ValueTable{
 
    private static ArrayList<  Value  > ValueTable;

    public ValueTable() {
        ValueTable = new ArrayList<  Value  >();
    }
//store the Corgi array into the array list with a specified array size
// return the index of where the array was stored in the ArrayList
    public void storeValue( Value val ) {

        //String udfDesc = defNode.info;
        ValueTable.add( val ); 
        System.out.println( " User Value stored at index " + (ValueTable.size()-1) + " of ValueTable");
  
    }// storeArray

    // Get the array from the CorgiArray index
    public Value getArgumentValue( int index ){

        Value returnUdf = ValueTable.get( index );

        return returnUdf;
    } 
    
    public int getSize(){
        int size = ValueTable.size();
        return size;
    }
    
    //Check if the argument is already in the table
    public boolean isin( String argument , String meth){

        boolean inList = false;

        if (ValueTable.size() == 0){
            return inList;
        }

        for( int i =0; i<= ValueTable.size()-1; i++){
            
            Value iterVal = ValueTable.get( i );
            String valListArg = iterVal.getArg();
            String valListMeth = iterVal.getMeth();
            if(argument.equals(valListArg) && meth.equals(valListMeth)){
                inList = true;
                return inList;
            } // end if
            
        }// end for
        return inList;
    }

    // remove an argumnet form the list
    public void removeArg( String argument){

        for( int i =0; i<= ValueTable.size(); i++){
            Value iterVal = ValueTable.get( i );
            String valListArg = iterVal.getArg();
            if(argument.equals(valListArg)){
                ValueTable.remove( i );
                return;
            } // end if
        }// end for
        return;
    }
    
    
    // get
    // // Get the array from the CorgiArray index
    // public Node get( String info ){

    //     for( i = 0; )
    //     double[] returnArray = udfTreeArray.get( index );

    //     return returnArray;
    // } // get



  } // end udfTreeArray