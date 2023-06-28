package tracing;

import org.xml.sax.SAXException;
import shapes.RTShape;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.SceneDescriptionParser;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class to encapsulate a scene described by
 * an XML file. It contains a list of all shapes, a
 * list of all lights, and the color of the ambient
 * component of the Phong's shading model, as well as
 * ambient component intensity.
 *
 * The scene is immutable.
 */

public class Scene {
    private final ArrayList<RTShape> shapes;
    private final ArrayList<Light> lights;
    private final Color ambientColor;
    private final double ambientComponentIntensity;

    /**
     * Constructors
     */
    /*
       Constructor from a path from the project root folder
       to the scene description XML. Uses the SceneDescriptionParser
       to parse the shapes and lights.

       Sets the ambient color to default black (no ambient component) and
       intensity to 0.
     */
    public Scene(String sceneDescriptionPath) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        SceneDescriptionParser descriptionParser = new SceneDescriptionParser(sceneDescriptionPath);
        this.shapes = descriptionParser.parseShapes();
        this.lights = descriptionParser.parseLights();

        this.ambientColor = Color.BLACK;
        this.ambientComponentIntensity = 0;
    }
    /*
       Constructor from a path from the project root folder
       to the scene description XML. Uses the SceneDescriptionParser
       to parse the shapes and lights.
     */
    public Scene(String sceneDescriptionPath, Color ambientColor, double ambientIntensity) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        SceneDescriptionParser descriptionParser = new SceneDescriptionParser(sceneDescriptionPath);
        this.shapes = descriptionParser.parseShapes();
        this.lights = descriptionParser.parseLights();

        this.ambientColor = ambientColor;
        this.ambientComponentIntensity = ambientIntensity;
    }

    /**
     * Getters
     */
    public ArrayList<RTShape> getShapes() {
        return this.shapes;
    }
    public ArrayList<Light> getLights() {
        return lights;
    }
    public Color getAmbientColor() {
        return this.ambientColor;
    }
    public double getAmbientComponentIntensity() {
        return this.ambientComponentIntensity;
    }
}
