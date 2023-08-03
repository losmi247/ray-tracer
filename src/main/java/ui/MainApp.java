package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class for the main GUI app using JavaFX.
 * 
 * Use
 *      mvn javafx:run -f "/home/milos/Desktop/Nesto/VSCode/ray-tracer/pom.xml"
 * to run the GUI app (replace with absolute path to pom.xml).
 */

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /// load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/openjfx/scene.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        /// get the controller from the loaded fxml
        FXMLController fxmlController = fxmlLoader.getController();
        /// set the stage variable in the controller to this app's main stage
        fxmlController.setStage(stage);
        
        /// create a scene, load css
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/openjfx/styles.css").toExternalForm());
        
        /// set title of the stage, and show
        stage.setTitle("Ray Tracer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}