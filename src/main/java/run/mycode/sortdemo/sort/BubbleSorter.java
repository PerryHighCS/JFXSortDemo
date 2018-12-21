package run.mycode.sortdemo.sort;

import java.util.concurrent.Semaphore;
import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a bubble sort on a DemoArray
 *
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class BubbleSorter<T extends Comparable<T>> implements SteppableSorter<T> {

    public static final String NAME = "Bubble Sort";

    private final DemoArray<T> arr;

    private boolean started;
    private boolean done;

    private final Semaphore stepSem;
    private final Thread sorter;

    /**
     * Prepare to bubble sort a DemoArray
     *
     * @param arr the array to sort
     */
    public BubbleSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.done = arr.length() <= 1;

        this.stepSem = new Semaphore(0);

        // 0 or 1 element is already sorted
        if (done) {
            sorter = null;
            started = false;
        } else {
            started = false;
            sorter = new Thread(() -> sort(), "Bubble Sort");
        }
    }

    /**
     * Perform the next step in the sort
     */
    @Override
    public void step() {
        if (done || sorter == null) {
            return;
        }

        if (!sorter.isAlive() && !started) {
            started = true;
            sorter.start();
        }

        stepSem.release();
    }

    private void sort() {

        boolean swapped;
        int j = arr.length() - 1;

        try {
            do {
                swapped = false;
                for (int i = 0; i < j; i++) {
                    stepSem.acquire();  // Pause for the next step
                    
                    if (arr.compare(i, i + 1) > 0) {
                        swapped = true;
                        stepSem.acquire();  // Pause for the next step
                        arr.swap(i, i + 1);
                    }
                }

                j--; // Every pass the biggest moves to the end, so don't go that far
            } while (swapped);
        } catch (InterruptedException ex) {
            System.out.println("Bubble Sort interrupted");
        }

        done = true;
    }

    @Override
    public boolean isSorted() {
        return done;
    }
}
