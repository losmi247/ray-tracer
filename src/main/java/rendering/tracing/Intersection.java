package rendering.tracing;

import rendering.shapes.RTShape;
import rendering.utility.Vector3D;

/**
 * Class for an intersection, consisting of the
 * shape that is hit, and a point on its surface
 * where it is hit.
 *
 * When the intersection does not exist, the
 * variable that's supposed to be the Intersection
 * object is null.
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
     * Getters
     */
    public RTShape getIntersectedShape() {
        return this.intersectedShape;
    }

    public Vector3D getIntersectionPoint() {
        return this.intersectionPoint;
    }
}
