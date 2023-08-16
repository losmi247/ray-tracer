package shapes;

import org.junit.jupiter.api.Test;
import rendering.shading.Material;
import rendering.shapes.Sphere;
import rendering.tracing.Intersection;
import rendering.tracing.Ray;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SphereTest {

    @Test
    void intersect() {
        Sphere s = new Sphere(new Vector3D(0,0,0),2, RTColor.red, Material.defaultNonReflectiveMaterial);
        Ray r = new Ray(new Vector3D(5,5,0), new Vector3D(-1,-1,0));
        Vector3D inter = s.intersect(r).getIntersectionPoint();
        assertEquals(2 / Math.sqrt(2), inter.getX(), 1e-9);
        assertEquals(2 / Math.sqrt(2), inter.getY(), 1e-9);
        assertEquals(0, inter.getZ(), 1e-9);
        assertEquals(2, inter.added(s.getCenter().negated()).magnitude(), 1e-9);
        assert(s.intersect(r).getIntersectedShape() == s);

        s = new Sphere(new Vector3D(7,9,39.8),5, RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(7,0,39.8), new Vector3D(0,1,0));
        inter = s.intersect(r).getIntersectionPoint();
        assertEquals(7, inter.getX(), 1e-9);
        assertEquals(4, inter.getY(), 1e-9);
        assertEquals(39.8, inter.getZ(), 1e-9);
        assertEquals(5, inter.added(s.getCenter().negated()).magnitude(), 1e-9);
        assert(s.intersect(r).getIntersectedShape() == s);

        s = new Sphere(new Vector3D(-7.04, 5.16, 2),1.5, RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(-0.19,1.82,1), new Vector3D(-0.82,0.54,0.2));
        inter = s.intersect(r).getIntersectionPoint();
        assertEquals(-5.589891726119754, inter.getX(), 1e-9);
        assertEquals(5.376026258664229, inter.getY(), 1e-9);
        assertEquals(2.317046762468233, inter.getZ(), 1e-9);
        assertEquals(1.5, inter.added(s.getCenter().negated()).magnitude(), 1e-9);
        assert(s.intersect(r).getIntersectedShape() == s);

        s = new Sphere(new Vector3D(3.45, -2.59, 2), 0.9, RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(0.61,2.58,1), new Vector3D(0.58,-0.81,0.08));
        inter = s.intersect(r).getIntersectionPoint();
        assertEquals(3.9282558951288262, inter.getX(), 1e-9);
        assertEquals(-2.054115991473017, inter.getY(), 1e-9);
        assertEquals(1.4576904682936314, inter.getZ(), 1e-9);
        assertEquals(0.9, inter.added(s.getCenter().negated()).magnitude(), 1e-9);
        assert(s.intersect(r).getIntersectedShape() == s);

        /// no intersection
        s = new Sphere(new Vector3D(4.1, -1.63, 2), 1.7, RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(1.34, 2.26, 1), new Vector3D(-0.89, 0.43, 0.16));
        assert(s.intersect(r) == null);

        /// intersections behind origin
        s = new Sphere(new Vector3D(1.67, 1.4, 2), 1.5, RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(-0.82, 2.37, 1), new Vector3D(-0.85, 0.49, -0.19));
        assert(s.intersect(r) == null);
    }

    @Test
    void parseShape() throws IncorrectSceneDescriptionXMLStructureException {
        Map<String,String> at = Map.of("center", "(1.2,-5.67,0)", "color", "(120,65,9)", "radius", "10.6");
        Sphere s = Sphere.parseShape(at);

        assertEquals(10.6, s.getRadius(), 1e-9);
        RTColor c = s.getColor();
        assertEquals(120, c.getRedInt());
        assertEquals(65, c.getGreenInt());
        assertEquals(9, c.getBlueInt());
        Vector3D v = s.getCenter();
        assertEquals(1.2, v.getX(), 1e-9);
        assertEquals(-5.67, v.getY(), 1e-9);
        assertEquals(0, v.getZ(), 1e-9);
    }

    @Test
    void testGetUnitNormalAt() {
        Sphere s = new Sphere(new Vector3D(0,0,0), 3, RTColor.green, Material.defaultNonReflectiveMaterial);
        Vector3D normal = s.getUnitNormalAt(new Intersection(s, new Vector3D(Math.sqrt(3), Math.sqrt(3), Math.sqrt(3))));
        assertEquals(Math.sqrt(3) / 3, normal.getX(), 1e-9);
        assertEquals(Math.sqrt(3) / 3, normal.getY(), 1e-9);
        assertEquals(Math.sqrt(3) / 3, normal.getZ(), 1e-9);

        s = new Sphere(new Vector3D(5,6,7), 4, RTColor.green, Material.defaultNonReflectiveMaterial);
        normal = s.getUnitNormalAt(new Intersection(s, new Vector3D(5,6,3)));
        assertEquals(0, normal.getX(), 1e-9);
        assertEquals(0, normal.getY(), 1e-9);
        assertEquals(-1, normal.getZ(), 1e-9);

        s = new Sphere(new Vector3D(5,6,7), 4, RTColor.green, Material.defaultNonReflectiveMaterial);
        normal = s.getUnitNormalAt(new Intersection(s, new Vector3D(5,6,11)));
        assertEquals(0, normal.getX(), 1e-9);
        assertEquals(0, normal.getY(), 1e-9);
        assertEquals(1, normal.getZ(), 1e-9);
    }
}