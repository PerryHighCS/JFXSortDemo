package run.mycode.sortdemo.sort;

import java.util.concurrent.Semaphore;
import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a merge sort on a DemoArray
 *
 * @param <T> The type of item to sort
 *
 * @author dahlem.brian
 */
public class MergeSorter<T extends Comparable<T>> implements SteppableSorter<T> {
    
    public static final String NAME = "Merge Sort2";

    private final DemoArray<T> arr;
    private final DemoArray<T> tmp; // a scratch array to work in
    
    private boolean started;
    private final Semaphore stepSem;
    private final Thread sorter;

    /**
     * Prepare to insertion sort a DemoArray
     *
     * @param arr the array to sort
     */
    public MergeSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.tmp = new DemoArray<>(arr.length());

        this.stepSem = new Semaphore(0);
        
        // 0 or 1 element is already sorted
        if (arr.length() <= 1) {
            sorter = null;
            started = false;
        }
        else {
            started = false;
            sorter = new Thread(() -> {
                mergeSort(0, arr.length() - 1);
            }, "Merge Sort");
        }
    }

    /**
     * Perform the next step in the sort
     */
    @Override
    public void step() {
        if (sorter == null) {
            return;
        }
        
        if (!sorter.isAlive() && !started) {
            started = true;
            sorter.start();
        }
        
        stepSem.release();
    }
    
    /**
     * Check if the sort operation is complete
     *
     * @return true if the sort has completed
     */
    @Override
    public boolean isSorted() {
        if (sorter == null) {
            return true;
        }
        else {        
            return started && !sorter.isAlive();
        }
    }

    @Override
    public boolean usesScratchArray() {
        return true;
    }

    @Override
    public DemoArray<T> getScratchArray() {
        return tmp;
    }
    
    private void mergeSort(int beg, int end) {
        if (beg >= end) {
            return;
        }
        
        int m = (beg + end) / 2;
        
        mergeSort(beg, m);
        mergeSort(m + 1, end);
        
        try {
            for (int i = beg; i <= m; i++) {
                stepSem.acquire();
                tmp.set(i, arr.get(i));
            }
            
            for (int i = m+1; i <= end; i++) {
                int j = end + m + 1 - i;
                stepSem.acquire();
                tmp.set(j, arr.get(i));
            }
            
            int i = beg;
            int j = end;
            
            for (int k = beg; k <= end; k++) {
                stepSem.acquire();
                if (tmp.compare(i, j) < 0) {
                    arr.set(k, tmp.remove(i));
                    i++;
                }
                else {
                    arr.set(k, tmp.remove(j));
                    j--;
                }
            }
        } catch (InterruptedException ex) {    
            System.err.println("Merge Sort INTERRUPTED!");
        }
    }
}
