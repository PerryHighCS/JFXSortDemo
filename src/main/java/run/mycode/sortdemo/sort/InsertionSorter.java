package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.DemoArray;

/**
 * Perform an insertion sort on a DemoArray
 *
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class InsertionSorter<T extends Comparable<T>>
        implements SteppableSorter {

    private final DemoArray<T> arr;

    private boolean done;
    private int i; // i is the index of the next item to find a home for
    private int j; // the index of the next position to check if it is the home
    private T tmp; // tmp is the item that needs a home

    /**
     * Prepare to insertion sort a DemoArray
     *
     * @param arr the array to sort
     */
    public InsertionSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.done = (arr.length() <= 1);

        // prepare the sort pointers and data
        this.i = 1; 
        this.j = 0; 
        if (!done) {
            this.tmp = arr.remove(i);
        } else {
            this.tmp = null;
        }
    }

    /**
     * Perform the next step in the sort
     */
    @Override
    public void step() {
        if (done) {
            return;
        }

        // If the home pointer hasn't reached the beginning of the array,
        // and we are still looking for a home for the item
        if (j >= 0 && arr.compare(j, tmp) > 0) {
            arr.move(j, j + 1); // Move the item at j to make room
            j--;                // prepare to check the next location
        } else {
            // move the item to the last position checked
            arr.set(j + 1, tmp);
            
            i++; // move to the next item in the array
            if (i >= arr.length()) { // if there are no more items,
                done = true;         // the sort is complete
                return;
            }
            j = i - 1;         // the next possible home is one before the item
            tmp = arr.remove(i);  // copy out the item looking for a home
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
