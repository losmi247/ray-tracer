package utility;

import shapes.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A class to keep track of existing ray tracing
 * primitives (e.g. sphere), and map their shapeIDs
 * to their parseShape(Map<> attributes) methods.
 *
 * Each shape's shapeID should just be the lowercase
 * version of its class name.
 */
public class ShapeMapper {
    private static final List<String> allExistingShapeIDs = List.of("sphere", "plane");

    /**
     * Methods
     */
    /*
       Method to invoke the correct parseShape(Map<> attributes)
       method given a map mapping attribute names to their values.

       Throws IncorrectSceneDescriptionXMLStructureException in case
       the shapeID argument is not a shapeID of an existing primitive.
     */
    public static RTShape mapParseShapeMethod(Map<String,String> leafAttributes, Matrix4D modelTransformation, String shapeID) throws IncorrectSceneDescriptionXMLStructureException, IOException {
        /// polygonal meshes are the only RTShape's that can have a "model-transform" attributes in their XML nodes
        if(shapeID.equals("triangle-mesh")) {
            return TriangleMesh.parseShape(leafAttributes, modelTransformation);
        }

        /// other RTShape implementations are not allowed to have a "model-transform" attribute
        else if(shapeID.equals("triangle")) {
            return Triangle.parseShape(leafAttributes);
        }
        else if(shapeID.equals("sphere")) {
            return Sphere.parseShape(leafAttributes);
        }
        else if(shapeID.equals("plane")) {
            return Plane.parseShape(leafAttributes);
        }

        /// nonexistent shapeID
        else {
            throw new IncorrectSceneDescriptionXMLStructureException("Undefined RTShape referenced in scene description.");
        }
    }
}
