package shapes;

import shading.Material;
import utility.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class for a box RTShape. It extends the
 * triangleMesh because it is formed using 12 triangles.
 *
 * All Boxes are flat shaded by default (no interpolation
 * of normals).
 *
 * All Box's are by inheritance also TriangleMesh's so the
 * shapeID of Box must end with "mesh" so that the ShapeMapper
 * can pass the modelling transform to it when invoking
 * parseShape (Box's can therefore have a modelling transform).
 *
 * Box inherits all implementations of RTShape interface methods
 * except for getShapeID (intersect, getUnitNormalAt, getColorAt,
 * getMaterialAt).
 */

public class Box extends TriangleMesh implements RTShape {
    public static final String shapeID = "box-mesh";

    /**
     * Constructors
     */
    /*
       Constructor from minimum and maximum coordinates (creates a box whose sides are
       parallel to the three axes), modelling transformation to place the box in the
       scene, color and material.
     */
    public Box(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, Matrix4D modelTransformation, RTColor color, Material material) {
        super(Box.createVertices(minX, maxX, minY, maxY, minZ, maxZ), Box.createVertexNormals(),
                new int[]{0,1,3, 0,3,2, 6,4,5, 6,5,7, 1,7,5, 1,5,3, 0,2,4, 0,4,6, 2,3,5, 2,5,4, 0,6,7, 0,7,1},
                /*
                No need for vertex normals indices, boxes are always flat shaded.
                new int[]{1,1,1, 1,1,1, 0,0,0, 0,0,0, 4,4,4, 4,4,4, 5,5,5, 5,5,5, 2,2,2, 2,2,2, 3,3,3, 3,3,3},
                 */
                modelTransformation,
                color, material);
    }

    /**
     * Static Utility Methods
     */
    /*
       Method that parses a Box from a Map<String,String>
       mapping attribute names to their values.

       Each Box is defined by a "minX",
       "maxX", "minY", "maxY", "minZ", "maxZ" attributes for
       limits of coordinates, and "model-transform" attribute.
       "color", and "material" are leaf
       attributes (only contain a String value).

       The "model-transform" attribute is an XML sub-node that
       contains a list of transformations in arbitrary order
       (when creating XML description file, you should do scaling, then rotation,
       then translation):

            "scale" - scale by 3 given coefficients for 3 axis (as Vector3D)
            "rotateX" - rotate about x-axis by given angle in degrees
            "rotateY" - rotate about y-axis by given angle in degrees
            "rotateZ" - rotate about z-axis by given angle in degrees
            "translate" - translate by given Vector3D

       In the SceneDescriptionParser, each of these transformations is
       parsed into a Matrix4D, they are multiplied in correct order
       (so that they are performed in the order in which they are written
       in the XML), and the final Matrix4D is passed as the value of the
       "model-transform" attribute. This final matrix is passed as an
       argument to the parseShape method below. Only polygonal meshes
       are allowed to include the 'Matrix4D modelTransformation' argument in
       the signature of their parseShape method, because they are the
       only RTShape's that are allowed to have a "model-transform" attribute.
       If the "model-transform" attribute is missing from the XML description
       of the triangle mesh, the modelling transformation is set to identity
       when passed to the method below.

       If the material attribute is missing from the XML
       description of the RTShape, Material.defaultNonReflectiveMaterial
       is set.

     */
    public static Box parseShape(Map<String, String> leafAttributes, Matrix4D modelTransformation) throws IncorrectSceneDescriptionXMLStructureException {
        Integer minX = null, maxX = null, minY = null, maxY = null, minZ = null, maxZ = null;
        /// model transformation has already been parsed by SceneDescriptionParser
        RTColor color = null;
        Material material = null;

        for (Map.Entry<String, String> entry : leafAttributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            switch (attributeName) {
                case "minX" -> minX = Integer.parseInt(attributeValue);
                case "maxX" -> maxX = Integer.parseInt(attributeValue);
                case "minY" -> minY = Integer.parseInt(attributeValue);
                case "maxY" -> maxY = Integer.parseInt(attributeValue);
                case "minZ" -> minZ = Integer.parseInt(attributeValue);
                case "maxZ" -> maxZ = Integer.parseInt(attributeValue);

                /// model transformation has already been parsed by SceneDescriptionParser

                case "color" -> color = SceneDescriptionParser.parseColor(attributeValue);
                case "material" ->
                {
                    /// first try to parse the material from name, then try to parse from description
                    try {
                        material = Material.parseMaterialFromName(attributeValue);
                    }
                    catch (IncorrectSceneDescriptionXMLStructureException e) {
                        material = Material.parseMaterial(attributeValue);
                    }
                }
                default -> throw new IncorrectSceneDescriptionXMLStructureException("Undefined attribute in Box description.");
            }
        }

        if(minX == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'minX' attribute in Box description.");
        }
        if(maxX == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'maxX' attribute in Box description.");
        }
        if(minY == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'minY' attribute in Box description.");
        }
        if(maxY == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'maxY' attribute in Box description.");
        }
        if(minZ == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'minZ' attribute in Box description.");
        }
        if(maxZ == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'maxZ' attribute in Box description.");
        }
        else if(color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'color' attribute in Box description.");
        }

        /// check if bounds are valid
        if(minX  > maxX) {
            throw new IncorrectSceneDescriptionXMLStructureException("Attribute 'minX' is larger than attribute 'maxX' in Box description.");
        }
        if(minY  > maxY) {
            throw new IncorrectSceneDescriptionXMLStructureException("Attribute 'minY' is larger than attribute 'maxY' in Box description.");
        }
        if(minZ  > maxZ) {
            throw new IncorrectSceneDescriptionXMLStructureException("Attribute 'minZ' is larger than attribute 'maxZ' in Box description.");
        }

        /// if missing material in XML, set default
        if(material == null) {
            material = Material.defaultNonReflectiveMaterial;
        }

        return new Box(minX, maxX, minY, maxY, minZ, maxZ, modelTransformation, color, material);
    }
    /*
       Method that creates the list of vertices of the box mesh given the limits for coordinates.
     */
    private static ArrayList<Vector3D> createVertices(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        ArrayList<Vector3D> vertices = new ArrayList<>();
        vertices.add(new Vector3D(minX, minY, minZ));
        vertices.add(new Vector3D(minX, minY, maxZ));

        vertices.add(new Vector3D(minX, maxY, minZ));
        vertices.add(new Vector3D(minX, maxY, maxZ));

        vertices.add(new Vector3D(maxX, maxY, minZ));
        vertices.add(new Vector3D(maxX, maxY, maxZ));

        vertices.add(new Vector3D(maxX, minY, minZ));
        vertices.add(new Vector3D(maxX, minY, maxZ));

        return vertices;
    }
    /*
       Method that creates the list of vertex normals of the vertices in the box mesh.
     */
    private static ArrayList<Vector3D> createVertexNormals() {
        ArrayList<Vector3D> vertexNormals = new ArrayList<>();
        vertexNormals.add(new Vector3D(1,0,0));
        vertexNormals.add(new Vector3D(-1,0,0));

        vertexNormals.add(new Vector3D(0,1,0));
        vertexNormals.add(new Vector3D(0,-1,0));

        vertexNormals.add(new Vector3D(0,0,1));
        vertexNormals.add(new Vector3D(0,0,-1));

        return vertexNormals;
    }

    /**
     * Getters
     */
    /*
       Override the getShapeID method to return the correct
       shape identifier, for ShapeMapper to use.
     */
    public String getShapeID() {
        return Box.shapeID;
    }
}
