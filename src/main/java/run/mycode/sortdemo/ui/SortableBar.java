package run.mycode.sortdemo.ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * A colored Bar that can be compared to other SortableBars and provides for
 * color animations
 * 
 * @author bdahl
 */
public class SortableBar extends Rectangle implements Comparable<SortableBar> {
    private final Integer val;
    private Color color;
    
    /**
     * Create a SortableBar with an inherent value
     * @param val the value this bar represents
     */
    public SortableBar(int val) {
        this.val = val;
        this.color = Color.BLACK;
        this.setFill(Color.BLACK);
        this.setStroke(Color.GRAY);
        this.setStrokeWidth(1);
    }
    
    /**
     * Get the value this bar represents
     * @return the value assigned to this bar
     */
    public int getValue() {
        return val;
    }
    
    /**
     * Compare this bar with another bar
     * @param o the other bar
     * @return Negative if the value of this bar comes before the value of the 
     *         other bar, 0 if they represent the same value, Positive if this
     *         bar comes after the other bar
     */
    @Override
    public int compareTo(SortableBar o) {
        return val.compareTo(o.val);        
    }
    
    /**
     * Set the base fill color of this bar
     * @param c 
     */
    public void setColor(Color c) {
        this.color = c;
        this.setFill(c);
    }
    
    /**
     * Temporarily change the color of this bar, fading back to its base color
     * @param c The temporary color to change this bar to
     * @param milliseconds The time to take fading the bar back to its normal
     *                      color
     */
    public void fadeColor(Color c, double milliseconds) {
        final SortableBar that = this;
        final Animation ani = new Transition() {
            {                
                setCycleDuration(Duration.millis(milliseconds));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                that.setFill(c.interpolate(that.color, frac));
            }
        };
        ani.play();
    }
}