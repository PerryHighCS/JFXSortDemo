package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.DemoArray;

/**
 *
 * @author bdahl
 * @param <T>
 */
public class SelectionSorter<T extends Comparable<T>> implements SteppableSorter {
    private final DemoArray<T> arr;
    
    private boolean done;
    private int i;
    private int j;
    private int min;
    
    public SelectionSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.i = 0;
        this.j = i+1;
        this.min = 0;      
        this.done = false;
    }
    
    @Override
    public void step() {
        if (done == true) {
            return;
        }
        
        if (arr.compare(min, j) > 0) {
            min = j;
        } 
        
        j++;
        
        if (j >= arr.length()) {
            arr.swap(i, min);
            
            i++;
            j = i + 1;
            min = i;
        }
        
        if (i >= arr.length() - 1) {
            done = true;
        }        
    }

    @Override
    public boolean isSorted() {
        return done;
    }
    
}
