package run.mycode.sortdemo.sort;

import java.util.ArrayList;
import java.util.List;
import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a quick sort on a DemoArray
 *
 * @param <T> The type of item to sort
 *
 * @author dahlem.brian
 */
public class QuickSorter<T extends Comparable<T>> implements SteppableSorter<T> {
    
    public static final String NAME = "Quick Sort";
    
    private final DemoArray<T> arr;
    private final List<QuickStackFrame> stack; // an operating stack to keep
                                               // track of the current sort 
                                               // state
    
    private boolean done;

    public QuickSorter(DemoArray<T> arr) {
        this.arr = arr;
        
        // 0 or 1 element is already sorted
        this.done = arr.length() <= 1;
        
        // Prepare the operating stack with a frame to sort the whole array
        this.stack = new ArrayList<>();
        stack.add(new QuickStackFrame(0, arr.length() - 1));
    }
    
    /**
     * Perform the next step in the sort
     */
    @Override
    public void step() {
        if (done) {
            return;
        }
        
        // Get the current stack frame
        QuickStackFrame state = stack.get(stack.size() - 1);
        
        // Determine the current operation
        switch (state.state) {
            case PARTITIONING:
                // The current segment of the array is being partitioned
                
                // if there is data to partition
                if (state.i < state.j) {
                    // Check the low half in the next step
                    state.state = State.CHECK_LOW;
                }
                else {
                    // if not, move to sorting the two halves
                    state.state = State.SPLITTING;
                }
                break;
                
            case CHECK_LOW:
                // Search the left side of the segment for the next element
                // that needs to move after the pivot
                if (arr.compare(state.i, state.pivot) < 0) {
                    state.i++;
                }
                else {
                    // Once an element has been found, shift to the right side
                    state.state = State.CHECK_HIGH;
                }
                break;
                
            case CHECK_HIGH:
                // Search the rightt side of the segment for the next element
                // that needs to move before the pivot
                if (arr.compare(state.j, state.pivot) > 0) {
                    state.j--;
                }
                else {
                    // Once an element has been found, swap it with the element
                    // from the left side so that both are now in the correct
                    // halves relative to the pivot
                    if (state.i < state.j) {
                        arr.swap(state.i, state.j);
                    }
                    
                    // If there is still data to be checked, continue the
                    // partition operation
                    if (state.i <= state.j) {
                        state.i++;
                        state.j--;
                    }
                    state.state = State.PARTITIONING;
                }
                break;
                
            case SPLITTING:
                // The pivot element is now correctly placed.
                stack.remove(stack.size() - 1); // Remove this frame since it is
                                                // complete
                
                // If there are elements on the right side of the array segment
                // that are greater value than the pivot, sort them
                if (state.i < state.end) {
                    stack.add(new QuickStackFrame(state.i, state.end));
                }
                
                // if there are elements on the left side less than the pivot
                // value, sort them
                if (state.beg < state.j) {
                    stack.add(new QuickStackFrame(state.beg, state.j));
                }
                break;
        }
        
        // Once the stack is empty, there is no more to do.
        if (stack.isEmpty()) {
            done = true;
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
    
    /**
     * An stack object that can keep track of operational state in a quick sort
     */
    private class QuickStackFrame {
        int beg;
        int end;
        int i;
        int j;
        T pivot;
        
        State state;
        
        QuickStackFrame(int beg, int end) {
            this.beg = beg;
            this.end = end;
            this.i = this.beg;
            this.j = this.end;
            
            int p = (this.beg + this.end) / 2;
            this.pivot = arr.get(p);
            this.state = State.PARTITIONING;
        }        
    }
    
    /**
     * The possible operating states of a quick sort
     */
    public static enum State {
        SPLITTING, PARTITIONING, CHECK_LOW, CHECK_HIGH
    }
}
