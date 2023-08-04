package ui;

import rendering.Camera;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ProgressBar;

/**
 * Class for a controller for the scene.fxml 
 * description of the GUI.
 */

public class FXMLController {
    /// the stage passed from the MainApp
    private Stage mainStage;

    /// absolute path to selected scene description XML
    private String absolutePathToSceneDescription;


    /// button to load the .fxml file
    @FXML
    private Button openFileChooserButton;
    /// label for the file chooser button
    @FXML
    private Label openFileChooserButtonLabel;

    /// button to render the chosen scene description
    @FXML
    private Button renderButton;
    /// label for the render button
    @FXML
    private Label renderButtonLabel;

    /// The image view for displaying the last rendered image.
    /// The only image file ever displayed and being rendered
    /// into is "./src/main/resources/rendered images/result.png".
    @FXML
    private ImageView renderedImageView;
    /// label for the rendered image
    @FXML
    private Label renderedImageViewLabel;

    /// progress bar to show rendering progress
    @FXML
    private ProgressBar renderingProgressBar;
    /// label for the progress bar
    @FXML
    private Label renderingProgressBarLabel;

    /**
     * Methods
     */
    /*
       Method that initialises the state of the controller
       on creation.
     */
    public void initialize() throws FileNotFoundException {
        /// initialise label of the button for choosing and loading scene description
        this.openFileChooserButtonLabel.setText("No file selected");

        /// initialise the progress bar
        this.renderingProgressBar.setProgress(0);

        /// set path to scene description to null initially
        this.absolutePathToSceneDescription = null;

        /// show the starting image ("Please load a scene description")
        this.showStartingImageInImageView();

        /// TODO - set a blank image in the ImageView while we are waiting for the rendered image
    }
    /* 
       Method that is called when the button to
       navigate the file system and choose one .xml file
       description is clicked.

       It updates the absolute path to the XML.
    */
    public void loadSceneDescription(final ActionEvent e) {
        /// choose and load the scene description
        FileChooser fileChooser = new FileChooser();
        File chosenSceneDescription = fileChooser.showOpenDialog(this.mainStage);

        /// if the file is null, or is not XML
        if(chosenSceneDescription == null || !chosenSceneDescription.getName().endsWith(".xml") ) {
            this.openFileChooserButtonLabel.setText("Invalid file chosen");
            /// reset path to null
            this.absolutePathToSceneDescription = null;
            return;
        }
        else {
            this.openFileChooserButtonLabel.setText("'" + chosenSceneDescription.getName() + "' loaded");
        }

        /// update the selected absolute path to the scene description XML
        this.absolutePathToSceneDescription = chosenSceneDescription.getAbsolutePath();

        /// reset the progress bar to beginning
        this.renderingProgressBar.setProgress(0);
    }
    /*
       Method that is called when the button to render the
       chosen scene description is clicked.
     */
    public void renderSceneDescription(final ActionEvent e) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException{
        /// if no valid XML description has been loaded
        if(this.absolutePathToSceneDescription == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occured");
            alert.setContentText("First load a valid XML scene description!");

            alert.showAndWait();
            return;
        }

        /// show the loading-circle in ImageView
        this.showLoadingScreenInImageView();

        /// TODO - update progress bar somehow while rendering

        /// otherwise create a camera
        Camera camera = new Camera();
        /// render and save the image at the default location /resources/rendered images/result.png
        Camera.saveImage(camera.renderWithCoreParallelization(this.absolutePathToSceneDescription));

        /// (re)load the rendered image in the image view
        this.showRenderedImageInImageView();
    }
    /*
       Method that loads the starting image ("Please load
       a scene description") to the ImageView.
     */
    public void showStartingImageInImageView() throws FileNotFoundException {
        this.loadImageFileInImageView("./src/main/resources/assets/starting_image.png");
    }
    /*
       Method that (re)loads the image that is displayed
       in the ImageView. It reads the image file
       "./src/main/resources/rendered images/result.png"
       again, and displays the last rendered image
       in the ImageView.
     */
    public void showRenderedImageInImageView() throws FileNotFoundException {
        /// load the last rendered image
        this.loadImageFileInImageView("./src/main/resources/rendered images/result.png");
    }
    /*
       Method that loads a loading-circle gif to the
       ImageView, used while an image is being rendered.

       The gif is from 
       https://giphy.com/gifs/awesome-circle-loading-WiIuC6fAOoXD2
     */
    public void showLoadingScreenInImageView() throws FileNotFoundException {
        /// TODO - see why GIFs are not being loaded
        this.loadImageFileInImageView("./src/main/resources/assets/loading_circle.gif");
    }
    /*
       Method that loads an arbitrary image file at the given
       relative or absolute path to the ImageView.
     */
    public void loadImageFileInImageView(String pathToImageFile) throws FileNotFoundException {
        /// take the image file
        File imageFile = new File(pathToImageFile);
        /// get the input stream and display the image
        InputStream isImage = (InputStream) new FileInputStream(imageFile);
        this.renderedImageView.setImage(new Image(isImage));
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