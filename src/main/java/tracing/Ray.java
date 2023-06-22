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
        Returns this ray reflected against a point on a
        surface with local unit normal vector 'normal'.
    */
    public Vector3D reflectedRay(Vector3D normal, Vector3D point){
        Vector3D perfectReflectionDirection = this.direction.
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
    public
}
