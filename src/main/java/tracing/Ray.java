package tracing;

import utility.Vector3D;

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
        Returns the point on this ray P = O + s * D for
        the given value of the non-negative parameter s.
     */
    public Vector3D pointAt(double s){
        return this.origin.added(this.direction.scaled(s));
    }
    /*
        Returns a new ray obtained from reflecting this ray
        against the given 'point' on a surface with local
        unit normal vector 'normal'.
    */
    public Ray reflectedRay(Vector3D point, Vector3D normal){
        Vector3D perfectReflectionDirection = this.direction.negated().reflected(normal);
        return new Ray(point, perfectReflectionDirection);
    }

    /**
     * Getters
     */
    public Vector3D getOrigin(){
        return this.origin;
    }
    public Vector3D getDirection(){
        return this.direction;
    }
}
