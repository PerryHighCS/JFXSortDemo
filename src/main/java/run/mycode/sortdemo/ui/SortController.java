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
    private Pane contentPane;

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

    private final int NUM_BARS = 100;
    private final double CYCLE_TIME = 5;
    private final double ACCESS_DIEOFF = 100;
    private final double CHANGE_DIEOFF = 300;

    private double barWidth;
    private boolean halfHeight;

    @FXML
    private void startSorts(ActionEvent event) {
        final Algorithm sortAlgorithm = sortChoice.getValue();
        final DataLayout startingSort = dataChoice.getValue();

        if (sortAlgorithm == Algorithm.ALL) {
            Runnable thisSort = null;
            Algorithm[] algorithms = Algorithm.values();

            for (int i = algorithms.length - 1; i >= 1; i--) {
                Algorithm a = algorithms[i];

                if (a == Algorithm.ALL) {
                    continue;
                }
                final Runnable nextSort = thisSort;
                thisSort = () -> doSort(startingSort, a, nextSort);
            }

            if (thisSort != null) {
                thisSort.run();
            }
        } else {
            doSort(startingSort, sortAlgorithm, null);
        }
    }

    private void doSort(DataLayout startingSortType, Algorithm sortAlgorithm, Runnable whenDone) {
        final SteppableSorter sorter;
        DemoArray<SortableBar> array;

        halfHeight = false;

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
                halfHeight = true;
                array = initBarArray(startingSortType, false);
                DemoArray<SortableBar> tmp = new DemoArray<>(array.length());
                initEvents(tmp, true);
                sorter = new MergeSorter<>(array, tmp);
                connectData(array, tmp);
                break;
            case QUICK:
                array = initBarArray(startingSortType, false);
                sorter = new QuickSorter<>(array);
                connectData(array);
                break;
            default:
                throw new IllegalArgumentException("Sorting algorithm not implemented: " + sortAlgorithm);
        }

        final Timeline sortAnimation = new Timeline();
        sortAnimation.getKeyFrames().add(new KeyFrame(
                Duration.millis(CYCLE_TIME),
                ae -> {
                    if (sorter.isSorted()) {
                        sortAnimation.stop();

                        if (whenDone != null) {
                            Timeline waitForIt = new Timeline(
                                    new KeyFrame(
                                            Duration.millis(1000),
                                            e -> whenDone.run()
                                    )
                            );
                            waitForIt.setCycleCount(1);
                            waitForIt.play();
                        }
                    } else {
                        sorter.step();
                    }
                })
        );

        sortAnimation.setCycleCount(Timeline.INDEFINITE);
        sortAnimation.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sortChoice.getItems().addAll(Algorithm.values());
        sortChoice.getSelectionModel().select(0);
        dataChoice.getItems().addAll(DataLayout.values());
        dataChoice.getSelectionModel().select(0);

        contentPane.widthProperty().addListener(ae -> redraw());
        contentPane.heightProperty().addListener(ae -> redraw());

        initBarArray(DataLayout.SORTED, false);
        redraw();
    }

    public void redraw() {
        double bottom = contentPane.getHeight();
        double maxHeight = contentPane.getHeight();

        double halfPos = bottom / 2;

        if (halfHeight) {
            maxHeight /= 2;
        }

        barWidth = contentPane.getWidth() / NUM_BARS;
        double unitHeight = maxHeight / NUM_BARS;

        List<Node> bars = contentPane.getChildren();

        for (Node n : bars) {
            if (!(n instanceof SortableBar)) {
                continue;
            }
            SortableBar bar = (SortableBar) n;

            bar.setHeight((bar.getValue() + 1) * unitHeight);

            double oldWidth = bar.getWidth();
            double idx = bar.getTranslateX() / oldWidth;

            bar.setWidth(barWidth);
            bar.setTranslateX(idx * barWidth);

            if (bar.getLayoutY() == 0) {
                bar.setTranslateY(halfPos - bar.getHeight());
            } else {
                bar.setLayoutY(bottom);
                bar.setTranslateY(-bar.getHeight());
            }
        }
    }

    private DemoArray<SortableBar> initBarArray(DataLayout initialLayout, boolean topHalf) {
        contentPane.getChildren().clear();

        SortableBar[] bars = new SortableBar[NUM_BARS];

        for (int i = 0; i < NUM_BARS; i++) {
            bars[i] = new SortableBar(i);
            bars[i].setHeight(i);
        }

        switch (initialLayout) {
            case RANDOM:
                randomize(bars);
                break;
            case REVERSED:
                reverse(bars);
                break;
            case SORTED:
                break;
        }

        double maxHeight = contentPane.getHeight();
        if (halfHeight) {
            maxHeight /= 2;
        }

        barWidth = contentPane.getWidth() / bars.length;
        double unitHeight = maxHeight / bars.length;

        for (int i = 0; i < bars.length; i++) {
            bars[i].setWidth(barWidth);
            bars[i].setHeight(unitHeight * (bars[i].getValue() + 1));
            bars[i].setTranslateX(barWidth * i);
            bars[i].setTranslateY(-bars[i].getHeight());
            bars[i].setLayoutY(contentPane.getHeight());

            contentPane.getChildren().add(bars[i]);
        }

        DemoArray<SortableBar> array = new DemoArray<>(bars);

        initEvents(array, topHalf);

        return array;
    }

    private void initEvents(DemoArray<SortableBar> array, boolean topHalf) {
        array.setOnAccess((idx, val) -> {
            if (val == null) {
                return;
            }
            val.setColor(Color.BLUE);
            val.fadeToColor(Color.BLACK, ACCESS_DIEOFF);
        });

        array.setOnChange((idx, oldval, val) -> {
            if (val == null) {
                return;
            }

            barWidth = contentPane.getWidth() / NUM_BARS;
            double bottom = contentPane.getHeight();

            if (topHalf) {
                val.setLayoutY(0);
                val.setTranslateY((bottom / 2) - val.getHeight());
            } else {
                val.setLayoutY(bottom);
                val.setTranslateY(-val.getHeight());
            }

            val.setTranslateX(idx * barWidth);

            val.setColor(Color.RED);
            val.fadeToColor(Color.BLACK, CHANGE_DIEOFF);
        });
    }

    private void connectData(DemoArray arr) {
        accesses.textProperty().bind(arr.getAccessesProperty().asString());
        reads.textProperty().bind(arr.getGetsProperty().asString());
        writes.textProperty().bind(arr.getPutsProperty().asString());
        comps.textProperty().bind(arr.getComparesProperty().asString());
        swaps.textProperty().bind(arr.getSwapsProperty().asString());
    }

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

    private void randomize(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int newPos = (int) (Math.random() * arr.length);
            Object t = arr[i];
            arr[i] = arr[newPos];
            arr[newPos] = t;
        }
    }

    private void reverse(Object[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int newPos = arr.length - i - 1;
            Object t = arr[newPos];
            arr[newPos] = arr[i];
            arr[i] = t;
        }
    }

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
