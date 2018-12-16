package run.mycode.sortdemo.sort;

import java.util.ArrayList;
import java.util.List;
import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a heap sort on a DemoArray
 *
 * @param <T> The type of data to sort
 *
 * @author bdahl
 */
public class HeapSorter<T extends Comparable<T>> implements SteppableSorter<T> {
    
    public static final String NAME = "Heap Sort";

    private final DemoArray<T> arr;
    private final List<HeapStackFrame> stack; // an operating stack to keep
                                              // track of the current sort 
                                              // state
    private boolean done;

    /**
     * Prepare to heap sort a DemoArray
     *
     * @param arr the array to sort
     */
    public HeapSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.done = arr.length() <= 1;

        // Initialize sort variables
        stack = new ArrayList<>();
        stack.add(HeapStackFrame.building(arr.length()));
    }

    /**
     * Perform the next step in the sort
     */
    @Override
    public void step() {
        if (done) {
            return;
        }

        HeapStackFrame state = stack.get(stack.size() - 1);
        switch (state.state) {
            case BUILD_HEAP:
                // Build a heap where the root node is greater than its children
                
                if (state.i >= 0) {
                    stack.add(HeapStackFrame.heapify(state.i, arr.length()));
                    state.i--;
                }
                
                if (state.i < 0) {
                    state.state = HeapStackFrame.State.EXTRACT_ELEMENT;
                    state.i = arr.length() - 1;
                }
                break;
                
            case EXTRACT_ELEMENT:
                // Extract the largest (root) element from the heap
                
                // If there are elements to extract
                if (state.i >= 0) {
                    // move the current root to the end of the array, it is
                    // the largest
                    arr.swap(0, state.i);
                    
                    // reduce the heap now that element is removed
                    stack.add(HeapStackFrame.heapify(0, state.i));
                    
                    state.i--;
                }
                
                // If there are no other elements to remove, we are done
                if (state.i < 0) {
                    stack.remove(stack.size() - 1);
                    done = true;
                }
                break;
            
            case HEAP_CHECK_L:
                // Check the left child of the root to see if it is larger than
                // the root                
                int l = (2 * state.root) + 1; // the left child index
                
                if (l < state.size && arr.compare(l, state.largest) > 0) {
                    state.largest = l;
                }
                
                // Check the right child next
                state.state = HeapStackFrame.State.HEAP_CHECK_R;
                break;
                
            case HEAP_CHECK_R:
                // Check right child of the root to see if it is largest of the
                // three                
                int r = (2 * state.root) + 2; // the right child index
                if (r < state.size && arr.compare(r, state.largest) > 0) {
                    state.largest = r;
                }
                
                // rearrange the three so the root is the largest
                state.state = HeapStackFrame.State.HEAPIFY;
                break;
                
            case HEAPIFY:
                // Rearrange the three nodes so the largest is at the root,
                // then ensure the heap is still valid
                
                // this stack frame is done, remove it before possibly adding a
                // new one
                stack.remove(stack.size() - 1); 
                               
                // If the root is not the largest element
                if (state.largest != state.root) {
                    arr.swap(state.root, state.largest);
                    
                    // and heapify the affected sub-tree
                    stack.add(HeapStackFrame.heapify(state.largest, state.size));
                }
                break;            
        }
    }

    /**
     * Check if the sort operation is complete
     *
     * @return true if the sort has completed
     */
    @Override
    public boolean isSorted() {
        return done;
    }
    
    @Override
    public boolean usesScratchArray() {
        return false;
    }

    @Override
    public DemoArray<T> getScratchArray() {
        return null;
    }
}

/**
 * An stack object that can keep track of operational state in a quick sort
 */
class HeapStackFrame {
    int root;
    int i;
    int size;
    int largest;

    State state;

    public static HeapStackFrame building(int size) {
        HeapStackFrame frame = new HeapStackFrame();

        frame.state = State.BUILD_HEAP;
        frame.i = (size / 2) - 1;

        return frame;
    }

    public static <T extends Comparable<T>> HeapStackFrame heapify(int root, int size) {
        HeapStackFrame frame = new HeapStackFrame();

        frame.state = State.HEAP_CHECK_L;
        frame.root = root;
        frame.size = size;
        frame.largest = root;

        return frame;
    }

    /**
     * The possible operating states of a quick sort
     */
    public static enum State {
        BUILD_HEAP, EXTRACT_ELEMENT, HEAP_CHECK_L, HEAP_CHECK_R, HEAPIFY
    }
}
