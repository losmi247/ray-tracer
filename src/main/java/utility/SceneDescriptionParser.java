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
            throw new IncorrectSceneDescriptionXMLStructureException("Missing XML node for shapes.");
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
            throw new IncorrectSceneDescriptionXMLStructureException("Missing XML node for lights.");
        }

        return lightsNode;
    }
    /*
       Method to parse the XML node containing shape descriptions.
     */
    public ArrayList<RTShape> parseShapes() throws IncorrectSceneDescriptionXMLStructureException, IOException {
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

       Every RTShape's XML node will have a couple of sub-nodes
       that do not have any children but only a value (e.g. center
       of sphere, color etc.). Only polygonal meshes (such as
       TriangleMesh) are allowed to have an additional attribute
       "model-transform" that has many XML child nodes which make
       up a list of transformations to be performed to obtain the
       mesh in world coordinates. If the "model-transform" attribute
       is missing in a polygonal mesh, the modelling transformation
       will be set to the identity matrix.
     */
    private static RTShape parseShape(Node shapeNode) throws IncorrectSceneDescriptionXMLStructureException, IOException {
        NodeList attributeNodes = shapeNode.getChildNodes();

        /// extract the XML node name, same as class variable shapeID in this RTShape's class
        String shapeID = shapeNode.getNodeName();
        /// check if this RTShape is a polygonal mesh
        boolean isMesh = shapeID.endsWith("mesh");
        /// if this RTShape is a mesh, variable below will contain the parsed modelling transform
        /// in case the "model-transform" attribute is missing for a polygonal mesh, the modelling
        /// transformation is set to identity.
        Matrix4D modelTransformation = Matrix4D.identity;

        int n = attributeNodes.getLength();
        Node currentNode;
        /// attributes (and values) of this RTShape's node that only contain a String value, no XML child nodes
        Map<String,String> leafAttributes = new HashMap<>();
        for(int i = 0; i < n; i++) {
            currentNode = attributeNodes.item(i);
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                /// if this attribute is not a leaf node (i.e. has XML child nodes), it must be the "model-transform"
                if(!SceneDescriptionParser.isLeafNode(currentNode)) {
                    /// the non-leaf node must be the "model-transform"
                    if(!currentNode.getNodeName().equals("model-transform")) {
                        throw new IncorrectSceneDescriptionXMLStructureException("There is non-leaf node in an RTShape description, that is not a 'model-transform'.");
                    }

                    /// only polygonal meshes are allowed to have a "model-transform" attribute
                    if(!isMesh) {
                        throw new IncorrectSceneDescriptionXMLStructureException("Non-polygonal-mesh RTShape has a 'model-transform' attribute.");
                    }
                    /// otherwise parse the modelling transformations
                    else {
                        modelTransformation = SceneDescriptionParser.parseTransformations(currentNode);
                    }
                }
                /// otherwise, just parse the value of the leaf attribute
                else {
                    leafAttributes.put(currentNode.getNodeName(), currentNode.getTextContent());
                }
            }
        }

        return ShapeMapper.mapParseShapeMethod(leafAttributes, modelTransformation, shapeID);
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

        if(positionNode == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing light 'position' attribute.");
        }
        else if(colorNode == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing light 'color' attribute.");
        }
        else if(intensityNode == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing light 'intensity' attribute.");
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
            throw new IncorrectSceneDescriptionXMLStructureException("Vector3D does not have exactly three components.");
        }
        return new Vector3D(Double.parseDouble(components[0]), Double.parseDouble(components[1]), Double.parseDouble(components[2]));
    }
    /*
       Method to parse a RTColor object represented as a string
       in the form (r,g,b) where r,g,b,a are integers from 0 to 255 inclusive.
     */
    public static RTColor parseColor(String s) throws IncorrectSceneDescriptionXMLStructureException {
        String[] components = s.substring(1, s.length()-1).split(",");
        if(components.length != 3) {
            throw new IncorrectSceneDescriptionXMLStructureException("RTColor does not have exactly three components.");
        }
        return new RTColor(Integer.parseInt(components[0]), Integer.parseInt(components[1]), Integer.parseInt(components[2]));
    }
    /*
       Method to parse modelling transformations from the XML node named "model-transform" which
       contains child XML nodes that represent modelling transformations (scale, rotateX, rotateY,
       rotateZ, translate). This method represents all the given transformations as matrices,
       and multiplies them in correct order to give the final modelling transformation.
     */
    private static Matrix4D  parseTransformations(Node modelTransformationNode) throws IncorrectSceneDescriptionXMLStructureException {
        /// if "model-transform" node contains no transformations, return 4x4 identity matrix
        Matrix4D modelTransformation = Matrix4D.identity;

        NodeList modelChildrenList = modelTransformationNode.getChildNodes();
        int modelChildrenListLength = modelChildrenList.getLength();
        Node current;
        for(int i = 0; i < modelChildrenListLength; i++) {
            current = modelChildrenList.item(i);
            if(current.getNodeType() == Node.ELEMENT_NODE) {
                /// parse the current transformation in the list
                String nodeName = current.getNodeName();
                String nodeValue = current.getTextContent();

                Matrix4D matrixRepresentation = Matrix4D.identity;
                switch (nodeName) {
                    /// scale attribute has a Vector3D value as a String
                    case "scale" -> matrixRepresentation = Matrix4D.getScalingMatrix(SceneDescriptionParser.parseVector3D(nodeValue));

                    /// rotate attributes have a real value as a String
                    /// rotation about x-axis
                    case "rotateX" -> matrixRepresentation = Matrix4D.getRotationMatrixAboutX(Double.parseDouble(nodeValue));
                    /// rotation about y-axis
                    case "rotateY" -> matrixRepresentation = Matrix4D.getRotationMatrixAboutY(Double.parseDouble(nodeValue));
                    /// rotation about z-axis
                    case "rotateZ" -> matrixRepresentation = Matrix4D.getRotationMatrixAboutZ(Double.parseDouble(nodeValue));

                    /// translate attributes have a Vector3D value as a String
                    case "translate" -> matrixRepresentation = Matrix4D.getTranslationMatrix(SceneDescriptionParser.parseVector3D(nodeValue));
                }

                modelTransformation = modelTransformation.multiply(matrixRepresentation);
            }
        }

        return modelTransformation;
    }
    /*
       Method to check if a given XML node is a leaf node, i.e.
       contains only a String value, and has no XML child nodes.
     */
    private static boolean isLeafNode(Node node) {
        NodeList childNodesList = node.getChildNodes();
        int childNodesListLength = childNodesList.getLength();

        Node currentNode;
        for(int i = 0; i < childNodesListLength; i++) {
            currentNode = childNodesList.item(i);
            /// if node has at least one child XML node, it's not a leaf node
            if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
                return false;
            }
        }

        return true;
    }
}
