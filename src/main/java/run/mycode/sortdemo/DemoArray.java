package run.mycode.sortdemo;

import java.util.Arrays;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import static run.mycode.sortdemo.DemoArray.accessCallback;

/**
 * An array with accessors to allow tracking of data accesses and changes
 * 
 * @author bdahl
 * 
 * @param <T> The type of data to store 
 */
public class DemoArray<T extends Comparable<T>> {
    private T[] data;
    
    private IntegerProperty accesses;
    private IntegerProperty gets;
    private IntegerProperty puts;
    private IntegerProperty compares;
    private IntegerProperty swaps;
    
    private changeCallback<T> onChange;
    private accessCallback<T> onAccess;
    private comparedCallback<T> onCompare;
    
    /**
     * Create a new array of a given size
     * 
     * @param size the number of elements to reserve space for 
     */
    @SuppressWarnings("unchecked")
    public DemoArray(int size) {
        this();
        data = (T[])(new Comparable[size]);
    }
    
    /**
     * Encapsulate an existing array. Note: makes a shallow copy of the
     * provided array
     * 
     * @param arr An array to copy into the DemoArray
     */
    public DemoArray(T[] arr) {
        this();
        data = Arrays.copyOf(arr, arr.length);
    }
    
    private DemoArray() {
        accesses = new SimpleIntegerProperty(0);
        gets = new SimpleIntegerProperty(0);
        puts = new SimpleIntegerProperty(0);
        compares = new SimpleIntegerProperty(0);
        swaps = new SimpleIntegerProperty(0);
    }
    
    /**
     * Get the size of the array
     * 
     * @return the size of the array
     */
    public int length() {
        return data.length;
    }
    
    /**
     * Reset the access/change counts
     */
    public synchronized void resetCounts() {
        reset();
    }
    
    private void reset() {
        accesses.set(0);
        gets.set(0);
        puts.set(0);
        compares.set(0);
        swaps.set(0);
    }
    
    public synchronized T get(int index) {
        accesses.set(accesses.get() + 1);
        gets.set(gets.get() + 1);

        if (onAccess != null) {
            onAccess.call(index, data[index]);
        }
        
        return data[index];
    }
    
    public synchronized void set(int index, T item) {
        accesses.set(accesses.get() + 1);
        puts.set(puts.get() + 1);

        if (onAccess != null) {
            onAccess.call(index, data[index]);
        }        
        if (onChange != null) {
            onChange.call(index, data[index], item);
        }
        
        data[index] = item;
    }
    
    public synchronized int compare(int index1, int index2) {
        accesses.set(accesses.get() + 2);
        gets.set(gets.get() + 2);
        compares.set(compares.get() + 1);
        if (onAccess != null) {
            onAccess.call(index1, data[index1]);
            onAccess.call(index2, data[index2]);
        }
        if (onCompare != null) {
            onCompare.call(index1, index2, data[index1], data[index2]);
        }
        
        return data[index1].compareTo(data[index2]);
    }
    
    public synchronized int compare(int index, T item) {
        accesses.set(accesses.get() + 1);
        gets.set(gets.get() + 1);
        compares.set(compares.get() + 1);
        if (onAccess != null) {
            onAccess.call(index, data[index]);
        }
        if (onCompare != null) {
            onCompare.call(index, -1, data[index], null);
        }
        
        return data[index].compareTo(item);
    }
    
    public synchronized void swap(int index1, int index2) {
        accesses.set(accesses.get() + 4);
        gets.set(gets.get() + 2);
        puts.set(puts.get() + 2);
        swaps.set(swaps.get() + 1);

        if (onAccess != null) {
            onAccess.call(index1, data[index1]);
            onAccess.call(index2, data[index2]);
        }        
        if (onChange != null) {
            onChange.call(index1, data[index1], data[index2]);
            onChange.call(index2, data[index2], data[index1]);
        }
        
        T temp = data[index1];
        data[index1] = data[index2];
        data[index2] = temp;
    }
    
    public void setOnChange(changeCallback<T> callback) {
        onChange = callback;
    }
    
    public void setOnAccess(accessCallback<T> callback) {
        onAccess = callback;
    }
    
    public void setOnCompare(comparedCallback<T> callback) {
        onCompare = callback;
    }

    public IntegerProperty getAccessesProperty() {
        return accesses;
    }

    public IntegerProperty getGetsProperty() {
        return gets;
    }

    public IntegerProperty getPutsProperty() {
        return puts;
    }

    public IntegerProperty getComparesProperty() {
        return compares;
    }

    public IntegerProperty getSwapsProperty() {
        return swaps;
    }
    
    public int getAccesses() {
        return accesses.get();
    }

    public int getGets() {
        return gets.get();
    }

    public int getPuts() {
        return puts.get();
    }

    public int getCompares() {
        return compares.get();
    }

    public int getSwaps() {
        return swaps.get();
    }
    
    
    @FunctionalInterface
    public interface accessCallback<T> {
        void call(int index, T val);
    }
    
    @FunctionalInterface
    public interface changeCallback<T> {
        void call(int index, T oldVal, T newVal);
    }
    
    @FunctionalInterface
    public interface comparedCallback<T> {
        void call(int index1, int index2, T val1, T val2);
    }
}
