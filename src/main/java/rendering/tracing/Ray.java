package rendering.tracing;

import rendering.shading.Material;
import rendering.shading.Shader;
import rendering.shapes.RTShape;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import java.util.ArrayList;

/**
 * Class to encapsulate a ray in the
 * form P = O + s * D (s is a non-negative
 * real number) where O is the origin
 * of the ray (point as a Vector3D) and
 * D is the direction (as a Vector3D).
 * The ray is immutable.
 */
public class Ray {
    private final Vector3D origin;
    private final Vector3D direction;
    /// coefficient controlling how much the origins of
    /// shadow rays and reflected rays are lifted from
    /// surface of the shape to prevent them from
    /// intersecting the shape they originated from
    private static final double liftingCoefficient = 1e-6;

    /**
     * Constructors
     */
    public Ray(Vector3D origin, Vector3D direction){
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * Methods
     */
    /*
       Method to cast and trace a ray, and find resulting color
       value contribution of this ray, given a scene.

       No further tracing is done in this method (e.g. no
       reflections), only the first intersection is used
       for shading.
     */
    public RTColor trace(Scene scene, Shader shader) {
        /// first find the first intersection of this ray and this scene
        Intersection firstIntersection = this.findFirstIntersection(scene);

        /// the ray does not intersect any RTShape
        if(firstIntersection == null) {
            /// TODO - decide what background to return if no intersection
            return RTColor.backgroundColor;
        }

        /// TODO - recursive tracing (reflections/refractions)
        return shader.evaluateShadingModel(firstIntersection);
    }
    /*
       Method to cast and trace ray, but also recursively trace its
       first 'tracingLimit' reflections off of RTShapes.
     */
    public RTColor traceWithReflections(Scene scene, Shader shader, int tracingLimit) {
        if(tracingLimit == 0) {
            return RTColor.blank;
        }

        /// first find the first intersection of this ray and this scene
        Intersection firstIntersection = this.findFirstIntersection(scene);

        /// the ray does not intersect any RTShape
        if(firstIntersection == null) {
            /// TODO - decide what background to return if no intersection
            return RTColor.backgroundColor;
        }

        /// now firstIntersection != null
        /// calculate contribution of this intersection point
        RTColor intersectionContribution = shader.evaluateShadingModel(firstIntersection);

        /// reflect the ray
        Vector3D normal = firstIntersection.getIntersectedShape().getUnitNormalAt(firstIntersection);
        Ray reflectedRay = this.reflectedRay(firstIntersection);
        /// calculate contribution of the reflected ray
        RTColor reflectionContribution = reflectedRay.traceWithReflections(scene, shader, tracingLimit - 1);

        /// mix the two contributions using the reflection coefficient of the intersected RTShape
        Material material = firstIntersection.getIntersectedShape().getMaterialAt(firstIntersection.getIntersectionPoint());
        return shader.mixReflectedColor(intersectionContribution, reflectionContribution, material.getReflectionCoefficient());
    }
    /*
       Method to find the first intersection of this ray with a RTShape from
       the given Scene object. Returns an Intersection object.
     */
    public Intersection findFirstIntersection(Scene scene) {
        ArrayList<RTShape> shapes = scene.getShapes();
        ArrayList<Light> lights = scene.getLights();

        /// find the first intersection of this ray with a shape
        Intersection closestIntersection = null;
        double minDistanceSoFar = -1;
        for (RTShape shape : shapes) {
            /// find the first intersection of this ray and this shape
            Intersection intersection = shape.intersect(this);
            if(intersection != null) {
                if(closestIntersection == null || this.distance(intersection.getIntersectionPoint()) < minDistanceSoFar) {
                    closestIntersection = intersection;
                    minDistanceSoFar = this.distance(intersection.getIntersectionPoint());
                }
            }
        }

        return closestIntersection;
    }
    /*
       Method to return the point on this ray P = O + s * D for
       the given value of the non-negative parameter s.
     */
    public Vector3D pointAt(double s){
        return this.origin.added(this.direction.scaled(s));
    }
    /*
       Method to return a new ray obtained by reflecting this ray
       against a given 'point' on the surface of a RTShape, i.e.
       a given Intersection.
    */
    public Ray reflectedRay(Intersection intersection){
        Vector3D point = intersection.getIntersectionPoint();
        Vector3D normal = intersection.getIntersectedShape().getUnitNormalAt(intersection);
        Vector3D perfectReflectionDirection = this.direction.negated().reflected(normal.normalised());
        return new Ray(Ray.liftOrigin(intersection), perfectReflectionDirection);
    }
    /*
       Method to get the distance from the origin of the ray
       to a particular point (as a Vector3D).
     */
    public double distance(Vector3D point) {
        return point.added(this.origin.negated()).magnitude();
    }

    /**
     * Static Utility Methods
     */
    /*
       Method that takes the intersection point on the surface of a shape, and lifts
       it up in the direction of the local unit normal to surface by a small amount
       defined by the liftingCoefficient class variable.

       It is used to move the origin of a shadow ray or a reflected ray away from the
       surface so that it does not intersect the object it originated from
       immediately and hence make the light source always occluded, or make the
       reflected ray return a wrong color value.
     */
    public static Vector3D liftOrigin(Intersection intersection) {
        RTShape intersectedShape = intersection.getIntersectedShape();
        Vector3D intersectionPoint = intersection.getIntersectionPoint();
        return intersectionPoint.added(intersectedShape.getUnitNormalAt(intersection).scaled(Ray.liftingCoefficient));
    }

    /**
     * Getters
     */
    public Vector3D getOrigin(){
        return new Vector3D(this.origin);
    }
    public Vector3D getDirection(){
        return new Vector3D(this.direction);
    }
}
