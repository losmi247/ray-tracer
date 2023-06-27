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
 */
public interface Shape {
    /**
     * Method that returns the point (as a Vector3D)
     * of intersection of the Ray r with the Shape,
     * or throws the checked exception NoIntersection
     * if there is no intersection.
     */
    Vector3D intersect(Ray r);
}