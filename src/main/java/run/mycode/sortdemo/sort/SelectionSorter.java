package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a selection sort on a DemoArray
 * 
 * NOTE: implementors should also include a private static final String NAME 
 * that includes a readable name for the sorting algorithm, or the classname 
 * will be used
 * 
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class SelectionSorter<T extends Comparable<T>>
        extends SteppableSorter<T> {
    
    public static final String NAME = "Selection Sort";
    
    /**
     * Prepare to selection sort a DemoArray
     * @param arr the array to sort
     */
    public SelectionSorter(DemoArray<T> arr) {
        super(arr, NAME); 
    }
    
    @Override
    protected synchronized void sort() {
        try {
            for (int i = 0; i < arr.length() - 1; i++) {
                int min = i;
                for (int j = i + 1; j < arr.length(); j++) {
                    this.wait();
                    if (arr.compare(j, min) < 0) {
                        min = j;
                    }
                }
                
                if (i != min) {
                    this.wait();
                    arr.swap(min, i);
                }
            }
        }
        catch (InterruptedException ex) {
        }
        
        done = true;
    }
}
