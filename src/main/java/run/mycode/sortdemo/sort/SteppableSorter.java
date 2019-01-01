package run.mycode.sortdemo.sort;

import java.util.concurrent.Semaphore;
import run.mycode.sortdemo.util.DemoArray;

/**
 * An interface for a sorting algorithm that can be run by repeatedly calling
 * the step method until the data isSorted.
 *
 * @param <T> the type to sort
 *
 * @author bdahl
 */
public abstract class SteppableSorter<T extends Comparable<T>> {   
    protected volatile boolean done;
    protected volatile boolean started;
    protected Semaphore step;
    
    protected final DemoArray<T> arr;
    
    protected Thread sorter;
    
    /**
     * Prepare the sorter to sort the array and spawn a new thread to run the
     * sorting
     * 
     * @param arr       The array to be sorted
     * @param sortName  The name of the sort to tag the thread with
     */
    public SteppableSorter(DemoArray<T> arr, String sortName) {
        this.arr = arr;
        
        this.step = new Semaphore(0);
        
        // 0 or 1 element is already sorted
        this.done = arr.length() <= 1;
        if (done) {
            sorter = null;
            started = false;
        } else {
            started = false;
            sorter = new Thread(() -> sort(), sortName);
        }
    }
    
    /**
     * Determine if the sorting algorithm uses a scratch array
     * 
     * @return true if the sorting algorithm requires a scratch array
     */
    public boolean usesScratchArray() {
        return false;
    }
    
    /**
     * Retrieve a reference to the scratch array if the algorithm uses one
     * 
     * @return the scratch array, null if none is used
     */
    public DemoArray<T> getScratchArray() {
        return null;
    }
    
    /**
     * Perform the next step of the sort
     */
    public void step() {
        if (done || sorter == null) {
            return;
        }

        if (!sorter.isAlive() && !started) {
            started = true;
            sorter.start();
        }
        
        step.release(); // Execute the next step
    }

    /**
     * Check if sorting is complete
     *
     * @return true if the sorting algorithm has completed
     */
    public boolean isSorted() {
        return done;
    }
    
    protected abstract void sort();
}
