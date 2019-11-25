package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a bubble sort on a DemoArray
 *
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class AwfulSort<T extends Comparable<T>>
        extends SteppableSorter<T> {

    public static final String NAME = "Awful Sort";

    /**
     * Prepare to bubble sort a DemoArray
     *
     * @param arr the array to sort
     */
    public AwfulSort(DemoArray<T> arr) {
        super(arr, NAME);
    }

    @Override
    protected void sort() throws InterruptedException {
        boolean swapped;

        do {
            swapped = false;
            for (int i = 1; i < arr.length(); i++) {

                step.acquire();  // Pause for the next step

                if (arr.compare(i, i - 1) < 0) {
                    step.acquire();  // Pause for the next step
                    arr.swap(i, i - 1);
                    i = 0;
                    swapped = true;
                }                
            }
        } while (swapped);

        done = true;
    }
}
