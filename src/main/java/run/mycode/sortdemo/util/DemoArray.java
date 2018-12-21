package run.mycode.sortdemo.util;

import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import static run.mycode.sortdemo.util.DemoArray.accessCallback;

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
        data = (T[]) (new Comparable[size]); // creating a typed array doesn't work... force it
    }

    /**
     * Encapsulate an existing array. Note: makes a shallow copy of the provided
     * array
     *
     * @param arr An array to copy into the DemoArray
     */
    public DemoArray(T[] arr) {
        this();
        data = Arrays.copyOf(arr, arr.length);
    }

    /**
     * initialize private properties of the array
     */
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
    public void resetCounts() {
        resetProperties();
    }

    private void resetProperties() {
        fxRunSafe(() -> {
            accesses.set(0);
            gets.set(0);
            puts.set(0);
            compares.set(0);
            swaps.set(0);
        });
    }

    private void fxRunSafe(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    /**
     * Get an element from the array
     *
     * @param index
     * @return
     */
    public T get(int index) {
        final T d = data[index];
        fxRunSafe(() -> {
            accesses.set(accesses.get() + 1);
            gets.set(gets.get() + 1);

            if (onAccess != null) {
                onAccess.call(index, d);
            }
        });

        return data[index];
    }

    /**
     * Remove an element from the array. The element will be replaced by null.
     * Note: the get operation will be counted, but not the set operation.
     *
     * @param index the location to remove an element from
     * @return the element removed from the array
     */
    public T remove(int index) {
        final T d = data[index];
        fxRunSafe(() -> {
            accesses.set(accesses.get() + 1);
            gets.set(gets.get() + 1);

            if (onAccess != null) {
                onAccess.call(index, d);
            }
            if (onChange != null) {
                onChange.call(index, d, null);
            }
        });

        T item = data[index];
        data[index] = null;
        return item;
    }

    /**
     * Update an element in the array
     *
     * @param index
     * @param item
     */
    public void set(int index, T item) {
        final T d = data[index];
        fxRunSafe(() -> {
            accesses.set(accesses.get() + 1);
            puts.set(puts.get() + 1);

            if (onAccess != null) {
                onAccess.call(index, d);
            }
            if (onChange != null) {
                onChange.call(index, null, item);
            }
        });

        data[index] = item;
    }

    /**
     * Move an item in the array to a new position
     *
     * @param index the index of the item to move, will contain null after move
     * @param newIndex the new index to move to
     */
    public void move(int index, int newIndex) {
        final T d1 = data[index];
        final T d2 = data[newIndex];
        
        fxRunSafe(() -> {
            accesses.set(accesses.get() + 1);
            gets.set(gets.get() + 1);
            puts.set(puts.get() + 1);

            if (onAccess != null) {
                onAccess.call(index, d1);
                onAccess.call(newIndex, d1);
            }
            if (onChange != null) {
                onChange.call(index, d1, null);
                onChange.call(newIndex, d2, d1);
            }
        });

        data[newIndex] = data[index];
        data[index] = null;
    }

    /**
     * Compare two elements in the array
     *
     * @param index1
     * @param index2
     * @return
     */
    public int compare(int index1, int index2) {
        final T d1 = data[index1];
        final T d2 = data[index2];
        
        fxRunSafe(() -> {
            accesses.set(accesses.get() + 2);
            gets.set(gets.get() + 2);
            compares.set(compares.get() + 1);
            if (onAccess != null) {
                onAccess.call(index1, d1);
                onAccess.call(index2, d2);
            }
            if (onCompare != null) {
                onCompare.call(index1, index2, d1, d2);
            }
        });

        return data[index1].compareTo(data[index2]);
    }

    /**
     * Compare an element in the array with another item outside the array
     *
     * @param index
     * @param item
     * @return Negative if the array element comes before the item, 0 if they
     * are the same value, Positive if the item should come before the array
     * element
     */
    public int compare(int index, T item) {
        final T d = data[index];
        fxRunSafe(() -> {
            accesses.set(accesses.get() + 1);
            gets.set(gets.get() + 1);
            compares.set(compares.get() + 1);
            if (onAccess != null) {
                onAccess.call(index, d);
            }
            if (onCompare != null) {
                onCompare.call(index, -1, d, item);
            }
        });

        return data[index].compareTo(item);
    }

    /**
     * Swap the positions of two array elements
     *
     * @param index1
     * @param index2
     */
    public void swap(int index1, int index2) {
        final T d1 = data[index1];
        final T d2 = data[index2];
        
        fxRunSafe(() -> {
            accesses.set(accesses.get() + 4);
            gets.set(gets.get() + 2);
            puts.set(puts.get() + 2);
            swaps.set(swaps.get() + 1);

            if (onAccess != null) {
                onAccess.call(index1, d1);
                onAccess.call(index2, d2);
            }
            if (onChange != null) {
                onChange.call(index1, null, d2);
                onChange.call(index2, null, d1);
            }
        });

        T temp = data[index1];
        data[index1] = data[index2];
        data[index2] = temp;
    }

    /**
     * Set the callback to call when an element in the array is changed
     *
     * @param callback
     */
    public void setOnChange(changeCallback<T> callback) {
        onChange = callback;
    }

    /**
     * Set the callback to call when an element in the array is accessed
     *
     * @param callback
     */
    public void setOnAccess(accessCallback<T> callback) {
        onAccess = callback;
    }

    /**
     * Set the callback to call when two items are compared
     *
     * @param callback
     */
    public void setOnCompare(comparedCallback<T> callback) {
        onCompare = callback;
    }

    /**
     * Access the access count property of the array
     *
     * @return
     */
    public IntegerProperty getAccessesProperty() {
        return accesses;
    }

    /**
     * Access the reads count property of the array
     *
     * @return
     */
    public IntegerProperty getGetsProperty() {
        return gets;
    }

    /**
     * Access the sets count property of the array
     *
     * @return
     */
    public IntegerProperty getPutsProperty() {
        return puts;
    }

    /**
     * Access the comparisons count property of the array
     *
     * @return
     */
    public IntegerProperty getComparesProperty() {
        return compares;
    }

    /**
     * Access the swaps count property of the array
     *
     * @return
     */
    public IntegerProperty getSwapsProperty() {
        return swaps;
    }

    /**
     * Get the number of times array elements have been accessed
     *
     * @return
     */
    public int getAccesses() {
        return accesses.get();
    }

    /**
     * Get the number of times array elements have been read
     *
     * @return
     */
    public int getGets() {
        return gets.get();
    }

    /**
     * Get the number of times array elements have been written
     *
     * @return
     */
    public int getPuts() {
        return puts.get();
    }

    /**
     * Get the number of times array elements have been compared
     *
     * @return
     */
    public int getCompares() {
        return compares.get();
    }

    /**
     * Get the number of times array elements have been swapped
     *
     * @return
     */
    public int getSwaps() {
        return swaps.get();
    }

    /**
     * A callback interface to be used when elements of the array are accessed
     *
     * @param <T> The type of element that is stored in the array
     */
    @FunctionalInterface
    public interface accessCallback<T> {

        /**
         * The callback method that will be called when array elements are
         * accessed
         *
         * @param index the index of the item that was accessed
         * @param val the item that was accessed
         */
        void call(int index, T val);
    }

    /**
     * A callback interface to be used when elements of the array are changed
     *
     * @param <T> The type of element that is stored in the array
     */
    @FunctionalInterface
    public interface changeCallback<T> {

        /**
         * The callback method that will be called when array elements are
         * accessed
         *
         * @param index the index of the item that was accessed
         * @param oldVal the previous item at that index (may be null)
         * @param newVal the new item placed at that index (may be null)
         */
        void call(int index, T oldVal, T newVal);
    }

    /**
     * A callback interface to be used when elements of the array are compared
     *
     * @param <T> The type of element that is stored in the array
     */
    @FunctionalInterface
    public interface comparedCallback<T> {

        /**
         * The callback method that will be called when array elements are
         * accessed
         *
         * @param index1 the first index that was compared
         * @param index2 the second index that was compared (negative if the
         * comparison was made to an item outside the array)
         * @param val1 the first item that was compared
         * @param val2 the second item that was compared
         */
        void call(int index1, int index2, T val1, T val2);
    }
}
