package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform an insertion sort on a DemoArray
 *
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class InsertionSorter<T extends Comparable<T>>
        extends SteppableSorter<T> {

    public static final String NAME = "Insertion Sort";    

    /**
     * Prepare to insertion sort a DemoArray
     *
     * @param arr the array to sort
     */
    public InsertionSorter(DemoArray<T> arr) {
        super(arr, NAME);    
    }

    @Override
    protected void sort() throws InterruptedException {
        for (int i = 1; i < arr.length(); i++) {

            step.acquire();  // Pause for the next step
            T item = arr.remove(i);

            int j = i - 1;

            step.acquire();  // Pause for the next step
            while (j >= 0 && arr.compare(j, item) > 0) {
                step.acquire();  // Pause for the next step
                arr.move(j, j + 1);
                j--;
                step.acquire();  // Pause for the next step
            }

            step.acquire();  // Pause for the next step
            arr.set(j + 1, item);
        }

        done = true;
    }
}
