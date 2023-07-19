package utility;

/**
 * Class to encapsulate an immutable 3D vector
 * in Cartesian coordinates.
 */
public class Vector3D {
    private final double x;
    private final double y;
    private final double z;

    /**
     * Constructors
     */
    public Vector3D(double component1, double component2, double component3) {
        this.x = component1;
        this.y = component2;
        this.z = component3;
    }
    public Vector3D(Vector3D v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    /*
       Constructor that sets all three components
       to the given real value.
     */
    public Vector3D(double a) {
        this.x = a;
        this.y = a;
        this.z = a;
    }

    /**
     * Methods
     */
    public Vector3D added(Vector3D v){
        return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
    }
    public Vector3D scaled(double scalingFactor){
        return new Vector3D(this.x * scalingFactor, this.y * scalingFactor, this.z * scalingFactor);
    }
    public Vector3D normalised(){
        return this.scaled(1 / this.magnitude());
    }
    public Vector3D negated(){
        return this.scaled(-1);
    }
    public double scalarProduct(Vector3D v){
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }
    /*
        Returns this vector reflected against a point on a
        surface with local unit normal vector 'normal'.
        In other words, returns a vector 'R' in the same plane
        as 'this' and 'normal' so that angle from 'this' to
        'normal' is the same as the angle from 'normal' to
        that 'R'.

        The argument 'normal' isn't necessarily normalised.
        This method normalises the returned vector.
    */
    public Vector3D reflected(Vector3D normal){
        Vector3D unitNormal = normal.normalised();
        return unitNormal.scaled(2 * this.scalarProduct(unitNormal)).added(this.negated()).normalised();
    }
    /*
       Returns the vector cross product (this X v).
     */
    public Vector3D crossProduct(Vector3D v) {
        return new Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
    }
    public double magnitude(){
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    public double magnitudeSquared() {
        return this.scalarProduct(this);
    }

    /** Getters
     */
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public double getZ(){
        return this.z;
    }
}
