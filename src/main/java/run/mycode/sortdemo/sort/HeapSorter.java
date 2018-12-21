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
    protected synchronized void sort() {
        try {
            int n = arr.length();
            
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(n, i);
            }
            
            for (int i=n-1; i >= 0; i--) {
                this.wait();
                arr.swap(0, i);
                
                heapify(i, 0);
            }
        } catch (InterruptedException ex) {

        }
        done = true;
    }
    
    private synchronized void heapify(int len, int root) 
            throws InterruptedException {
        int largest = root;
        int l = 2*root + 1; // left child
        int r = 2*root + 2; // right child
        
        // If the left child is > root
        this.wait();
        if (l < len && arr.compare(l, largest) > 0) {
            largest = l;
        }
        
        // If the right child is the greatest of the three
        this.wait();
        if (r < len && arr.compare(r, largest) > 0) {
            largest = r;
        }
        
        // If the largest isn't the root
        if (largest != root) {
            this.wait();
            arr.swap(root, largest); // move the largest up
            
            heapify(len, largest); // And heapify the child that was changed
        }
    }
}
