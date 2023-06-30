package utility;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import shapes.RTShape;
import tracing.Light;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to parse the XML description of the scene.
 *
 * The XML scene description should have the structure as in the
 * example 'scene.xml'. The root element is 'elements', all
 * shapes are enclosed in a 'shapes' element within it, and all
 * lights are enclosed in a 'lights' element within it.
 *
 * Checked exceptions from used libraries are always passed up.
 * If incorrect structure of the XML scene description is detected,
 * IncorrectSceneDescriptionXMLStructureException is thrown.
 */
public class SceneDescriptionParser {
    private final Document document;

    /**
     * Constructors
     */
    /*
       Constructor from a path from the project root folder
       to the scene description XML.
       Creates a Document object to use later to navigate the XML file.
     */
    public SceneDescriptionParser(String sceneDescriptionPath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.document = builder.parse(new File(sceneDescriptionPath));
        this.document.getDocumentElement().normalize();
    }

    /**
     * Methods
     */
    /*
       Method to extract the "shapes" XML node containing shape
       descriptions, from the root node named "elements".
     */
    private Node getShapesNode() throws IncorrectSceneDescriptionXMLStructureException {
        Node rootNode = this.document.getElementsByTagName("elements").item(0);
        NodeList rootChildrenList = rootNode.getChildNodes();

        Node shapesNode = null;
        Node currentNode;
        for(int i = 0; i < rootChildrenList.getLength(); i++) {
            currentNode = rootChildrenList.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                if(currentNode.getNodeName().equals("shapes")) {
                    shapesNode = currentNode;
                }
            }
        }

        if(shapesNode == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        return shapesNode;
    }
    /*
       Method to extract the "lights" XML node containing light
       descriptions, from the root node named "elements".
     */
    private Node getLightsNode() throws IncorrectSceneDescriptionXMLStructureException {
        Node rootNode = this.document.getElementsByTagName("elements").item(0);
        NodeList rootChildrenList = rootNode.getChildNodes();

        Node lightsNode = null;
        Node currentNode;
        for(int i = 0; i < rootChildrenList.getLength(); i++) {
            currentNode = rootChildrenList.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                if(currentNode.getNodeName().equals("lights")) {
                    lightsNode = currentNode;
                }
            }
        }

        if(lightsNode == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        return lightsNode;
    }
    /*
       Method to parse the XML node containing shape descriptions.
     */
    public ArrayList<RTShape> parseShapes() throws IncorrectSceneDescriptionXMLStructureException {
        NodeList shapesNodeList = this.getShapesNode().getChildNodes();

        /// iterate through shapes
        int shapesNodeListLength = shapesNodeList.getLength();
        ArrayList<RTShape> shapes = new ArrayList<>();
        Node currentNode;
        for (int i = 0; i < shapesNodeListLength; i++) {
            currentNode = shapesNodeList.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                shapes.add(SceneDescriptionParser.parseShape(currentNode));
            }
        }

        return shapes;
    }
    /*
       Method to parse the XML node containing light descriptions.
     */
    public ArrayList<Light> parseLights() throws IncorrectSceneDescriptionXMLStructureException {
        NodeList lightsNodeList = this.getLightsNode().getChildNodes();

        /// iterate through lights
        int lightsNodeListLength = lightsNodeList.getLength();
        ArrayList<Light> lights = new ArrayList<>();
        Node currentNode;
        for (int i = 0; i < lightsNodeListLength; i++) {
            currentNode = lightsNodeList.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                lights.add(SceneDescriptionParser.parseLight(currentNode));
            }
        }

        return lights;
    }

    /**
     * Static Utility Methods
     */
    /*
       Method to parse a given XML node for a shape, and create
       an appropriate RTShape object.
     */
    private static RTShape parseShape(Node shapeNode) throws IncorrectSceneDescriptionXMLStructureException {
        NodeList attributeNodes = shapeNode.getChildNodes();
        int n = attributeNodes.getLength();
        Node current;
        Map<String,String> attributes = new HashMap<>();
        for(int i = 0; i < n; i++) {
            current = attributeNodes.item(i);
            if(current.getNodeType() == Node.ELEMENT_NODE) {
                attributes.put(current.getNodeName(),current.getTextContent());
            }
        }

        return ShapeMapper.mapParseShapeMethod(attributes, shapeNode.getNodeName());
    }
    /*
       Method to parse a given XML node for a light, and create
       an appropriate Light object.
     */
    private static Light parseLight(Node lightNode) throws IncorrectSceneDescriptionXMLStructureException {
        NodeList lightChildren = lightNode.getChildNodes();
        Node positionNode = null, colorNode = null, intensityNode = null;
        int lightChildrenLength = lightChildren.getLength();
        Node currentNode;
        for(int i = 0; i < lightChildrenLength; i++) {
            currentNode = lightChildren.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                if(currentNode.getNodeName().equals("position")) {
                    positionNode = currentNode;
                }
                if(currentNode.getNodeName().equals("color")) {
                    colorNode = currentNode;
                }
                if(currentNode.getNodeName().equals("intensity")) {
                    intensityNode = currentNode;
                }
            }
        }

        if(positionNode == null || colorNode == null || intensityNode == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        /// parse light position
        String positionStr = positionNode.getTextContent();
        Vector3D position = SceneDescriptionParser.parseVector3D(positionStr);

        /// parse light specular color
        String colorStr = colorNode.getTextContent();
        RTColor color = SceneDescriptionParser.parseColor(colorStr);

        /// parse light intensity
        String intensityStr = intensityNode.getTextContent();
        double intensity = Double.parseDouble(intensityStr);

        return new Light(position, color, intensity);
    }
    /*
       Method to parse a Vector3D object represented as a string
       in the form "(x,y,z)".
     */
    public static Vector3D parseVector3D(String s) throws IncorrectSceneDescriptionXMLStructureException {
        String[] components = s.substring(1, s.length()-1).split(",");
        if(components.length != 3) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }
        return new Vector3D(Double.parseDouble(components[0]), Double.parseDouble(components[1]), Double.parseDouble(components[2]));
    }
    /*
       Method to parse a RTColor object represented as a string
       in the form (r,g,b,a) where r,g,b,a are integers from 0 to 255 inclusive.
     */
    public static RTColor parseColor(String s) throws IncorrectSceneDescriptionXMLStructureException {
        String[] components = s.substring(1, s.length()-1).split(",");
        if(components.length != 4) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }
        return new RTColor(Integer.parseInt(components[0]), Integer.parseInt(components[1]), Integer.parseInt(components[2]), Integer.parseInt(components[3]));
    }
}
