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
 * @param <T>
 */
public class QuickSorter<T extends Comparable<T>> implements SteppableSorter {
    private final DemoArray<T> arr;
    private final List<QuickStackFrame> stack;
    
    private boolean done;

    public QuickSorter(DemoArray<T> arr) {
        this.arr = arr;
        this.done = arr.length() <= 1;
        this.stack = new ArrayList<>();
        stack.add(new QuickStackFrame(0, arr.length() - 1));
    }
    
    @Override
    public void step() {
        if (done) {
            return;
        }
        
        QuickStackFrame state = stack.get(stack.size() - 1);
        
        switch (state.state) {
            case PARTITIONING:
                if (state.i < state.j) {
                    state.state = State.CHECK_LOW;
                }
                else {
                    state.state = State.SPLITTING;
                }
                break;
                
            case CHECK_LOW:
                if (arr.compare(state.i, state.pivot) < 0) {
                    state.i++;
                }
                else {
                    state.state = State.CHECK_HIGH;
                }
                break;
                
            case CHECK_HIGH:
                if (arr.compare(state.j, state.pivot) > 0) {
                    state.j--;
                }
                else {
                    if (state.i < state.j) {
                        arr.swap(state.i, state.j);
                    }
                    if (state.i <= state.j) {
                        state.i++;
                        state.j--;
                    }
                    state.state = State.PARTITIONING;
                }
                break;
                
            case SPLITTING:
                stack.remove(stack.size() - 1);
                
                if (state.i < state.end) {
                    stack.add(new QuickStackFrame(state.i, state.end));
                }
                if (state.beg < state.j) {
                    stack.add(new QuickStackFrame(state.beg, state.j));
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
    
    public static enum State {
        SPLITTING, PARTITIONING, CHECK_LOW, CHECK_HIGH
    }
}
