package run.mycode.sortdemo.sort;

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
    protected void sort() throws InterruptedException {
        quickSort(0, arr.length() - 1);
        
        done = true;
    }
    
    private void quickSort(int beg, int end) throws InterruptedException {
        int i = beg;
        int j = end;
        
        step.acquire();  // Pause for the next step
        T pivot = arr.get((i + j) / 2);
        
        while (i <= j) {
            step.acquire();  // Pause for the next step
            while (arr.compare(i, pivot) < 0) {
                i++;
                step.acquire();  // Pause for the next step
            }
            
            step.acquire();  // Pause for the next step
            while (arr.compare(j, pivot) > 0) {
                j--;
                step.acquire();  // Pause for the next step
            }
            
            if (i <= j) {
                step.acquire();  // Pause for the next step
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
