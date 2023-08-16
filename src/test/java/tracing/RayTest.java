package tracing;

import org.junit.jupiter.api.Test;
import rendering.shading.Material;
import rendering.shapes.Plane;
import rendering.tracing.Intersection;
import rendering.tracing.Ray;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import static org.junit.jupiter.api.Assertions.*;

class RayTest {

    @Test
    void testPointAt() {
        Ray r = new Ray(new Vector3D(6,5,4), new Vector3D(1,2,3));
        Vector3D p = r.pointAt(5);
        assertEquals(11, p.getX(), 1e-9);
        assertEquals(15, p.getY(), 1e-9);
        assertEquals(19, p.getZ(), 1e-9);
    }

    @Test
    void testReflectedRay() {
        Ray r = new Ray(new Vector3D(-1, 1, 0), new Vector3D(1, -1, 0));
        Ray refl = r.reflectedRay(new Intersection(new Plane(new Vector3D(0, 1, 0), new Vector3D(4, 5, 6), RTColor.red, Material.defaultNonReflectiveMaterial), new Vector3D(4, 5, 6)));
        Vector3D dir = refl.getDirection();
        assertEquals(Math.sqrt(2) / 2, dir.getX(), 1e-9);
        assertEquals(Math.sqrt(2)/ 2, dir.getY(), 1e-9);
        assertEquals(0, dir.getZ(), 1e-9);
        Ray refl1 = refl.reflectedRay(new Intersection(new Plane(new Vector3D(0, 1, 0), new Vector3D(4, 5, 6), RTColor.red, Material.defaultNonReflectiveMaterial), new Vector3D(4, 5, 6)));
        dir = refl1.getDirection();
        assertEquals(Math.sqrt(2) / 2, dir.getX(), 1e-9);
        assertEquals(-Math.sqrt(2)/ 2, dir.getY(), 1e-9);
        assertEquals(0, dir.getZ(), 1e-9);


        r = new Ray(new Vector3D(7, 8, 9), new Vector3D(1, -1, -1));
        refl = r.reflectedRay(new Intersection(new Plane(new Vector3D(-1, 0, 1), new Vector3D(10,11,12), RTColor.red, Material.defaultNonReflectiveMaterial), new Vector3D(10,11,12)));
        dir = refl.getDirection();
        assertEquals(-Math.sqrt(3) / 3, dir.getX(), 1e-9);
        assertEquals(-Math.sqrt(3)/ 3, dir.getY(), 1e-9);
        assertEquals(Math.sqrt(3) / 3, dir.getZ(), 1e-9);
        refl1 = refl.reflectedRay(new Intersection(new Plane(new Vector3D(-1, 0, 1), new Vector3D(7,8,9), RTColor.red, Material.defaultNonReflectiveMaterial), new Vector3D(7,8,9)));
        dir = refl1.getDirection();
        assertEquals(Math.sqrt(3) / 3, dir.getX(), 1e-9);
        assertEquals(-Math.sqrt(3)/ 3, dir.getY(), 1e-9);
        assertEquals(-Math.sqrt(3)/ 3, dir.getZ(), 1e-9);
    }

    @Test
    void testDistance() {
        Ray r = new Ray(new Vector3D(-1, 1, 0), new Vector3D(1, -1, 0));
        Vector3D point = new Vector3D(4, -4, 0);
        assertEquals(5 * Math.sqrt(2), r.distance(point), 1e-9);
    }
}