package shapes;
import utility.Vector3D;
import tracing.Ray;

/**
 * Interface for a shape that can be included in
 * the XML scene description.
 *
 * Apart from the methods in this interface, each shape
 * should have a unique String identifier same as its
 * corresponding XML element name.
 *
 * EACH SHAPE MUST ALSO HAVE A
 *      public static RTShape parseShape(Map<String,String> attributes){}
 * TO PARSE THE SHAPE FROM A MAP MAPPING ITS ATTRIBUTES NAMES
 * TO THEIR VALUES.
 */
public interface RTShape {
    /*
       Method that returns the point (as a Vector3D)
       of intersection of the Ray r with the RTShape,
       or throws the checked exception NoIntersection
       if there is no intersection.
     */
    Vector3D intersect(Ray ray);
}