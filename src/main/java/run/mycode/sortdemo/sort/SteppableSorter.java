package run.mycode.sortdemo.sort;

/**
 * An interface for a sorting algorithm that can be run by repeatedly calling
 * the step method until the data isSorted.
 *
 * @author bdahl
 */
public interface SteppableSorter {

    /**
     * Perform the next step of the sort
     */
    public void step();

    /**
     * Check if sorting is complete
     *
     * @return true if the sorting algorithm has completed
     */
    public boolean isSorted();

}
