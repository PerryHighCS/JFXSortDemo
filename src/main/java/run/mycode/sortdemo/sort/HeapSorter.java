package run.mycode.sortdemo.sort;

import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a heap sort on a DemoArray
 *
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class HeapSorter<T extends Comparable<T>> extends SteppableSorter<T> {

    public static final String NAME = "Heap Sort";

    /**
     * Prepare to heap sort a DemoArray
     *
     * @param arr the array to sort
     */
    public HeapSorter(DemoArray<T> arr) {
        super(arr, NAME);
    }

    @Override
    protected void sort() {
        try {
            int n = arr.length();
            
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(n, i);
            }
            
            for (int i=n-1; i >= 0; i--) {
                step.acquire();  // Pause for the next step
                arr.swap(0, i);
                
                heapify(i, 0);
            }
        } catch (InterruptedException ex) {

        }
        done = true;
    }
    
    private void heapify(int len, int root) 
            throws InterruptedException {
        int largest = root;
        int l = 2*root + 1; // left child
        int r = 2*root + 2; // right child
        
        // If the left child is > root
        step.acquire();  // Pause for the next step
        if (l < len && arr.compare(l, largest) > 0) {
            largest = l;
        }
        
        // If the right child is the greatest of the three
        step.acquire();  // Pause for the next step
        if (r < len && arr.compare(r, largest) > 0) {
            largest = r;
        }
        
        // If the largest isn't the root
        if (largest != root) {
            step.acquire();  // Pause for the next step
            arr.swap(root, largest); // move the largest up
            
            heapify(len, largest); // And heapify the child that was changed
        }
    }
}
