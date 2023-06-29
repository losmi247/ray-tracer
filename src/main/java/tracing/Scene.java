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
 * an XML file. It contains a list of all shapes
 * and a list of all lights.
 *
 * The scene is immutable.
 */

public class Scene {
    private final ArrayList<RTShape> shapes;
    private final ArrayList<Light> lights;

    /**
     * Constructors
     */
    /*
       Constructor from a path from the project root folder
       to the scene description XML. Uses the SceneDescriptionParser
       to parse the shapes and lights.
     */
    public Scene(String sceneDescriptionPath) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        SceneDescriptionParser descriptionParser = new SceneDescriptionParser(sceneDescriptionPath);
        this.shapes = descriptionParser.parseShapes();
        this.lights = descriptionParser.parseLights();
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
}
