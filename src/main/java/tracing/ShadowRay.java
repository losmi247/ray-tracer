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
       RTShape (i.e. a given Intersection object),
       and a light to which the shadow ray
       is to be cast.

       It uses the intersected shape so that it
       can lift the intersection point up, away from
       the surface in the direction of the local unit
       normal to prevent the shadow ray from intersecting
       the original RTShape (light source would be occluded
       immediately).

       Used for basic shadow rendering - the target
       point is the position of the light source.
     */
    public ShadowRay(Intersection intersection, Light light) {
        super(Ray.liftOrigin(intersection), light.getPosition().added(Ray.liftOrigin(intersection).negated()).normalised());
        this.target = light.getPosition();
    }
    /*
       Constructor from a point on the surface of a
       RTShape (i.e. a given Intersection object),
       and a light to which the shadow ray
       is to be cast.

       It uses the intersected shape so that it
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
    public ShadowRay(Intersection intersection, Vector3D target) {
        super(Ray.liftOrigin(intersection), target.added(Ray.liftOrigin(intersection).negated().normalised()));
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
            Intersection intersectionPoint = shape.intersect(this);

            /// if the intersection point is closer to ray origin than
            /// target, the light source is occluded
            if(intersectionPoint != null) {
                double distanceHit = super.distance(intersectionPoint.getIntersectionPoint());
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
     * Getters
     */
    /*
       Gets the point that the shadow ray is
       supposed to reach before hitting some
       RTShape so that the origin of the
       shadow ray is illuminated by given light
       source.
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
