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
    /// coefficient controlling how much the origins of
    /// shadow rays are lifted from surface of the shape
    private static final double liftingCoefficient = 1e-6;

    /**
     * Constructors
     */
    /*
       Constructor from a point on the surface of a
       RTShape, and a light to which the shadow ray
       is to be cast.

       It also takes the intersected shape so that it
       can lift the intersection point up, away from
       the surface in the direction of the local unit
       normal to prevent the shadow ray from intersecting
       the original RTShape (light source would be occluded
       immediately).

       Used for basic shadow rendering - the target
       point is the position of the light source.
     */
    public ShadowRay(Vector3D intersectionPoint, RTShape intersectedShape, Light light) {
        super(ShadowRay.liftOrigin(intersectionPoint, intersectedShape), light.getPosition().added(ShadowRay.liftOrigin(intersectionPoint, intersectedShape).negated()).normalised());
        this.target = light.getPosition();
    }
    /*
       Constructor from a point on the surface of a
       RTShape, and any other given point to which the
       shadow ray is to be cast.

       It also takes the intersected shape so that it
       can lift the intersection point up, away from
       the surface in the direction of the local unit
       normal to prevent the shadow ray from intersecting
       the original RTShape (light source would be occluded
       immediately).

       Used for distributed ray tracing, where light
       sources are distributed over spheres. We sample
       a random point on the sphere (light source), cast
       a shadow ray to it, and set the shadow intensity
       to the proportion of occluded shadow rays, to
       render soft shadows.
     */
    public ShadowRay(Vector3D intersectionPoint, RTShape intersectedShape, Vector3D target) {
        super(ShadowRay.liftOrigin(intersectionPoint, intersectedShape), target.added(ShadowRay.liftOrigin(intersectionPoint, intersectedShape).negated().normalised()));
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
            if(intersection != null) {
                double distanceHit = super.distance(intersection);
                double distanceTarget = super.distance(target);
                /// also say the light source is occluded if the intersection
                /// point is sufficiently (1e-12) close to the target, to avoid
                /// "pointy" shadow artifacts due to precision errors (similar
                /// to why we lift origins of shadow rays up from the surface)
                if (distanceHit < distanceTarget || Math.abs(distanceHit - distanceTarget) < 1e-12) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Static Utility Methods
     */
    /*
       Method that takes the point on the surface of the given shape, and lifts it
       up in the direction of the local unit normal to surface by a small amount
       defined by the liftingCoefficient class variable.

       It is used to move the origin of the shadow ray away from the surface so that
       it does not intersect 'another' object immediately and hence make the light
       source always occluded.
     */
    public static Vector3D liftOrigin(Vector3D intersectionPoint, RTShape intersectedShape) {
        return intersectionPoint.added(intersectedShape.getUnitNormalAt(intersectionPoint).scaled(ShadowRay.liftingCoefficient));
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
