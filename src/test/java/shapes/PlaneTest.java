package shapes;

import org.junit.jupiter.api.Test;
import rendering.shading.Material;
import rendering.shapes.Plane;
import rendering.tracing.Ray;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlaneTest {

    @Test
    void intersect() {
        Plane p = new Plane(new Vector3D(0, 1, 0), new Vector3D(2,13,0), RTColor.red, Material.defaultNonReflectiveMaterial);

        Vector3D inter = p.intersect(new Ray(new Vector3D(5,0,-11), new Vector3D(0,1,0))).getIntersectionPoint();
        assertEquals(5, inter.getX());
        assertEquals(13, inter.getY());
        assertEquals(-11, inter.getZ());
        assert(p.intersect(new Ray(new Vector3D(5,0,-11), new Vector3D(0,1,0))).getIntersectedShape() == p);


        p = new Plane(new Vector3D(-1, 1, -1), new Vector3D(0,2,0), RTColor.red, Material.defaultNonReflectiveMaterial);

        inter = p.intersect(new Ray(new Vector3D(0,1,0), new Vector3D(-1,1,0))).getIntersectionPoint();
        assertEquals(-0.5, inter.getX());
        assertEquals(1.5, inter.getY());
        assertEquals(0, inter.getZ());
        assert(p.intersect(new Ray(new Vector3D(0,1,0), new Vector3D(-1,1,0))).getIntersectedShape() == p);


        p = new Plane(new Vector3D(-1, 1, -1), new Vector3D(0,2,0), RTColor.red, Material.defaultNonReflectiveMaterial);

        assert(p.intersect(new Ray(new Vector3D(0,1,0), new Vector3D(1,-1,0))) == null);


        p = new Plane(new Vector3D(28, -11, 157), new Vector3D(34,56,12), RTColor.red, Material.defaultNonReflectiveMaterial);

        assert(p.intersect(new Ray(new Vector3D(123,3123,434), new Vector3D(6,1,-1))) == null);


        p = new Plane(new Vector3D(6,5,1), new Vector3D(0,6,0), RTColor.red, Material.defaultNonReflectiveMaterial);

        inter = p.intersect(new Ray(new Vector3D(0,0,0), new Vector3D(2,4,8))).getIntersectionPoint();
        assertEquals(1.5, inter.getX());
        assertEquals(3, inter.getY());
        assertEquals(6, inter.getZ());
        assert(p.intersect(new Ray(new Vector3D(0,0,0), new Vector3D(2,4,8))).getIntersectedShape() == p);
    }

    @Test
    void parseShape() throws IncorrectSceneDescriptionXMLStructureException {
        Plane p = Plane.parseShape(Map.of("normal", "(1,-1,-1)", "point", "(2,0,0)", "color", "(0,255,0)"));

        Vector3D normal = p.getUnitNormal();
        assertEquals(1 / Math.sqrt(3), normal.getX(), 1e-9);
        assertEquals(-1 / Math.sqrt(3), normal.getY(), 1e-9);
        assertEquals(-1 / Math.sqrt(3), normal.getZ(), 1e-9);

        Vector3D point = p.getPointInPlane();
        assertEquals(2, point.getX(), 1e-9);
        assertEquals(0, point.getY(), 1e-9);
        assertEquals(0, point.getZ(), 1e-9);

        RTColor col = p.getColorAt(point);
        assertEquals(col.getRedInt(), 0);
        assertEquals(col.getGreenInt(), 255);
        assertEquals(col.getBlueInt(), 0);
    }
}