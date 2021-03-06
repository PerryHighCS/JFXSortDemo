package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a bubble sort on a DemoArray
 *
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class BubbleSorter<T extends Comparable<T>>
        extends SteppableSorter<T> {

    public static final String NAME = "Bubble Sort";

    /**
     * Prepare to bubble sort a DemoArray
     *
     * @param arr the array to sort
     */
    public BubbleSorter(DemoArray<T> arr) {
        super(arr, NAME);
    }

    @Override
    protected void sort() throws InterruptedException {
        boolean swapped;
        int j = arr.length() - 1;

        do {
            swapped = false;
            for (int i = 0; i < j; i++) {

                step.acquire();  // Pause for the next step

                if (arr.compare(i, i + 1) > 0) {
                    swapped = true;
                    step.acquire();  // Pause for the next step
                    arr.swap(i, i + 1);
                }
            }

            j--; // Every pass the biggest moves to the end, so don't go that far
        } while (swapped);

        done = true;
    }
}
