package utility;

import org.junit.jupiter.api.Test;
import rendering.utility.Vector3D;

import static org.junit.jupiter.api.Assertions.*;

class Vector3DTest {

    @Test
    void testNormalise() {
        Vector3D v = new Vector3D(3, 4, 5);
        Vector3D normed = v.normalised();

        assertEquals(3 / Math.sqrt(50), normed.getX(), 1e-9);
        assertEquals(4 / Math.sqrt(50), normed.getY(), 1e-9);
        assertEquals(5 / Math.sqrt(50), normed.getZ(), 1e-9);
    }

    @Test
    void testMagnitude() {
        Vector3D v = new Vector3D(3, -4, 5);

        assertEquals(50, v.magnitudeSquared());
        assertEquals(Math.sqrt(50), v.magnitude());
    }

    @Test
    void testAdded() {
        Vector3D v = new Vector3D(3.5, -4, 5);
        Vector3D u = new Vector3D(1.2,9.1,0);

        Vector3D c = v.added(u);
        assertEquals(4.7, c.getX(), 1e-9);
        assertEquals(5.1, c.getY(), 1e-9);
        assertEquals(5, c.getZ(), 1e-9);
    }

    @Test
    void testScaled() {
        Vector3D v = new Vector3D(3.5, -4, 5);
        Vector3D c = v.scaled(0.5);
        assertEquals(1.75, c.getX(), 1e-9);
        assertEquals(-2, c.getY(), 1e-9);
        assertEquals(2.5, c.getZ(), 1e-9);

        Vector3D u = new Vector3D(1.2,9.2,-1);
        c = u.scaled(-1.5);
        assertEquals(-1.8, c.getX(), 1e-9);
        assertEquals(-13.8, c.getY(), 1e-9);
        assertEquals(1.5, c.getZ(), 1e-9);
    }

    @Test
    void testNegated() {
        Vector3D v = new Vector3D(3.5, -4, 5);
        Vector3D c = v.negated();
        assertEquals(-3.5, c.getX(), 1e-9);
        assertEquals(4, c.getY(), 1e-9);
        assertEquals(-5, c.getZ(), 1e-9);
    }

    @Test
    void testScalarProduct() {
        Vector3D v = new Vector3D(3.5, -4, 5);
        double x = v.scalarProduct(new Vector3D(8, -0.5, 0.8));
        assertEquals(34, x, 1e-9);
    }

    @Test
    void testReflected() {
        Vector3D norm = new Vector3D(0,0,1);
        Vector3D v = new Vector3D(1,0,1);

        Vector3D refl = v.reflected(norm);
        assertEquals(-Math.sqrt(2)/2, refl.getX(), 1e-9);
        assertEquals(0, refl.getY(), 1e-9);
        assertEquals(Math.sqrt(2)/2, refl.getZ(), 1e-9);
        Vector3D refl1 = refl.reflected(norm);
        assertEquals(Math.sqrt(2)/2, refl1.getX(), 1e-9);
        assertEquals(0, refl1.getY(), 1e-9);
        assertEquals(Math.sqrt(2)/2, refl1.getZ(), 1e-9);


        norm = new Vector3D(1, 1, 0);
        v = new Vector3D(-1, -1, -1);
        refl = v.reflected(norm);
        assertEquals(-1 / Math.sqrt(3), refl.getX(), 1e-9);
        assertEquals(-1 / Math.sqrt(3), refl.getY(), 1e-9);
        assertEquals(1 / Math.sqrt(3), refl.getZ(), 1e-9);
        refl1 = refl.reflected(norm);
        assertEquals(-1 / Math.sqrt(3), refl1.getX(), 1e-9);
        assertEquals(-1 / Math.sqrt(3), refl1.getY(), 1e-9);
        assertEquals(-1 / Math.sqrt(3), refl1.getZ(), 1e-9);


        norm = new Vector3D(1, 1, 1);
        v = new Vector3D(1, 0, 0);
        refl = v.reflected(norm);
        refl1 = refl.reflected(norm);
        assertEquals(1, refl1.getX(), 1e-9);
        assertEquals(0, refl1.getY(), 1e-9);
        assertEquals(0, refl1.getZ(), 1e-9);

        norm = new Vector3D(-1, -1, -1);
        v = new Vector3D(1, 0 ,0);
        refl = v.reflected(norm);
        refl1 = refl.reflected(norm);
        assertEquals(1, refl1.getX(), 1e-9);
        assertEquals(0, refl1.getY(), 1e-9);
        assertEquals(0, refl1.getZ(), 1e-9);
    }

    @Test
    public void testCrossProduct() {
        Vector3D a = new Vector3D(12.3, -342.1, 44);
        Vector3D b = new Vector3D(132.23, -322.534, -34);
        Vector3D c = a.crossProduct(b);
        assertEquals(25822.896, c.getX(), 1e-9);
        assertEquals(6236.32, c.getY(), 1e-9);
        assertEquals(41268.7148, c.getZ(), 1e-9);

        a = new Vector3D(100, 8, 44);
        b = new Vector3D(20, 1.6, 8.8);
        c = a.crossProduct(b);
        assertEquals(0, c.getX(), 1e-9);
        assertEquals(0, c.getY(), 1e-9);
        assertEquals(0, c.getZ(), 1e-9);
    }

    @Test
    public void testTranslate() {
        Vector3D a = new Vector3D(12.3, 45, -10);
        Vector3D t = a.translate(new Vector3D(-6.1, 3, -20));
        assertEquals(6.2, t.getX(), 1e-9);
        assertEquals(48, t.getY(), 1e-9);
        assertEquals(-30, t.getZ(), 1e-9);
    }
}