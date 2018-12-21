package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a merge sort on a DemoArray
 *
 * @param <T> The type of item to sort
 *
 * @author dahlem.brian
 */
public class MergeSorter<T extends Comparable<T>> extends SteppableSorter<T> {

    public static final String NAME = "Merge Sort";
    
    private final DemoArray<T> tmp; // a scratch array to work in

    /**
     * Prepare to insertion sort a DemoArray
     *
     * @param arr the array to sort
     */
    public MergeSorter(DemoArray<T> arr) {
        super(arr, NAME); 
        
        this.tmp = new DemoArray<>(arr.length());
    }

    @Override
    public boolean usesScratchArray() {
        return true;
    }

    @Override
    public DemoArray<T> getScratchArray() {
        return tmp;
    }

    @Override
    protected void sort() {
        try {
            mergeSort(0, arr.length() - 1);
        } catch (InterruptedException ex) {
        }
        done = true;
    }

    private void mergeSort(int beg, int end) throws InterruptedException {
        if (beg >= end) {
            return;
        }

        int m = (beg + end) / 2;

        mergeSort(beg, m);
        mergeSort(m + 1, end);

        for (int i = beg; i <= m; i++) {
            step.acquire();     // Pause for the next step
            tmp.set(i, arr.get(i));
        }

        for (int i = m + 1; i <= end; i++) {
            int j = end + m + 1 - i;
            step.acquire();     // Pause for the next step
            tmp.set(j, arr.get(i));
        }

        int i = beg;
        int j = end;

        for (int k = beg; k <= end; k++) {
            step.acquire();  // Pause for the next step
            if (tmp.compare(i, j) < 0) {
                step.acquire();  // Pause for the next step
                arr.set(k, tmp.remove(i));
                i++;
            } else {
                step.acquire();  // Pause for the next step
                arr.set(k, tmp.remove(j));
                j--;
            }
        }
    }
}
