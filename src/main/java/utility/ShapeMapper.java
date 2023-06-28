package utility;

import shapes.RTShape;
import shapes.Sphere;

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
    private static final List<String> allExistingShapeIDs = List.of("sphere");

    /**
     * Methods
     */
    /*
       Method to invoke the correct parseShape(Map<> attributes)
       method given a map mapping attribute names to their values.

       Throws IncorrectSceneDescriptionXMLStructureException in case
       the shapeID argument is not a shapeID of an existing primitive.
     */
    public static RTShape mapParseShapeMethod(Map<String,String> attributes, String shapeID) throws IncorrectSceneDescriptionXMLStructureException {
        if(shapeID.equals("sphere")) {
            return Sphere.parseShape(attributes);
        }
        throw new IncorrectSceneDescriptionXMLStructureException();
    }
}
