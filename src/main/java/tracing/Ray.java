package tracing;

import shading.Shader;
import shapes.RTShape;
import utility.RTColor;
import utility.Vector3D;

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
       reflections).
     */
    public RTColor trace(Scene scene, Shader shader) {
        /// first find the first intersection of this ray and this scene
        Intersection firstIntersection = this.findFirstIntersection(scene);

        /// the ray does not intersect any RTShape
        if(!firstIntersection.intersectionExists()) {
            /// TODO - decide what background to return if no intersection
            return RTColor.backgroundColor;
        }

        /// TODO - recursive tracing (reflections/refractions)
        return shader.evaluateShadingModel(firstIntersection);
    }
    /*
       Method to find the first intersection of this ray with a RTShape from
       the given Scene object. Returns an Intersection object.
     */
    public Intersection findFirstIntersection(Scene scene) {
        ArrayList<RTShape> shapes = scene.getShapes();
        ArrayList<Light> lights = scene.getLights();

        /// find the first intersection of this ray with a shape
        Vector3D intersectionPoint = null;
        double minDistanceSoFar = -1;
        RTShape intersectedShape = null;
        for (RTShape shape : shapes) {
            /// find the first intersection of this ray and this shape
            Vector3D intersection = shape.intersect(this);
            if(intersection != null) {
                if(intersectionPoint == null || this.distance(intersection) < minDistanceSoFar) {
                    intersectionPoint = intersection;
                    minDistanceSoFar = this.distance(intersection);
                    intersectedShape = shape;
                }
            }
        }

        return new Intersection(intersectedShape, intersectionPoint);
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
       against the given 'point' on a surface with local
       unit normal vector 'normal'.
    */
    public Ray reflectedRay(Vector3D point, Vector3D normal){
        Vector3D perfectReflectionDirection = this.direction.negated().reflected(normal);
        return new Ray(point, perfectReflectionDirection);
    }
    /*
       Method to get the distance from the origin of the ray
       to a particular point (as a Vector3D).
     */
    public double distance(Vector3D point) {
        return point.added(this.origin.negated()).magnitude();
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
