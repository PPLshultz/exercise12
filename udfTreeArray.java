import java.util.ArrayList;

class udfTreeArray{
 
    private static ArrayList<  Node  > udfTreeArray;

    public udfTreeArray() {
        udfTreeArray = new ArrayList<  Node  >();
    }

//store the Corgi array into the array list with a specified array size
// return the index of where the array was stored in the ArrayList
    public void storeUdf( Node defNode ) {

        //String udfDesc = defNode.info;
        udfTreeArray.add( defNode ); 
        System.out.println( " User UDF Node stored at index " + (udfTreeArray.size()-1) + " of udfArray");
  
    }// storeArray

    // Get the array from the CorgiArray index
    public Node getUdf( int index ){

        Node returnUdf = udfTreeArray.get( index );

        return returnUdf;
    } 
    
    public int getSize(){
        int size = udfTreeArray.size();
        return size;
    }
    
    
    
    
    
    // get
    // // Get the array from the CorgiArray index
    // public Node get( String info ){

    //     for( i = 0; )
    //     double[] returnArray = udfTreeArray.get( index );

    //     return returnArray;
    // } // get



  } // end udfTreeArray