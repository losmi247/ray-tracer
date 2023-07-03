package tracing;

import shapes.RTShape;
import utility.Vector3D;

import java.util.ArrayList;

/**
 * A class for a shadow ray to be cast from a
 * given point on the surface of a RTShape, to a
 * given light source.
 *
 * Each ShadowRay has a target point which it has
 * to reach before hitting any other object so that
 * the light source can illuminate the point where
 * the ShadowRay originates from.
 */

public class ShadowRay extends Ray {
    private final Vector3D target;

    /**
     * Constructors
     */
    /*
       Constructor from a point on the surface of a
       RTShape, and a light to which the shadow ray
       is to be cast.

       Used for basic shadow rendering - the target
       point is the position of the light source.
     */
    public ShadowRay(Vector3D point, Light light) {
        super(point, light.getPosition().added(point.negated()).normalised());
        this.target = light.getPosition();
    }
    /*
       Constructor from a point on the surface of a
       RTShape, and any other given point to which the
       shadow ray is to be cast.

       Used for distributed ray tracing, where light
       sources are distributed over spheres. We sample
       a random point on the sphere (light source), cast
       a shadow ray to it, and set the shadow intensity
       to the proportion of occluded shadow rays, to
       render soft shadows.
     */
    public ShadowRay(Vector3D point, Vector3D target) {
        super(point, target.added(point.negated().normalised()));
        this.target = target;
    }

    /**
     * Methods
     */
    /*
       Method that casts this ray from its origin and
       checks if it intersects any other shape in the
       scene before reaching the 'target' point.

       Returns 'true' iff the light source is occluded.
     */
    public boolean lightSourceOccluded(Scene scene) {
        ArrayList<RTShape> shapes = scene.getShapes();

        /// check intersections of this ray with each shape
        for (RTShape shape : shapes) {
            /// find the first intersection of this ray and this shape
            Vector3D intersection = shape.intersect(this);

            /// if the intersection point is closer to ray origin than
            /// target, the light source is occluded
            if(intersection != null && super.distance(intersection) < super.distance(target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Getters
     */
    public Vector3D getTarget() {
        return this.target;
    }
    /*
       Gets the point on the surface of a RTShape
       from which the shadow ray in cast.
     */
    public Vector3D getPoint() {
        return super.getOrigin();
    }
    public Vector3D getDirection() {
        return super.getDirection();
    }
}
