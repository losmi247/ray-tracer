package ui;

import java.io.File;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Class for a controller for the scene.fxml 
 * description of the GUI.
 */

public class FXMLController {
    /// the stage passed from the MainApp
    private Stage mainStage;

    /// button to load the .fxml file
    @FXML
    private Button openFileChooserButton;

    /// label for the above button
    @FXML
    private Label openFileChooserButtonLabel;

    /**
     * Methods
     */
    /*
       Method that initialises the state of the controller
       on creation.
     */
    public void initialize() {
        /// initialise label
        this.openFileChooserButtonLabel.setText("No file selected");
    }
    /* 
       Method that is called when the button is clicked to
       navigate the file system and choose one .xml file
       description.
    */
    public void onClick(final ActionEvent e) {
        /// load the scene description
        FileChooser fileChooser = new FileChooser();
        File chosenSceneDescription = fileChooser.showOpenDialog(this.mainStage);

        /// if the file is null, or is not XML
        if(chosenSceneDescription == null || !chosenSceneDescription.getName().endsWith(".xml") ) {
            this.openFileChooserButtonLabel.setText("Invalid file chosen");
        }
        else {
            this.openFileChooserButtonLabel.setText("'" + chosenSceneDescription.getName() + "' loaded");
        }

        /// TODO - now pass the XML file to XML scene description parser
    }

    /*
     * Getters, Setters
     */
    /*
       Setter for the stage variable in the controller. The stage
       variable is used on loading the file chooser.
     */
    public void setStage(Stage stage) {
        this.mainStage = stage;
    }
}