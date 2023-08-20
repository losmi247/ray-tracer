package utility;

import org.junit.jupiter.api.Test;
import rendering.utility.Matrix4D;
import rendering.utility.Vector3D;

import static org.junit.jupiter.api.Assertions.*;

class Matrix4DTest {

    @Test
    void multiply() {
        Matrix4D m = new Matrix4D(1,2,3,4, 0, 0,5, 6, 0, 2, 1, 3, 0, 0, 0, 0);
        Matrix4D n = m.multiplyFromRight(new Matrix4D(0, 1, 1, 1, 0, 0, 2, 2, 0, 0, 0, 3, 0, 0, 0, 0));
        assertEquals(0, n.getM00(), 1e-9);
        assertEquals(1, n.getM01(), 1e-9);
        assertEquals(5, n.getM02(), 1e-9);
        assertEquals(14, n.getM03(), 1e-9);
        assertEquals(0, n.getM10(), 1e-9);
        assertEquals(0, n.getM11(), 1e-9);
        assertEquals(0, n.getM12(), 1e-9);
        assertEquals(15, n.getM13(), 1e-9);
        assertEquals(0, n.getM20(), 1e-9);
        assertEquals(0, n.getM21(), 1e-9);
        assertEquals(4, n.getM22(), 1e-9);
        assertEquals(7, n.getM23(), 1e-9);
        assertEquals(0, n.getM30(), 1e-9);
        assertEquals(0, n.getM31(), 1e-9);
        assertEquals(0, n.getM32(), 1e-9);
        assertEquals(0, n.getM33(), 1e-9);

        m = new Matrix4D(12,-43,431,4.5, 1.2, 0,4, -0.2, 6.7, 1, 1, 1, 2, 3.98, 90, 12.2);
        n = m.multiplyFromRight(new Matrix4D(4, 4, 5, 6, -1, -2, 0, 28, 0, 9, 1, -60, 1, 1, 1, 1));
        assertEquals(95.5, n.getM00(), 1e-9);
        assertEquals(4017.5, n.getM01(), 1e-9);
        assertEquals(495.5, n.getM02(), 1e-9);
        assertEquals(-26987.5, n.getM03(), 1e-9);
        assertEquals(4.6, n.getM10(), 1e-9);
        assertEquals(40.5999999999999, n.getM11(), 1e-9);
        assertEquals(9.8, n.getM12(), 1e-9);
        assertEquals(-233, n.getM13(), 1e-9);
        assertEquals(26.8, n.getM20(), 1e-9);
        assertEquals(34.8, n.getM21(), 1e-9);
        assertEquals(35.5, n.getM22(), 1e-9);
        assertEquals(9.2, n.getM23(), 1e-9);
        assertEquals(16.22, n.getM30(), 1e-9);
        assertEquals(822.24, n.getM31(), 1e-9);
        assertEquals(112.2, n.getM32(), 1e-9);
        assertEquals(-5264.36, n.getM33(), 1e-9);
    }

    @Test
    void testMultiply() {
        Matrix4D m = new Matrix4D(12,-43,431,4.5, 1.2, 0,4, -0.2, 6.7, 1, 1, 1, 2, 3.98, 90, 12.2);
        Vector3D v = m.multiplyFromRight(new Vector3D(-34,12,452));
        assertEquals(193892.5, v.getX(), 1e-9);
        assertEquals(1767, v.getY(), 1e-9);
        assertEquals(237.2, v.getZ(), 1e-9);
    }

    @Test
    void getScalingMatrix() {
        Matrix4D n = Matrix4D.getScalingMatrix(new Vector3D(-2, 0, 7.6));
        assertEquals(-2, n.getM00(), 1e-9);
        assertEquals(0, n.getM01(), 1e-9);
        assertEquals(0, n.getM02(), 1e-9);
        assertEquals(0, n.getM03(), 1e-9);
        assertEquals(0, n.getM10(), 1e-9);
        assertEquals(0, n.getM11(), 1e-9);
        assertEquals(0, n.getM12(), 1e-9);
        assertEquals(0, n.getM13(), 1e-9);
        assertEquals(0, n.getM20(), 1e-9);
        assertEquals(0, n.getM21(), 1e-9);
        assertEquals(7.6, n.getM22(), 1e-9);
        assertEquals(0, n.getM23(), 1e-9);
        assertEquals(0, n.getM30(), 1e-9);
        assertEquals(0, n.getM31(), 1e-9);
        assertEquals(0, n.getM32(), 1e-9);
        assertEquals(1, n.getM33(), 1e-9);
    }

    @Test
    void getRotationMatrixAboutX() {
        Matrix4D n = Matrix4D.getRotationMatrixAboutX(45);
        Vector3D v = n.multiplyFromRight(new Vector3D(17, 1, 1));
        assertEquals(17, v.getX(), 1e-9);
        assertEquals(0, v.getY(), 1e-9);
        assertEquals(Math.sqrt(2), v.getZ(), 1e-9);

        n = Matrix4D.getRotationMatrixAboutX(78);
        v = n.multiplyFromRight(new Vector3D(17, -3, 21));
        assertEquals(17, v.getX(), 1e-9);
        assertEquals(-21.164834688, v.getY(), 1e-9);
        assertEquals(1.431702705, v.getZ(), 1e-9);

        n = Matrix4D.getRotationMatrixAboutZ(-20).multiplyFromRight(
                Matrix4D.getRotationMatrixAboutX(-331).multiplyFromRight(
                        Matrix4D.getRotationMatrixAboutY(3)
                )
        );
        v = n.multiplyFromRight(new Vector3D(65, 89, 2));
        assertEquals(87.950813987, v.getX(), 1e-9);
        assertEquals(51.550002444, v.getY(), 1e-9);
        assertEquals(41.919584527, v.getZ(), 1e-9);
    }

    @Test
    void getRotationMatrixAboutY() {
        Matrix4D n = Matrix4D.getRotationMatrixAboutY(45);
        Vector3D v = n.multiplyFromRight(new Vector3D(1, 17, 1));
        assertEquals(Math.sqrt(2), v.getX(), 1e-9);
        assertEquals(17, v.getY(), 1e-9);
        assertEquals(0, v.getZ(), 1e-9);

        n = Matrix4D.getRotationMatrixAboutY(-81);
        v = n.multiplyFromRight(new Vector3D(17, -3, 21));
        assertEquals(-18.082069247, v.getX(), 1e-9);
        assertEquals(-3, v.getY(), 1e-9);
        assertEquals(20.075825556, v.getZ(), 1e-9);

        n = Matrix4D.getRotationMatrixAboutX(-331).multiplyFromRight(
                Matrix4D.getRotationMatrixAboutZ(-20).multiplyFromRight(
                        Matrix4D.getRotationMatrixAboutY(3)
                )
        );
        v = n.multiplyFromRight(new Vector3D(65, 89, 2));
        assertEquals(91.534464486, v.getX(), 1e-9);
        assertEquals(54.379105617, v.getY(), 1e-9);
        assertEquals(28.536900385, v.getZ(), 1e-9);
    }

    @Test
    void getRotationMatrixAboutZ() {
        Matrix4D n = Matrix4D.getRotationMatrixAboutZ(45);
        Vector3D v = n.multiplyFromRight(new Vector3D(1, 1, 17));
        assertEquals(0, v.getX(), 1e-9);
        assertEquals(Math.sqrt(2), v.getY(), 1e-9);
        assertEquals(17, v.getZ(), 1e-9);

        n = Matrix4D.getRotationMatrixAboutZ(235);
        v = n.multiplyFromRight(new Vector3D(17, -3, 21));
        assertEquals(-12.208255551, v.getX(), 1e-9);
        assertEquals(-12.204855444, v.getY(), 1e-9);
        assertEquals(21, v.getZ(), 1e-9);

        n = Matrix4D.getRotationMatrixAboutZ(-20).multiplyFromRight(
                Matrix4D.getRotationMatrixAboutY(3).multiplyFromRight(
                        Matrix4D.getRotationMatrixAboutX(-331)
                )
        );
        v = n.multiplyFromRight(new Vector3D(65, 89, 2));
        assertEquals(89.495961683, v.getX(), 1e-9);
        assertEquals(49.231112419, v.getY(), 1e-9);
        assertEquals(41.433928277, v.getZ(), 1e-9);
    }

    @Test
    void getTranslationMatrix() {
        Matrix4D n = Matrix4D.getTranslationMatrix(new Vector3D(6, -1, 3.2));
        assertEquals(1, n.getM00(), 1e-9);
        assertEquals(0, n.getM01(), 1e-9);
        assertEquals(0, n.getM02(), 1e-9);
        assertEquals(6, n.getM03(), 1e-9);
        assertEquals(0, n.getM10(), 1e-9);
        assertEquals(1, n.getM11(), 1e-9);
        assertEquals(0, n.getM12(), 1e-9);
        assertEquals(-1, n.getM13(), 1e-9);
        assertEquals(0, n.getM20(), 1e-9);
        assertEquals(0, n.getM21(), 1e-9);
        assertEquals(1, n.getM22(), 1e-9);
        assertEquals(3.2, n.getM23(), 1e-9);
        assertEquals(0, n.getM30(), 1e-9);
        assertEquals(0, n.getM31(), 1e-9);
        assertEquals(0, n.getM32(), 1e-9);
        assertEquals(1, n.getM33(), 1e-9);
    }

    @Test
    void getDeterminantTest() {
        Matrix4D v = new Matrix4D(1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1, 1);
        assertEquals(-16, v.getDeterminant(), 1e-9);

        v = new Matrix4D(5, -7, -1, 0, 11, 56.78, 33, 1, 2, 0.6, 0, 1, 17, 21, 1, 56);
        assertEquals(-19144.199999999997, v.getDeterminant(), 1e-9);
    }

    @Test
    void transposedTest() {
        Matrix4D v = new Matrix4D(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4D n = v.transposed();

        assertEquals(1, n.getM00(), 1e-9);
        assertEquals(5, n.getM01(), 1e-9);
        assertEquals(9, n.getM02(), 1e-9);
        assertEquals(13, n.getM03(), 1e-9);
        assertEquals(2, n.getM10(), 1e-9);
        assertEquals(6, n.getM11(), 1e-9);
        assertEquals(10, n.getM12(), 1e-9);
        assertEquals(14, n.getM13(), 1e-9);
        assertEquals(3, n.getM20(), 1e-9);
        assertEquals(7, n.getM21(), 1e-9);
        assertEquals(11, n.getM22(), 1e-9);
        assertEquals(15, n.getM23(), 1e-9);
        assertEquals(4, n.getM30(), 1e-9);
        assertEquals(8, n.getM31(), 1e-9);
        assertEquals(12, n.getM32(), 1e-9);
        assertEquals(16, n.getM33(), 1e-9);
    }

    @Test
    void scaledTest() {
        Matrix4D v = new Matrix4D(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4D n = v.scaled((double) 1 / 16);

        assertEquals((double) 1 / 16, n.getM00(), 1e-9);
        assertEquals((double) 2 / 16, n.getM01(), 1e-9);
        assertEquals((double) 3 / 16, n.getM02(), 1e-9);
        assertEquals((double) 4 / 16, n.getM03(), 1e-9);
        assertEquals((double) 5 / 16, n.getM10(), 1e-9);
        assertEquals((double) 6 / 16, n.getM11(), 1e-9);
        assertEquals((double) 7 / 16, n.getM12(), 1e-9);
        assertEquals((double) 8 / 16, n.getM13(), 1e-9);
        assertEquals((double) 9 / 16, n.getM20(), 1e-9);
        assertEquals((double) 10 / 16, n.getM21(), 1e-9);
        assertEquals((double) 11 / 16, n.getM22(), 1e-9);
        assertEquals((double) 12 / 16, n.getM23(), 1e-9);
        assertEquals((double) 13 / 16, n.getM30(), 1e-9);
        assertEquals((double) 14 / 16, n.getM31(), 1e-9);
        assertEquals((double) 15 / 16, n.getM32(), 1e-9);
        assertEquals((double) 16 / 16, n.getM33(), 1e-9);
    }

    @Test
    void inverseTest() {
        Matrix4D v = new Matrix4D(1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1, 1);
        Matrix4D n = v.getInverse();

        assertEquals((double) 1 / 4, n.getM00(), 1e-9);
        assertEquals((double) 1 / 4, n.getM01(), 1e-9);
        assertEquals((double) 1 / 4, n.getM02(), 1e-9);
        assertEquals((double) -1 / 4, n.getM03(), 1e-9);
        assertEquals((double) 1 / 4, n.getM10(), 1e-9);
        assertEquals((double) 1 / 4, n.getM11(), 1e-9);
        assertEquals((double) -1 / 4, n.getM12(), 1e-9);
        assertEquals((double) 1 / 4, n.getM13(), 1e-9);
        assertEquals((double) 1 / 4, n.getM20(), 1e-9);
        assertEquals((double) -1 / 4, n.getM21(), 1e-9);
        assertEquals((double) 1 / 4, n.getM22(), 1e-9);
        assertEquals((double) 1 / 4, n.getM23(), 1e-9);
        assertEquals((double) -1 / 4, n.getM30(), 1e-9);
        assertEquals((double) 1 / 4, n.getM31(), 1e-9);
        assertEquals((double) 1 / 4, n.getM32(), 1e-9);
        assertEquals((double) 1 / 4, n.getM33(), 1e-9);


        v = new Matrix4D(3, -2, 0, 9, 1, 5, 15, -10, 3, 11, -23,6, 13, 1, 5, 7);
        n = v.getInverse();

        assertEquals((double) -0.1475548060708263, n.getM00(), 1e-9);
        assertEquals(-0.057124789207419896, n.getM01(), 1e-9);
        assertEquals(-0.011593591905564924, n.getM02(), 1e-9);
        assertEquals(0.11804384485666104, n.getM03(), 1e-9);
        assertEquals(0.13048060708263068, n.getM10(), 1e-9);
        assertEquals(0.11872892074198989, n.getM11(), 1e-9);
        assertEquals(0.0656091905564924, n.getM12(), 1e-9);
        assertEquals(-0.05438448566610455, n.getM13(), 1e-9);
        assertEquals(0.09253794266441821, n.getM20(), 1e-9);
        assertEquals(0.061182546374367625, n.getM21(), 1e-9);
        assertEquals(-0.008800590219224283, n.getM22(), 1e-9);
        assertEquals(-0.024030354131534568, n.getM23(), 1e-9);
        assertEquals(0.18929173693086004, n.getM30(), 1e-9);
        assertEquals(0.045425801011804386, n.getM31(), 1e-9);
        assertEquals(0.018444350758853287, n.getM32(), 1e-9);
        assertEquals(-0.05143338954468803, n.getM33(), 1e-9);
    }
}