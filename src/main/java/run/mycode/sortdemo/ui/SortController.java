package run.mycode.sortdemo.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import run.mycode.sortdemo.DemoArray;
import run.mycode.sortdemo.sort.BubbleSorter;
import run.mycode.sortdemo.sort.InsertionSorter;
import run.mycode.sortdemo.sort.MergeSorter;
import run.mycode.sortdemo.sort.QuickSorter;
import run.mycode.sortdemo.sort.SelectionSorter;
import run.mycode.sortdemo.sort.SteppableSorter;

public class SortController implements Initializable {

    @FXML
    private Pane barDisplay;

    @FXML
    private ChoiceBox<Algorithm> sortChoice;

    @FXML
    private ChoiceBox<DataLayout> dataChoice;

    @FXML
    private Parent stats;

    @FXML
    private Label accesses;

    @FXML
    private Label reads;

    @FXML
    private Label writes;

    @FXML
    private Label swaps;

    @FXML
    private Label comps;
    
    @FXML
    private Label time;

    private final int NUM_BARS = 100;
    private final double CYCLE_TIME = 5;
    private final double ACCESS_DIEOFF = 100;
    private final double CHANGE_DIEOFF = 300;

    private boolean halfHeight;

    /**
     * Handle the user clicking on the Sort button by beginning the sorting demo
     *
     * @param event unused
     */
    @FXML
    private void startSorts(ActionEvent event) {
        final Algorithm sortAlgorithm = sortChoice.getValue();
        final DataLayout startingSort = dataChoice.getValue();

        if (sortAlgorithm != Algorithm.ALL) {
            // If a particular sort was chosen, demonstrate it
            demoSort(startingSort, sortAlgorithm, null);
        } else {
            // Perform all sorts by building up a chain of callbacks from last to first

            // The very last sort should reset the choice box to ALL
            Runnable thisSort = () -> sortChoice.setValue(Algorithm.ALL);

            // Loop through all the available algrithms in reverse order
            Algorithm[] algorithms = Algorithm.values();
            for (int i = algorithms.length - 1; i >= 1; i--) {
                Algorithm a = algorithms[i];

                // When it is this algorithm's turn,
                final Runnable nextSort = thisSort;
                thisSort = () -> {
                    sortChoice.setValue(a);             // First set the choice box to indicate the algorithm
                    demoSort(startingSort, a, nextSort);  // Then perform the sort deno
                };
            }

            // Call the last built sorting callback
            thisSort.run();
        }
    }

    /**
     * Demonstrate a sorting algorithm
     *
     * @param startingSortType The initial layout of the data, ie randomized or
     * already sorted data
     * @param sortAlgorithm The sorting algorithm to use
     * @param whenDone A callback to make when the sorting is complete
     */
    private void demoSort(DataLayout startingSortType, Algorithm sortAlgorithm, Runnable whenDone) {
        final SteppableSorter sorter;
        DemoArray<SortableBar> array;

        // Most algorithms only use one array, so prepare to display using the
        // full hight of the content pane
        halfHeight = false;

        // Prepare the array for sorting, choose the proper sorter, and connect
        // the array's instrumentation to the display labels
        switch (sortAlgorithm) {
            case BUBBLE:
                array = initBarArray(startingSortType, false);
                sorter = new BubbleSorter<>(array);
                connectData(array);
                break;
            case SELECTION:
                array = initBarArray(startingSortType, false);
                sorter = new SelectionSorter<>(array);
                connectData(array);
                break;
            case INSERTION:
                array = initBarArray(startingSortType, false);
                sorter = new InsertionSorter<>(array);
                connectData(array);
                break;
            case MERGE:
                halfHeight = true; // Merge sort needs two arrays, so shrink the display
                array = initBarArray(startingSortType, false);
                DemoArray<SortableBar> tmp = new DemoArray<>(array.length());
                initEvents(tmp, true);
                sorter = new MergeSorter<>(array, tmp);
                connectData(array, tmp); // Hook up instrumentation from both arrays
                break;
            case QUICK:
                array = initBarArray(startingSortType, false);
                sorter = new QuickSorter<>(array);
                connectData(array);
                break;
            default:
                throw new IllegalArgumentException("Sorting algorithm not implemented: " + sortAlgorithm);
        }

        final long startTime = System.nanoTime();
        time.setText("0ms");
        // Set up a timeline to repeatedly step through the sorting algorithm
        final Timeline sortAnimation = new Timeline();
        sortAnimation.getKeyFrames().add(new KeyFrame(
                Duration.millis(CYCLE_TIME), // The time between steps in the algorithm
                ae -> {
                    // Update the time display
                    long elapsed = (System.nanoTime() - startTime) / 1_000_000;
                    time.setText(elapsed + "ms");
                    
                    if (!sorter.isSorted()) { // If there is more sorting to do
                        sorter.step();        // perform the next step
                    } else {                  // If sorting is complete
                        sortAnimation.stop();   // Stop the animation

                        if (whenDone != null) { // and call any provided callback
                            Timeline waitForIt
                            = new Timeline(
                                    new KeyFrame( // after 1 second 
                                            Duration.millis(1000),
                                            e -> whenDone.run()
                                    )
                            );
                            waitForIt.setCycleCount(1);
                            waitForIt.play();
                        }
                    }
                })
        );

        // Start the sorting animation running, we'll stop it when we know its
        // done
        sortAnimation.setCycleCount(Timeline.INDEFINITE);
        sortAnimation.play();
    }

    /**
     * Set up the scene
     *
     * @param url unused
     * @param rb unused
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up the choiceboxes with the appropriate values and preselect 
        // the first option
        sortChoice.getItems().addAll(Algorithm.values());
        sortChoice.getSelectionModel().select(0);
        dataChoice.getItems().addAll(DataLayout.values());
        dataChoice.getSelectionModel().select(0);

        // Setup listeners to handle resizing the window
        barDisplay.widthProperty().addListener(ae -> redraw());
        barDisplay.heightProperty().addListener(ae -> redraw());
    }

    /**
     * Redraw the sorting display
     */
    public void redraw() {
        // Determine the bottom positions for the bars
        double bottom = barDisplay.getHeight();
        double halfPos = bottom / 2;

        // Calculate the sizing units for the bars
        final double maxHeight = halfHeight ? barDisplay.getHeight() / 2
                : barDisplay.getHeight();
        final double barWidth = barDisplay.getWidth() / NUM_BARS;
        final double unitHeight = maxHeight / NUM_BARS;

        // Readjust all of the bars
        final List<Node> bars = barDisplay.getChildren();
        bars.stream().filter(n -> n instanceof SortableBar)
                .forEach(n -> {
                    SortableBar bar = (SortableBar) n;

                    bar.setHeight((bar.getValue() + 1) * unitHeight);

                    // Calculate the bar's index from its position on screen
                    double idx = bar.getTranslateX() / bar.getWidth();

                    // Use the index and the bar's new width to set its position
                    bar.setWidth(barWidth);
                    bar.setTranslateX(idx * barWidth);

                    // Determine if the bar is in the top or bottom half of the screen\
                    // and move it to the correct location
                    if (bar.getLayoutY() == 0) {    // the top half is measured from y=0
                        bar.setTranslateY(halfPos - bar.getHeight());
                    } else {                        // the bottom half is measured from
                        bar.setLayoutY(bottom);     // the bottom of the display
                        bar.setTranslateY(-bar.getHeight());
                    }
                });
    }

    /**
     * Prepare an array of SortableBars
     *
     * @param initialLayout the starting ordering of the data
     * @param topHalf true if these bars will occupy the top half of the display
     * @return An initialized array filled with
     */
    private DemoArray<SortableBar> initBarArray(DataLayout initialLayout,
            boolean topHalf) {
        // Clear out anything currently being displayed
        barDisplay.getChildren().clear();

        // Create a normal array for preparing the bars
        SortableBar[] bars = new SortableBar[NUM_BARS];

        // Initialize the bars in sorted order
        for (int i = 0; i < NUM_BARS; i++) {
            bars[i] = new SortableBar(i);
        }

        // Reorganize the bars based on the chosen layout
        switch (initialLayout) {
            case RANDOM:
                randomize(bars);
                break;
            case REVERSED:
                reverse(bars);
                break;
            case SORTED:
                break;
            default:
                throw new IllegalArgumentException("Unknown data order: "
                        + initialLayout.toString());
        }

        // Determine the actual sizing for the bars
        final double bottom = barDisplay.getHeight();
        final double maxHeight = halfHeight ? barDisplay.getHeight() / 2
                : barDisplay.getHeight();
        final double unitHeight = maxHeight / bars.length;
        final double barWidth = barDisplay.getWidth() / bars.length;

        // Size and move the bars
        for (int i = 0; i < bars.length; i++) {
            bars[i].setWidth(barWidth);
            bars[i].setHeight(unitHeight * (bars[i].getValue() + 1));

            bars[i].setTranslateX(barWidth * i);

            if (topHalf) {
                bars[i].setTranslateY(bottom - bars[i].getHeight());
                bars[i].setLayoutY(0);
            } else {
                bars[i].setTranslateY(-bars[i].getHeight());
                bars[i].setLayoutY(bottom);
            }

            barDisplay.getChildren().add(bars[i]);
        }

        // Encapsulate the array of bars into a DemoArray
        DemoArray<SortableBar> array = new DemoArray<>(bars);

        // Hookup demoArray events so the bars will display properly
        initEvents(array, topHalf);

        return array;
    }

    /**
     * Connect DemoArray events to call-backs that update the display
     *
     * @param array The DemoArray to connect to
     * @param topHalf true if this array only fills the top half of the display
     */
    private void initEvents(DemoArray<SortableBar> array, boolean topHalf) {
        // When an element is accessed, turn it blue for a bit
        array.setOnAccess((idx, val) -> {
            if (val == null) {
                return;
            }
            val.setColor(Color.BLUE);
            val.fadeToColor(Color.BLACK, ACCESS_DIEOFF);
        });

        // When an element is modified, turn it red for a bit and make sure the
        // bar is properly located in the display
        array.setOnChange((idx, oldval, val) -> {
            if (val == null) {
                return;
            }

            // determine the width of the bars for positioning them
            final double barWidth = barDisplay.getWidth() / NUM_BARS;
            final double bottom = topHalf ? barDisplay.getHeight() / 2
                    : barDisplay.getHeight();

            if (topHalf) {
                val.setLayoutY(0);
                val.setTranslateY(bottom - val.getHeight());
            } else {
                val.setLayoutY(bottom);
                val.setTranslateY(-val.getHeight());
            }

            val.setTranslateX(idx * barWidth);

            val.setColor(Color.RED);
            val.fadeToColor(Color.BLACK, CHANGE_DIEOFF);
        });
    }

    /**
     * Hookup array instrumentation to the appropriate labels
     *
     * @param arr The DemoArray to monitor
     */
    private void connectData(DemoArray arr) {
        accesses.textProperty().bind(arr.getAccessesProperty().asString());
        reads.textProperty().bind(arr.getGetsProperty().asString());
        writes.textProperty().bind(arr.getPutsProperty().asString());
        comps.textProperty().bind(arr.getComparesProperty().asString());
        swaps.textProperty().bind(arr.getSwapsProperty().asString());
    }

    /**
     * Hookup array instrumentation to the appropriate labels. Information from
     * both arrays is totaled before display
     *
     * @param arr1 One DemoArray to monitor
     * @param arr2 The other DemoArray to monitor
     */
    private void connectData(DemoArray arr1, DemoArray arr2) {

        accesses.textProperty().bind(Bindings
                .add(arr1.getAccessesProperty(), arr2.getAccessesProperty())
                .asString());
        reads.textProperty().bind(Bindings
                .add(arr1.getGetsProperty(), arr2.getGetsProperty())
                .asString());
        writes.textProperty().bind(Bindings
                .add(arr1.getPutsProperty(), arr2.getPutsProperty())
                .asString());
        comps.textProperty().bind(Bindings
                .add(arr1.getComparesProperty(), arr2.getComparesProperty())
                .asString());
        swaps.textProperty().bind(Bindings
                .add(arr1.getSwapsProperty(), arr2.getSwapsProperty())
                .asString());
    }

    /**
     * Randomly rearrange the data in an array
     *
     * @param arr the array to reorder
     */
    private void randomize(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int newPos = (int) (Math.random() * arr.length);
            Object t = arr[i];
            arr[i] = arr[newPos];
            arr[newPos] = t;
        }
    }

    /**
     * Reverse the order of an array
     *
     * @param arr the array to reorder
     */
    private void reverse(Object[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int newPos = arr.length - i - 1;
            Object t = arr[newPos];
            arr[newPos] = arr[i];
            arr[i] = t;
        }
    }

    /**
     * The available sorting algorithms along with text descriptions
     */
    private static enum Algorithm {
        ALL("All"),
        BUBBLE("Bubble Sort"), SELECTION("Selection Sort"),
        INSERTION("Insertion Sort"), MERGE("Merge Sort"), QUICK("Quick Sort");

        public final String text;

        private Algorithm(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }
    };

    /**
     * The available initial data orderings along with text descriptions
     */
    private static enum DataLayout {
        RANDOM("Random"), SORTED("Sorted"), REVERSED("Reversed");

        public final String text;

        private DataLayout(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

}
