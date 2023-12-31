package rendering.utility;

import rendering.shapes.*;

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
    private static final List<String> allExistingShapeIDs = List.of("sphere", "plane", "triangle", "triangle-mesh", "box-mesh");

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
        return switch (shapeID) {
            /// polygonal meshes are the only RTShape's that can have a "model-transform" attributes in their XML nodes
            case "triangle-mesh" -> TriangleMesh.parseShape(leafAttributes, modelTransformation);
            case "box-mesh" -> Box.parseShape(leafAttributes, modelTransformation);


            /// other RTShape implementations are not allowed to have a "model-transform" attribute
            case "triangle" -> Triangle.parseShape(leafAttributes);
            case "sphere" -> Sphere.parseShape(leafAttributes);
            case "plane" -> Plane.parseShape(leafAttributes);


            /// nonexistent shapeID
            default ->
                    throw new IncorrectSceneDescriptionXMLStructureException("Undefined RTShape referenced in scene description.");
        };
    }
}
