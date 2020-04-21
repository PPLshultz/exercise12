
import java.util.ArrayList;

class SL3Array{
 
    private static ArrayList<  double[]  > corgiArray;

    public SL3Array() {
      corgiArray = new ArrayList<  double[]  >();
    }

//store the Corgi array into the array list with a specified array size
// return the index of where the array was stored in the ArrayList
    public int storeArray( double value ) {
        
        int valueInt = (int)value;
        double[] newArray = new double[valueInt];

        for( int i = 0; i <= valueInt-1; i++){
            newArray[i] = 0;
        }
        corgiArray.add( newArray ); 
        System.out.println( " Array stored at index " + (corgiArray.size()-1) + " of arrayOfArrays");

        return corgiArray.size()-1;  // the index that the array is in the array list     
    }// storeArray

    // Add a double[] into the Corgi array list
    public void add( double[] array ){

        corgiArray.add( array );

    } // add

    // Get the array from the CorgiArray index
    public double[] get( int index ){

        double[] returnArray = corgiArray.get( index );

        return returnArray;
    } // get

    // Change content of a specific array

    public void changeContent( int Arraysindex , int index , double content){

        double[] neededArray = corgiArray.get( Arraysindex ); // Get the required array to manipulate

        neededArray[index] = content; // Put the content into the array

        corgiArray.set( Arraysindex, neededArray ); // Replace the old array with the new content

    } // changeContent


  }