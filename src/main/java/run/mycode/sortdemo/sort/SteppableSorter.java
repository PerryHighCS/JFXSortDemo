package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * An interface for a sorting algorithm that can be run by repeatedly calling
 * the step method until the data isSorted.
 *
 * @param <T> the type to sort
 *
 * @author bdahl
 */
public interface SteppableSorter<T extends Comparable<T>> {    
    /**
     * Determine if the sorting algorithm uses a scratch array
     * 
     * @return true if the sorting algorithm requires a scratch array
     */
    default public boolean usesScratchArray() {
        return false;
    }
    
    /**
     * Retrieve a reference to the scratch array if the algorithm uses one
     * 
     * @return the scratch array, null if none is used
     */
    default public DemoArray<T> getScratchArray() {
        return null;
    }
    
    /**
     * Perform the next step of the sort
     */
    public void step();

    /**
     * Check if sorting is complete
     *
     * @return true if the sorting algorithm has completed
     */
    public boolean isSorted();
}
