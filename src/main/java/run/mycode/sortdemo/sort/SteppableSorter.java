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
    /**
     * True once the sort operation is complete and the DemoArray arr is 
     * believed to be sorted.
     */
    protected volatile boolean done;
    
    /**
     * True once the sort begins.
     */
    protected volatile boolean started;
    
    /**
     * The semaphore that controls the speed of the sort. Acquiring the 
     * semaphore will pause until step() is called.
     */
    protected Semaphore step;

    /**
     * The array to sort in implementation of sort()
     */
    protected final DemoArray<T> arr;

    
    /**
     * The thread that is executing the sort() method
     */
    protected Thread sorter;

    /**
     * Prepare the sorter to sort the array and spawn a new thread to run the
     * sorting
     *
     * @param arr The array to be sorted
     * @param sortName The name of the sort to tag the thread with and display
     *                 in the UI
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

            sorter = new Thread(() -> {
                try {
                    sort();
                } catch (InterruptedException ignored) {
                    /*
                        If the thread is interrupted, just quit the sort.
                     */
                }
            }, sortName);
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
    public final void step() {
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

    /**
     * Check if the sorting thread has been interrupted and cannot continue
     * 
     * @return true if the thread was interrupted
     */
    public boolean isInterrupted() {
        return sorter.isInterrupted();
    }
    
    /**
     * Interrupt the sorting thread
     */
    public void interrupt() {
        sorter.interrupt();
    }
    
    /**
     * Perform the sorting operation on arr. This method will be called in a new 
     * thread, to sort the DemoArray arr.
     * 
     * @throws InterruptedException if the sorting thread is interrupted while
     *                              waiting for the semaphore. Note: the sort
     *                              status on this exception is undefined. It
     *                              may be possible to restart the sort 
     *                              depending on sort implementation
     */
    protected abstract void sort() throws InterruptedException;
}
