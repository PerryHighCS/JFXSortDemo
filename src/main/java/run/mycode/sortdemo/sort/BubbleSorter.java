package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.DemoArray;

/**
 *
 * @author bdahl
 * @param <T>
 */
public class BubbleSorter<T extends Comparable<T>> implements SteppableSorter {
    private final DemoArray<T> arr;
    private boolean swapped;
    private int i;
    private int j;
    private boolean done;
    
    public BubbleSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.swapped = false;
        this.i = 0;
        this.j = arr.length() - 2;
        this.done = arr.length() <= 1;
    }
    
    @Override
    public void step() {
        if (done) {
            return;
        }
        
        if (arr.compare(i, i + 1) > 0) {
            arr.swap(i, i + 1);
            swapped = true;
        }
                
        if (i < j) {
            i++;
        }
        else {
            done = (swapped == false);
            swapped = false;
            i = 0;
            j--;
        }
    }

    @Override
    public boolean isSorted() {
        return done;
    }
    
}
