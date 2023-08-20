package rendering.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix3DTest {

    @Test
    void testMultiplyFromRightMatrix() {
        Matrix3D v = new Matrix3D(4, 1, 0, -13, -5, 2, 1, 7, 6);
        Matrix3D n = v.multiplyFromRight(new Matrix3D(5, 1, 20, 5, 8, -3, 2, 0.6, 0));
        assertEquals(25, n.getM00(), 1e-9);
        assertEquals(12, n.getM01(), 1e-9);
        assertEquals(77, n.getM02(), 1e-9);
        assertEquals(-86, n.getM10(), 1e-9);
        assertEquals(-51.8, n.getM11(), 1e-9);
        assertEquals(-245, n.getM12(), 1e-9);
        assertEquals(52, n.getM20(), 1e-9);
        assertEquals(60.6, n.getM21(), 1e-9);
        assertEquals(-1, n.getM22(), 1e-9);
    }

    @Test
    void testMultiplyFromRightVector() {
        Matrix3D v = new Matrix3D(4, 1, 0, -13, -5, 2, 1, 7, 6);
        Vector3D n = v.multiplyFromRight(new Vector3D(1,2,3));
        assertEquals(6, n.getX(), 1e-9);
        assertEquals(-17, n.getY(), 1e-9);
        assertEquals(33, n.getZ(), 1e-9);
    }

    @Test
    void transposed() {
        Matrix3D v = new Matrix3D(1,2,3,4,5,6,7,8,9);
        Matrix3D n = v.transposed();
        assertEquals(1, n.getM00(), 1e-9);
        assertEquals(4, n.getM01(), 1e-9);
        assertEquals(7, n.getM02(), 1e-9);
        assertEquals(2, n.getM10(), 1e-9);
        assertEquals(5, n.getM11(), 1e-9);
        assertEquals(8, n.getM12(), 1e-9);
        assertEquals(3, n.getM20(), 1e-9);
        assertEquals(6, n.getM21(), 1e-9);
        assertEquals(9, n.getM22(), 1e-9);
    }

    @Test
    void scaled() {
        Matrix3D v = new Matrix3D(1,2,3,4,5,6,7,8,9);
        Matrix3D n = v.scaled(2);
        assertEquals(2, n.getM00(), 1e-9);
        assertEquals(4, n.getM01(), 1e-9);
        assertEquals(6, n.getM02(), 1e-9);
        assertEquals(8, n.getM10(), 1e-9);
        assertEquals(10, n.getM11(), 1e-9);
        assertEquals(12, n.getM12(), 1e-9);
        assertEquals(14, n.getM20(), 1e-9);
        assertEquals(16, n.getM21(), 1e-9);
        assertEquals(18, n.getM22(), 1e-9);
    }

    @Test
    void getDeterminant() {
        Matrix3D v = new Matrix3D(5, -2, 0, 1.6, 6, 17, 2.56, -10, 8);
        assertEquals(1028.56, v.getDeterminant(), 1e-9);
    }

    @Test
    void getInverse() {
        Matrix3D v = new Matrix3D(5, -2, 0, 1.6, 6, 17, 2.56, -10, 8);
        Matrix3D n = v.getInverse();
        assertEquals(0.21194679940888232091, n.getM00(), 1e-9);
        assertEquals(0.015555728396982188695, n.getM01(), 1e-9);
        assertEquals(-0.033055922843587150966, n.getM02(), 1e-9);
        assertEquals(0.029866998522205802287, n.getM10(), 1e-9);
        assertEquals(0.03888932099245547173, n.getM11(), 1e-9);
        assertEquals(-0.082639807108967877422, n.getM12(), 1e-9);
        assertEquals(-0.030489227658085089835, n.getM20(), 1e-9);
        assertEquals(0.043633818153535039276, n.getM21(), 1e-9);
        assertEquals(0.032278136423738041535, n.getM22(), 1e-9);
    }
}