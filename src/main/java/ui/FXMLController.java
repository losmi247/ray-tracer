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
import javafx.concurrent.Task;

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

        /// set path to scene description to null initially
        this.absolutePathToSceneDescription = null;

        /// initalise progress to 0
        this.resetRenderingProgressBar();

        /// show the starting image ("Please load a scene description")
        this.showLoadSceneDescriptionPromptInImageView();
    }
    /* 
       Method that is called when the button to
       navigate the file system and choose one .xml file
       description is clicked.

       It updates the absolute path to the XML.
    */
    public void loadSceneDescription(final ActionEvent e) throws FileNotFoundException {
        /// choose and load the scene description
        FileChooser fileChooser = new FileChooser();
        File chosenSceneDescription = fileChooser.showOpenDialog(this.mainStage);

        /// if the file is null, or is not XML
        if(chosenSceneDescription == null || !chosenSceneDescription.getName().endsWith(".xml") ) {
            this.openFileChooserButtonLabel.setText("Invalid file chosen");

            /// reset path to null
            this.absolutePathToSceneDescription = null;

            /// reset ImageView to starting screen
            this.showLoadSceneDescriptionPromptInImageView();

            /// reset the progress bar to beginning
            this.resetRenderingProgressBar();

            return;
        }
        else {
            this.openFileChooserButtonLabel.setText("'" + chosenSceneDescription.getName() + "' loaded");
        }

        /// update the selected absolute path to the scene description XML
        this.absolutePathToSceneDescription = chosenSceneDescription.getAbsolutePath();

        /// reset the progress bar to beginning
        this.resetRenderingProgressBar();

        /// show the prompt to click the render button in ImageView
        this.showRenderPromptScreenInImageView();
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
            alert.setContentText("First load an XML scene description!");

            alert.showAndWait();

            /// show prompt to load scene description
            this.showLoadSceneDescriptionPromptInImageView();

            /// reset the progress bar to beginning
            this.resetRenderingProgressBar();

            return;
        }

        /// create a camera
        Camera camera = new Camera();
        /// Get the Task for rendering the scene, reporting progress, and saving rendered image at
        /// the default location '/resources/rendered images/result.png', from the created camera
        Task<Void> renderingTask = camera.getRenderWithCPUCoreParallelizationTask(this.absolutePathToSceneDescription);
        /// bind the rendering Task's progress to the ProgressBar's progress, we will unbind it later when needed
        this.renderingProgressBar.progressProperty().bind(renderingTask.progressProperty());

        /// if the Task failes, check if the XML scene description is invalid, and show an alert if it is really invalid
        renderingTask.setOnFailed(eventHandler -> {
            Throwable exc = renderingTask.getException();

            /// if the XML scene description syntax is incorrect, show an alert and reset to XML scene description loading
            if(exc instanceof IncorrectSceneDescriptionXMLStructureException) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Incorrect XML scene description syntax!");

                alert.showAndWait();

                /// show prompt to load scene description
                try{
                    this.showLoadSceneDescriptionPromptInImageView();
                }
                /// if prompt image file is not found, rethrow an unchecked exception because there is no handling mechanism
                catch(FileNotFoundException fnfe) {
                    throw new RuntimeException("Scene-description-prompt image not found in 'assets' directory.");
                }

                /// reset the progress bar to beginning
                this.resetRenderingProgressBar();
            }
            /// otherwise there is no handling mechanism
            else {
                throw new RuntimeException("Non scene-description-related exception thrown from the rendering Task.");
            }
        });
        /// when the Task executes, (re)load the rendered image in the ImageView
        renderingTask.setOnSucceeded(eventHandler -> {
            try{
                this.showRenderedImageInImageView();
            }
            /// if rendered image file is not found, rethrow an unchecked exception because there is no handling mechanism
            catch(FileNotFoundException fnfe) {
                throw new RuntimeException("Rendered image not found at default location.");
            }
        });

        /// run the rendering Task asynchrously on another thread
        (new Thread(renderingTask)).start();
    }
    /*
       Method that loads the starting image ("Please load
       a scene description") to the ImageView.
     */
    public void showLoadSceneDescriptionPromptInImageView() throws FileNotFoundException {
        this.loadImageFileInImageView("./src/main/resources/assets/load_description_prompt.png");
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
    public void showRenderPromptScreenInImageView() throws FileNotFoundException {
        this.loadImageFileInImageView("./src/main/resources/assets/render_description_prompt.png");
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
       Method that resets the rendering ProgressBar to 0.
     */
    public void resetRenderingProgressBar() {
        /// unbind the progress property of the ProgressBar if it s bound to some property
        this.renderingProgressBar.progressProperty().unbind();
        /// reset the progress
        this.renderingProgressBar.setProgress(0);
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