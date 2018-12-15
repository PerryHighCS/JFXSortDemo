package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.DemoArray;

/**
 *
 * @author dahlem.brian
 * 
 * @param <T> The type of item to sort
 */
public class InsertionSorter<T extends Comparable<T>> implements SteppableSorter {
    private final DemoArray<T> arr;

    private boolean done;
    private int i;
    private int j;
    private T tmp;
    
    public InsertionSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.done = (arr.length() <= 1);
        this.i = 1;
        this.j = 0;
        if (!done) {
            this.tmp = arr.get(i);
        }
        else {
            this.tmp = null;
        }
    }
    
    @Override
    public void step() {
        if (done) {
            return;
        }
                
        if (j < 0 || arr.compare(j, tmp) <= 0) {
            arr.set(j + 1, tmp);
            i++;
            if (i >= arr.length()) {
                done = true;
                return;
            }
            j = i - 1;
            tmp = arr.get(i);
        }
        else {
            arr.swap(j, j + 1);
            j--;
        }
        
    }

    @Override
    public boolean isSorted() {
        return done;
    }
    
}
