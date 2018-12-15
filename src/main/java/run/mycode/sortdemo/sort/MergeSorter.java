/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run.mycode.sortdemo.sort;

import java.util.ArrayList;
import java.util.List;
import run.mycode.sortdemo.DemoArray;

/**
 *
 * @author dahlem.brian
 * 
 * @param <T> The type of item to sort
 */
public class MergeSorter<T extends Comparable<T>> implements SteppableSorter {
    private final DemoArray<T> arr;
    private final DemoArray<T> tmp;
    private final List<MergeStackFrame> stack;
    
    private boolean done;

    public MergeSorter(DemoArray<T> arr, DemoArray<T> tmp) {
        this.arr = arr;
        this.tmp = tmp;
        
        this.done = (arr.length() <= 1);
        this.stack = new ArrayList<>();
        
        int mid = arr.length() / 2;
        stack.add(new MergeStackFrame(0, mid, arr.length() - 1));
    }
    
    @Override
    public void step() {
        if (done) {
            return;
        }
        
        MergeStackFrame state = stack.get(stack.size() - 1);
        
        switch (state.state) {
            case SPLITTING:
                if (state.beg < state.end) {                
                    int mid = (state.beg + state.end) / 2;
                    int m = (mid + state.end) / 2;
                    stack.add(new MergeStackFrame(state.mid + 1, m, state.end));
                    m = (state.beg + mid) / 2;
                    stack.add(new MergeStackFrame(state.beg, m, state.mid));
                    state.state = MergeStackFrame.State.COPYDOWN_FIRST;
                    state.i = state.beg;
                }
                else {
                    stack.remove(stack.size() - 1);
                }
                break;            
            case COPYDOWN_FIRST:
                if (state.i <= state.mid) {
                    tmp.set(state.i, arr.get(state.i));
                    state.i++;
                }
                else {
                    state.state = MergeStackFrame.State.COPYDOWN_SECOND;
                }
                break;
            case COPYDOWN_SECOND:
                if (state.i <= state.end) {
                    int j = state.end + state.mid + 1 - state.i;
                    tmp.set(j, arr.get(state.i));
                    state.i++;
                }
                else {
                    state.state = MergeStackFrame.State.MERGING;
                    state.i = state.beg;
                    state.j = state.end;
                    state.k = state.beg;
                } 
                break;
            case MERGING:
                if (tmp.compare(state.i, state.j) < 0) {
                    arr.set(state.k, tmp.get(state.i));
                    state.i++;
                }
                else {
                    arr.set(state.k, tmp.get(state.j));
                    state.j--;
                }
                state.k++;
                if (state.k > state.end) {
                    stack.remove(stack.size() - 1);
                }
                break;
        }
        
        if (stack.isEmpty()) {
            done = true;
        }
    }

    @Override
    public boolean isSorted() {
        return done;
    }
    
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
        
        static enum State {
            SPLITTING, COPYDOWN_FIRST, COPYDOWN_SECOND, MERGING
        };
    }
}
