package tracing;

import shapes.RTShape;
import utility.Vector3D;

/**
 * Class for an intersection, consisting of the
 * shape that is hit, and a point on its surface
 * where it is hit.
 *
 * When the intersection does not exist, both
 * fields are null.
 */

public class Intersection {
    private final RTShape intersectedShape;
    private final Vector3D intersectionPoint;

    /**
     * Constructors
     */
    public Intersection(RTShape shape, Vector3D point) {
        this.intersectedShape = shape;
        this.intersectionPoint = point;
    }

    /**
     * Methods
     */
    /*
       Method to check whether the intersection exists, i.e.
       whether the fields are null.
     */
    public boolean intersectionExists() {
        return this.intersectedShape != null && this.intersectionPoint != null;
    }

    /**
     * Getters
     */
    public RTShape getIntersectedShape() {
        return this.intersectedShape;
    }

    public Vector3D getIntersectionPoint() {
        return this.intersectionPoint;
    }
}
