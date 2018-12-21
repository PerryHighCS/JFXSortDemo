package run.mycode.sortdemo.sort;

import java.util.logging.Level;
import java.util.logging.Logger;
import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a quick sort on a DemoArray
 *
 * @param <T> The type of item to sort
 *
 * @author dahlem.brian
 */
public class QuickSorter<T extends Comparable<T>> 
        extends SteppableSorter<T> {
    
    public static final String NAME = "Quick Sort";

    public QuickSorter(DemoArray<T> arr) {
        super(arr, NAME); 
    }

    @Override
    protected void sort() {
        try {
            quickSort(0, arr.length() - 1);
        } catch (InterruptedException ex) {
        }
        
        done = true;
    }
    
    private synchronized void quickSort(int beg, int end) throws InterruptedException {
        int i = beg;
        int j = end;
        
        this.wait();
        T pivot = arr.get((i + j) / 2);
        
        while (i <= j) {
            this.wait();
            while (arr.compare(i, pivot) < 0) {
                i++;
                this.wait();
            }
            
            this.wait();
            while (arr.compare(j, pivot) > 0) {
                j--;
                this.wait();
            }
            
            if (i <= j) {
                this.wait();
                arr.swap(i, j);
                i++;
                j--;   
            }
        }
        
        if (beg < j) {
            quickSort(beg, j);
        }
        if (i < end) {
            quickSort(i, end);
        }
    }
}
