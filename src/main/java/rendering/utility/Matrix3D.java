package rendering.utility;

/**
 * Class for 3x3 matrices which are used to
 * represent modelling transformations for
 * triangle meshes (transforming from object
 * to world coordinates, i.e. positioning the
 * mesh in the scene).
 */

public class Matrix3D {
    /// matrix elements
    private final double m00;
    private final double m01;
    private final double m02;
    private final double m10;
    private final double m11;
    private final double m12;
    private final double m20;
    private final double m21;
    private final double m22;

    /// identity matrix
    public static final Matrix3D identity = new Matrix3D(1, 0, 0,
            0, 1, 0,
            0, 0, 1);

    /**
     * Constructors
     */
    /*
       Constructor from all elements.
     */
    public Matrix3D(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }
    /*
       Constructor from a 4x4 matrix, by taking the upper left 3x3 sub-matrix.
     */
    public Matrix3D(Matrix4D m) {
        this.m00 = m.getM00();
        this.m01 = m.getM01();
        this.m02 = m.getM02();
        this.m10 = m.getM10();
        this.m11 = m.getM11();
        this.m12 = m.getM12();
        this.m20 = m.getM20();
        this.m21 = m.getM21();
        this.m22 = m.getM22();
    }

    /**
     * Methods
     */
    /*
       Method to multiply this matrix by another 3x3 matrix m from the right side.
     */
    public Matrix3D multiplyFromRight(Matrix3D m) {
        return new Matrix3D(
                m00*m.m00 + m01*m.m10 + m02*m.m20,
                m00*m.m01 + m01*m.m11 + m02*m.m21,
                m00*m.m02 + m01*m.m12 + m02*m.m22,

                m10*m.m00 + m11*m.m10 + m12*m.m20,
                m10*m.m01 + m11*m.m11 + m12*m.m21,
                m10*m.m02 + m11*m.m12 + m12*m.m22,

                m20*m.m00 + m21*m.m10 + m22*m.m20,
                m20*m.m01 + m21*m.m11 + m22*m.m21,
                m20*m.m02 + m21*m.m12 + m22*m.m22
        );
    }
    /*
       Method to multiply this matrix by a 3x1 matrix from right side,
       i.e. a Vector3D. The result is a new 3x1 matrix,
       which is returned as a new Vector3D.

       This method is used for applying model transforms to vertex normals.
     */
    public Vector3D multiplyFromRight(Vector3D v) {
        return new Vector3D(
                m00*v.getX() + m01*v.getY() + m02*v.getZ(),
                m10*v.getX() + m11*v.getY() + m12*v.getZ(),
                m20*v.getX() + m21*v.getY() + m22*v.getZ()
        );
    }
    /*
       Method to return a new matrix created by transposing this matrix.
     */
    public Matrix3D transposed() {
        return new Matrix3D(
                this.m00, this.m10, this.m20,
                this.m01, this.m11, this.m21,
                this.m02, this.m12, this.m22
        );
    }
    /*
       Method to return a new matrix created by multiplying all elements
       of this matrix by the given real number.
     */
    public Matrix3D scaled(double x) {
        return new Matrix3D(
                x * m00, x * m01, x * m02,
                x * m10, x * m11, x * m12,
                x * m20, x * m21, x * m22
        );
    }
    /*
       Method to calculate the determinant of this 3x3 matrix.
     */
    public double getDeterminant() {
        return m00*(m11*m22 - m12*m21) - m01*(m10*m22 - m20*m12) + m02*(m10*m21 - m20*m11);
    }
    /*
       Method to calculate the inverse 3x3 matrix  of this 3x3 matrix.
     */
    public Matrix3D getInverse() {
        double determinant = this.getDeterminant();

        if(Math.abs(determinant) < 1e-12) {
            throw new RuntimeException("Singular matrix has no inverse.");
        }

        Matrix3D adjugate = new Matrix3D(
                m11*m22 - m21*m12,
                -m01*m22 + m02*m21,
                m01*m12 - m02*m11,
                -m10*m22 + m12*m20,
                m00*m22 - m20*m02,
                -m00*m12 + m02*m10,
                m10*m21 - m20*m11,
                -m00*m21 + m20*m01,
                m00*m11 - m10*m01
        );

        return adjugate.scaled(1 / determinant);
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
    public double getM10() {
        return this.m10;
    }
    public double getM11() {
        return this.m11;
    }
    public double getM12() {
        return this.m12;
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
}
