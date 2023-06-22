package shapes;
import utility.Vector3D;
import tracing.Ray;

public interface Shape {
    /**
     * Method that returns the point (as a Vector3D)
     * of intersection of the Ray r with the Shape,
     * or throws the checked exception NoIntersection
     * if there is no intersection.
     */
    Vector3D intersect(Ray r) throws NoIntersection;
}