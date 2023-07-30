package utility;

/**
 * Class for 4x4 matrices which are used to
 * represent modelling transformations for
 * triangle meshes (transforming from object
 * to world coordinates, i.e. positioning the
 * mesh in the scene).
 */

public class Matrix4D {
    /// matrix elements
    private final double m00;
    private final double m01;
    private final double m02;
    private final double m03;
    private final double m10;
    private final double m11;
    private final double m12;
    private final double m13;
    private final double m20;
    private final double m21;
    private final double m22;
    private final double m23;
    private final double m30;
    private final double m31;
    private final double m32;
    private final double m33;

    /// identity matrix
    public static final Matrix4D identity = new Matrix4D(1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1);

    /**
     * Constructors
     */
    /*
       Constructor from all elements.
     */
    public Matrix4D(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    /**
     * Methods
     */
    /*
       Method to multiply this matrix by another 4x4 matrix m from the right side.
     */
    public Matrix4D multiplyFromRight(Matrix4D m) {
        return new Matrix4D(
                m00*m.m00 + m01*m.m10 + m02*m.m20 + m03*m.m30,
                m00*m.m01 + m01*m.m11 + m02*m.m21 + m03*m.m31,
                m00*m.m02 + m01*m.m12 + m02*m.m22 + m03*m.m32,
                m00*m.m03 + m01*m.m13 + m02*m.m23 + m03*m.m33,

                m10*m.m00 + m11*m.m10 + m12*m.m20 + m13*m.m30,
                m10*m.m01 + m11*m.m11 + m12*m.m21 + m13*m.m31,
                m10*m.m02 + m11*m.m12 + m12*m.m22 + m13*m.m32,
                m10*m.m03 + m11*m.m13 + m12*m.m23 + m13*m.m33,

                m20*m.m00 + m21*m.m10 + m22*m.m20 + m23*m.m30,
                m20*m.m01 + m21*m.m11 + m22*m.m21 + m23*m.m31,
                m20*m.m02 + m21*m.m12 + m22*m.m22 + m23*m.m32,
                m20*m.m03 + m21*m.m13 + m22*m.m23 + m23*m.m33,

                m30*m.m00 + m31*m.m10 + m32*m.m20 + m33*m.m30,
                m30*m.m01 + m31*m.m11 + m32*m.m21 + m33*m.m31,
                m30*m.m02 + m31*m.m12 + m32*m.m22 + m33*m.m32,
                m30*m.m03 + m31*m.m13 + m32*m.m23 + m33*m.m33
        );
    }
    /*
       Method to multiply this matrix by a 4x1 matrix from right side,
       created by extending the given Vector3D to have a 4th element
       at the end which is equal to 1. The result is a new 4x1 matrix,
       which is returned without its last element, i.e. a Vector3D.
     */
    public Vector3D multiplyFromRight(Vector3D v) {
        return new Vector3D(
                m00*v.getX() + m01*v.getY() + m02*v.getZ() + m03,
                m10*v.getX() + m11*v.getY() + m12*v.getZ() + m13,
                m20*v.getX() + m21*v.getY() + m22*v.getZ() + m23//,
                //m30*v.getX() + m31*v.getY() + m32*v.getZ() + m33
        );
    }

    /**
     * Static Utility Methods
     */
    /*
       Method that returns a matrix that represents the
       scaling transformation by 3 given real numbers,
       one for each direction x,y,z, about the origin (0,0,0).
     */
    public static Matrix4D getScalingMatrix(Vector3D scalingCoefficients) {
        return new Matrix4D(
                scalingCoefficients.getX(), 0, 0, 0,
                0, scalingCoefficients.getY(), 0, 0,
                0, 0, scalingCoefficients.getZ(), 0,
                0, 0, 0, 1
        );
    }
    /*
       Method that returns a matrix that represents the
       rotation transformation by the given angle in
       degrees about x-axis, counterclockwise (y to z).
     */
    public static Matrix4D getRotationMatrixAboutX(double angle) {
        /// convert degrees to radians
        angle = (angle / 180) * Math.PI;
        return new Matrix4D(
                1, 0, 0, 0,
                0, Math.cos(angle), -Math.sin(angle), 0,
                0, Math.sin(angle), Math.cos(angle), 0,
                0, 0, 0, 1
        );
    }
    /*
       Method that returns a matrix that represents the
       rotation transformation by the given angle in
       degrees about y-axis, counterclockwise (z to x).
     */
    public static Matrix4D getRotationMatrixAboutY(double angle) {
        /// convert degrees to radians
        angle = (angle / 180) * Math.PI;
        return new Matrix4D(
                Math.cos(angle), 0, Math.sin(angle), 0,
                0, 1, 0, 0,
                -Math.sin(angle), 0, Math.cos(angle), 0,
                0, 0, 0, 1
        );
    }
    /*
       Method that returns a matrix that represents the
       rotation transformation by the given angle in
       degrees about z-axis, counterclockwise (x to y).
     */
    public static Matrix4D getRotationMatrixAboutZ(double angle) {
        /// convert degrees to radians
        angle = (angle / 180) * Math.PI;
        return new Matrix4D(
                Math.cos(angle), -Math.sin(angle), 0, 0,
                Math.sin(angle), Math.cos(angle), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }
    /*
       Method that returns a matrix that represents the
       translation transformation by the given Vector3D.
     */
    public static Matrix4D getTranslationMatrix(Vector3D translationVector) {
        return new Matrix4D(
                1, 0, 0, translationVector.getX(),
                0, 1, 0, translationVector.getY(),
                0, 0, 1, translationVector.getZ(),
                0, 0, 0, 1
        );
    }

    /**
     * Getters
     */
    public double getM00() {
        return this.m00;
    }
    public double getM01() {
        return this.m01;
    }
    public double getM02() {
        return this.m02;
    }
    public double getM03() {
        return this.m03;
    }
    public double getM10() {
        return this.m10;
    }
    public double getM11() {
        return this.m11;
    }
    public double getM12() {
        return this.m12;
    }
    public double getM13() {
        return this.m13;
    }
    public double getM20() {
        return this.m20;
    }
    public double getM21() {
        return this.m21;
    }
    public double getM22() {
        return this.m22;
    }
    public double getM23() {
        return this.m23;
    }
    public double getM30() {
        return this.m30;
    }
    public double getM31() {
        return this.m31;
    }
    public double getM32() {
        return this.m32;
    }
    public double getM33() {
        return this.m33;
    }
}
