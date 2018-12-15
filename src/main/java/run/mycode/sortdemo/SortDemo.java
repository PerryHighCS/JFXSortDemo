package run.mycode.sortdemo;

import run.mycode.sortdemo.ui.SortController;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class SortDemo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxl = new FXMLLoader(getClass().getResource("/fxml/SortScene.fxml"));
        Parent root = fxl.load();
        
        SortController sc = fxl.<SortController>getController();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.getIcons().add(new Image(SortDemo.class.getResourceAsStream("/icon.png")));
        stage.setTitle("SortDemo");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
