package run.mycode.sortdemo.ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author bdahl
 */
public class SortableBar  extends Rectangle implements Comparable<SortableBar> {
    private final Integer val;
    
    public SortableBar(int val) {
        this.val = val;
        this.setStroke(Color.GRAY);
        this.setStrokeWidth(1);
    }
    
    public int getValue() {
        return val;
    }
    
    @Override
    public int compareTo(SortableBar o) {
        return val.compareTo(o.val);        
    }
    
    public void setColor(Color c) {
        this.setFill(c);
    }
    
    public void fadeToColor(Color c, double milliseconds) {
        SortableBar that = this;
        final Animation ani = new Transition() {
            private final Color start;
            {
                start = (Color)SortableBar.this.getFill();
                
                setCycleDuration(Duration.millis(milliseconds));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                that.setFill(start.interpolate(c, frac));
            }
        };
        ani.play();
    }
}
