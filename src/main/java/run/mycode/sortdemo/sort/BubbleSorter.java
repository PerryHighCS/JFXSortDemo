package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.DemoArray;

/**
 * Perform a bubble sort on a DemoArray
 * 
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class BubbleSorter<T extends Comparable<T>> implements SteppableSorter {
    private final DemoArray<T> arr;
    private boolean done;
    
    private boolean swapped;
    private int i; // i is the index currently being inspected
    private int j; // j points to the position the next largest item will move
                   // into
    
    /**
     * Prepare to bubble sort a DemoArray
     * @param arr the array to sort
     */
    public BubbleSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.done = arr.length() <= 1;
        
        // Initialize sort variables
        this.swapped = false;
        this.i = 0;
        this.j = arr.length() - 2; 
    }
    
    /**
     * Perform the next step in the sort
     */
    @Override
    public void step() {        
        if (done) {
            return;
        }
        
        // Compare the current element to the next element
        if (arr.compare(i, i + 1) > 0) {
            arr.swap(i, i + 1); // if the current element is larger, bubble it up
            swapped = true;     // and remember that something was moved
        }
                
        // Loop until the largest unsorted element has moved into place
        if (i < j) {
            i++;
        }
        else {
            // Once the largest has reached its home,
            done = (swapped == false); // we are done if nothing was moved
            swapped = false;  // reset the swapped flag
            i = 0;            // move back to the beginning of the array
            j--;              // the home of the next largest item
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
    
}
