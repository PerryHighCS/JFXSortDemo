package run.mycode.sortdemo.sort;

import java.util.ArrayList;
import java.util.List;
import run.mycode.sortdemo.util.DemoArray;

/**
 * Perform a merge sort on a DemoArray
 *
 * @param <T> The type of item to sort
 *
 * @author dahlem.brian
 */
public class MergeSorter<T extends Comparable<T>> implements SteppableSorter<T> {
    
    public static final String NAME = "Merge Sort";

    private final DemoArray<T> arr;
    private final DemoArray<T> tmp; // a scratch array to work in
    private final List<MergeStackFrame> stack; // an operating stack to keep
    // track of the current sort 
    // state
    private boolean done;

    /**
     * Prepare to insertion sort a DemoArray
     *
     * @param arr the array to sort
     */
    public MergeSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.tmp = new DemoArray<>(arr.length());

        if (tmp.length() < arr.length()) {
            throw new IllegalArgumentException("tmp array must be same size as array to sort.");
        }

        // 0 or 1 element is already sorted
        this.done = (arr.length() <= 1);

        // Prepare the operating stack with a frame to sort the whole array
        this.stack = new ArrayList<>();
        int mid = arr.length() / 2;
        stack.add(new MergeStackFrame(0, mid, arr.length() - 1));
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
        MergeStackFrame state = stack.get(stack.size() - 1);

        // Determine the current operation
        switch (state.state) {
            case SPLITTING:
                // The array is being split then each half will be sorted

                // If there are more than one element in this array section 
                if (state.beg < state.end) {
                    // Find the middle element to divide the section to be sorted
                    int mid = (state.beg + state.end) / 2;

                    // Find the middle of the second half of the section
                    int m = (mid + state.end) / 2;
                    // Add a stack frame to sort the second half
                    stack.add(new MergeStackFrame(state.mid + 1, m, state.end));

                    // Find the middle of the first half of the section
                    m = (state.beg + mid) / 2;
                    // Add a stack frame to sort the first half
                    stack.add(new MergeStackFrame(state.beg, m, state.mid));

                    // Once those operations are complete, the two halves are
                    // sorted, so they need to be merged... after copying them
                    // to the scratch array
                    state.state = MergeStackFrame.State.COPYDOWN_FIRST;
                    state.i = state.beg; // point the i index to the beginning 
                    //of the array section
                } else {
                    // If there is 1 or 0 elements in this section, it is 
                    // already sorted, so nothing else needs to happen
                    stack.remove(stack.size() - 1);
                }
                break;
            case COPYDOWN_FIRST:
                // Moving the first half to the scratch array

                // If there are elements still to move to the scratch array
                if (state.i <= state.mid) {
                    // Move the element at i to the scratch array
                    tmp.set(state.i, arr.remove(state.i));
                    // then move to the next element for the next step
                    state.i++;
                } else {
                    // once all elements in the half are moved to scratch,
                    // do the same with the second half
                    state.state = MergeStackFrame.State.COPYDOWN_SECOND;
                }
                break;
            case COPYDOWN_SECOND:
                // Moving the second half to the scratch array

                // If there are elements to move
                if (state.i <= state.end) {
                    // Find the location to move to... elements in the second
                    // half are moved into reverse order in the scratch array
                    int j = state.end + state.mid + 1 - state.i;
                    tmp.set(j, arr.remove(state.i));

                    // then move to the next element for the next step
                    state.i++;
                } else {
                    // Once all elements have been moved, prepare to merge
                    // the two sorted halves into a sorted whole
                    state.state = MergeStackFrame.State.MERGING;
                    state.i = state.beg; // the smallest item in the first half
                    state.j = state.end; // the smallest item in the second half
                    state.k = state.beg; // the next location in the array
                }
                break;
            case MERGING:
                // The two sorted halves have been moved to the scratch array
                // so copy the elements back to the array in sorted order

                // If the item at i is < the item at j
                if (tmp.compare(state.i, state.j) < 0) {
                    // copy the element at i to the next location in the array
                    arr.set(state.k, tmp.remove(state.i));
                    state.i++;
                } else {
                    // otherwise copy the element at j to the next spot in the array
                    arr.set(state.k, tmp.remove(state.j));
                    state.j--;
                }

                // move the insertion point to the next location in the array
                state.k++;
                // if the insertion point has moved past the end of this section
                if (state.k > state.end) {
                    // remove this operation from the stack... it is complete
                    stack.remove(stack.size() - 1);
                }
                break;
        }

        // If there is nothing else in the operation stack, the sort is complete!
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
        return true;
    }

    @Override
    public DemoArray<T> getScratchArray() {
        return tmp;
    }
    /**
     * An stack object that can keep track of operational state in a merge sort
     */
    private static class MergeStackFrame {

        int beg;
        int mid;
        int end;
        int i = 0;
        int j = 0;
        int k = 0;

        State state;

        MergeStackFrame(int beg, int mid, int end) {
            this.beg = beg;
            this.mid = mid;
            this.end = end;
            this.state = State.SPLITTING;
        }

        /**
         * The possible operating states of the merge sort
         */
        static enum State {
            SPLITTING, COPYDOWN_FIRST, COPYDOWN_SECOND, MERGING
        };
    }
}
