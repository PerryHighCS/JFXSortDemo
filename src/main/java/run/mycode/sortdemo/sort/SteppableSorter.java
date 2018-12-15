package run.mycode.sortdemo.sort;

/**
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
     * @return 
     */
    public boolean isSorted();
    
}
