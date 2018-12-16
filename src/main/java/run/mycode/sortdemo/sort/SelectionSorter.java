package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a selection sort on a DemoArray
 * 
 * NOTE: implementors should also include a private static final String NAME 
 * that includes a readable name for the sorting algorithm, or the classname 
 * will be used
 * 
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class SelectionSorter<T extends Comparable<T>> implements SteppableSorter<T> {
    
    public static final String NAME = "Selection Sort";
    
    private final DemoArray<T> arr;
    
    private boolean done;
    private int i;
    private int j;
    private int min;
    
    /**
     * Prepare to selection sort a DemoArray
     * @param arr the array to sort
     */
    public SelectionSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.done = arr.length() <= 1;
        
        // initialize the sort pointers
        this.i = 0;
        this.j = i+1;
        this.min = 0;      
    }
    
    /**
     * Perform the next step in the sort
     */
    @Override
    public void step() {
        if (done == true) {
            return;
        }
        
        // Compare the item at the current min position with the item at j
        if (arr.compare(min, j) > 0) {
            min = j; // Update if we have found a new minimum
        } 
        
        j++; // Move to the next element
        
        // Once the j pointer has scanned the whole array
        if (j >= arr.length()) { 
            arr.swap(i, min); // Swap the current item and the smallest found
            
            // Update the index pointers
            i++;       // Move the i index to the next element
            j = i + 1; // Move the j pointer to the element after i
            min = i;   // Assume the element at i is the new minimum
        }
        
        // Once i has scanned the whole array, the sort is complete
        if (i >= arr.length() - 1) {
            done = true;
        }        
    }

    /**
     * Check if the sort operation is complete
     * 
     * @return true if the sort has completed 
     */
    @Override
    public boolean isSorted() {
        return done;
    }
    
    @Override
    public boolean usesScratchArray() {
        return false;
    }

    @Override
    public DemoArray<T> getScratchArray() {
        return null;
    }
}
