package shapes;

import org.junit.jupiter.api.Test;
import rendering.shading.Material;
import rendering.shapes.Triangle;
import rendering.tracing.Intersection;
import rendering.tracing.Ray;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TriangleTest {

    @Test
    void intersect() {
        Triangle t = new Triangle(new Vector3D(7.53,5.46,1.5), new Vector3D(1,0.5,-0.5), new Vector3D(-4.13,5.27,-0.5), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        Ray r = new Ray(new Vector3D(-2.96,1.55,0), new Vector3D(7.04, 2.6, 0));
        Vector3D v = t.intersect(r).getIntersectionPoint();
        assertEquals(1.1889235018798452,v.getX(), 1e-9);
        assertEquals(3.082272884216988,v.getY(), 1e-9);
        assertEquals(0,v.getZ(), 1e-9);
        assert(t.intersect(r).getIntersectedShape() == t);

        t = new Triangle(new Vector3D(-1.87,-8.38,3), new Vector3D(1,0.5,-2), new Vector3D(3.47,4.06,-0.5), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(-1.32,-4.69,2.76), new Vector3D(1.69,-1.15,-2.75));
        v = t.intersect(r).getIntersectionPoint();
        assertEquals(-0.6449338014344091,v.getX(), 1e-9);
        assertEquals(-5.1493645729884205,v.getY(), 1e-9);
        assertEquals(1.6615194993755176,v.getZ(), 1e-9);
        assert(t.intersect(r).getIntersectedShape() == t);

        t = new Triangle(new Vector3D(-1.87,-8.38,3), new Vector3D(1,0.5,-2), new Vector3D(3.47,4.06,-0.5), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(-1.32,-4.69,2.76), new Vector3D(1.69,-1.15,1.07));
        assert(t.intersect(r) == null);

        t = new Triangle(new Vector3D(-1.87,-8.38,4.32), new Vector3D(0.8,-7.89,-2), new Vector3D(1.1,-1.23,-0.5), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(-4.19,-4.45,1.84), new Vector3D(-2.98,1.6,0.86));
        assert(t.intersect(r) == null);

        t = new Triangle(new Vector3D(-1.86821,-8.37571,4.32277), new Vector3D(0.80185,-7.89174,-2), new Vector3D(1.09907,-1.23121,-0.5), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        r = new Ray(new Vector3D(-4.19496,-4.45111,1.84128), (new Vector3D(-1.86821,-8.37571,4.32277)).added((new Vector3D(-4.19496,-4.45111,1.84128)).negated()));
        v = t.intersect(r).getIntersectionPoint();
        assertEquals(-1.86821,v.getX(), 1e-9);
        assertEquals(-8.37571,v.getY(), 1e-9);
        assertEquals(4.32277,v.getZ(), 1e-9);
        assert(t.intersect(r).getIntersectedShape() == t);
    }

    /// test flat shading normal computation
    @Test
    void getUnitNormalAtNoInterpolation() {
        Triangle t = new Triangle(new Vector3D(-1,-1,0), new Vector3D(1,-1,0), new Vector3D(0,1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        Vector3D n = t.getUnitNormalAt(new Intersection(t, new Vector3D(0,0,0)));
        assertEquals(0,n.getX(),1e-9);
        assertEquals(0,n.getY(),1e-9);
        assertEquals(1,n.getZ(),1e-9);

        t = new Triangle(new Vector3D(0,0,0), new Vector3D(1,0,1), new Vector3D(0,1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        n = t.getUnitNormalAt(new Intersection(t, new Vector3D(0,0,0)));
        assertEquals(-Math.sqrt(2)/2, n.getX(), 1e-9);
        assertEquals(0, n.getY(), 1e-9);
        assertEquals(Math.sqrt(2)/2, n.getZ(), 1e-9);
    }

    /// test smooth shading normal computation
    @Test
    void getUnitNormalAtWithInterpolation() {
        Triangle t = new Triangle(new Vector3D(-1,-1,0), new Vector3D(1,-1,0), new Vector3D(0,1,0), new Vector3D(0,1,0), new Vector3D(0,1,0), new Vector3D(0,1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        Vector3D n = t.getUnitNormalAt(new Intersection(t, new Vector3D(0,-1,0)));
        assertEquals(0,n.getX(),1e-9);
        assertEquals(1,n.getY(),1e-9);
        assertEquals(0,n.getZ(),1e-9);

        t = new Triangle(new Vector3D(3,-5,1), new Vector3D(2,-1,0), new Vector3D(2,7,1), new Vector3D(1,1,1), new Vector3D(0,1,2), new Vector3D(1,1,3), RTColor.red, Material.defaultNonReflectiveMaterial);
        n = t.getUnitNormalAt(new Intersection(t, new Vector3D(5, -21, 2)));
        Vector3D g = (new Vector3D(Math.sqrt(3) - 1 / Math.sqrt(11), Math.sqrt(3) - 1 / Math.sqrt(5) - 1 / Math.sqrt(11), Math.sqrt(3) - 2 / Math.sqrt(5) - 3 / Math.sqrt(11))).normalised();
        assertEquals(g.getX(), n.getX(),1e-9);
        assertEquals(g.getY(), n.getY(),1e-9);
        assertEquals(g.getZ(), n.getZ(),1e-9);
    }

    @Test
    void parseShape() throws IncorrectSceneDescriptionXMLStructureException {
        Map<String, String> c = Map.of("vertexA", "(1,2,3)", "vertexB", "(-1,-2,-3)", "vertexC", "(4,5,6)", "normalA", "(-123,-1,4)", "normalB", "(4,-13,-2)", "normalC", "(9,3,2)", "color", "(255,0,255)", "material", "gold");
        Triangle t = Triangle.parseShape(c);
        assertEquals(1, t.getVertexA().getX(), 1e-9);
        assertEquals(2, t.getVertexA().getY(), 1e-9);
        assertEquals(3, t.getVertexA().getZ(), 1e-9);
        assertEquals(-1, t.getVertexB().getX(), 1e-9);
        assertEquals(-2, t.getVertexB().getY(), 1e-9);
        assertEquals(-3, t.getVertexB().getZ(), 1e-9);
        assertEquals(4, t.getVertexC().getX(), 1e-9);
        assertEquals(5, t.getVertexC().getY(), 1e-9);
        assertEquals(6, t.getVertexC().getZ(), 1e-9);

        assertEquals(-0.999439, t.getNormalA().getX(), 1e-6);
        assertEquals(-0.00812552, t.getNormalA().getY(), 1e-6);
        assertEquals(0.0325021, t.getNormalA().getZ(), 1e-6);
        assertEquals(0.290957, t.getNormalB().getX(), 1e-6);
        assertEquals(-0.945611, t.getNormalB().getY(), 1e-6);
        assertEquals(-0.145479, t.getNormalB().getZ(), 1e-6);
        assertEquals(0.928279, t.getNormalC().getX(), 1e-6);
        assertEquals(0.309426, t.getNormalC().getY(), 1e-6);
        assertEquals(0.206284, t.getNormalC().getZ(), 1e-6);

        assertEquals(1, t.getColorAt(new Vector3D(1,2,3)).getRed(), 1e-9);
        assertEquals(0, t.getColorAt(new Vector3D(1,2,3)).getGreen(), 1e-9);
        assertEquals(1, t.getColorAt(new Vector3D(1,2,3)).getBlue(), 1e-9);

        assertEquals(0.24725, t.getMaterialAt(new Vector3D(1,2,3)).getAmbientCoefficient().getX(), 1e-9);
    }

    @Test
    void testGetBarycentricCoordinates() {
        Triangle t = new Triangle(new Vector3D(12,-1,0), new Vector3D(-4,6,1), new Vector3D(-2,7,4), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        Vector3D b = t.getBarycentricCoordinates(new Vector3D(0.5, 4.5, 1.5));
        assertEquals(0.25, b.getX(), 1e-9);
        assertEquals(0.5, b.getY(), 1e-9);
        assertEquals(0.25, b.getZ(), 1e-9);

        t = new Triangle(new Vector3D(1,-16,3), new Vector3D(-6,2,0), new Vector3D(56,72,41), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        b = t.getBarycentricCoordinates(new Vector3D(20.731, 22.052, 17.04));
        assertEquals(0.391, b.getX(), 1e-9);
        assertEquals(0.222, b.getY(), 1e-9);
        assertEquals(0.387, b.getZ(), 1e-9);

        t = new Triangle(new Vector3D(23,1,4), new Vector3D(-6,8,3), new Vector3D(-12,9,-4), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), new Vector3D(-1,-1,0), RTColor.red, Material.defaultNonReflectiveMaterial);
        b = t.getBarycentricCoordinates(new Vector3D(-77.2, 24.2, -14.4));
        assertEquals(-2, b.getX(), 1e-9);
        assertEquals(0.8, b.getY(), 1e-9);
        assertEquals(2.2, b.getZ(), 1e-9);

        t = new Triangle(new Vector3D(3,-5,1), new Vector3D(2,-1,0), new Vector3D(2,7,1), new Vector3D(1,1,1), new Vector3D(0,1,2), new Vector3D(1,1,3), RTColor.red, Material.defaultNonReflectiveMaterial);
        b = t.getBarycentricCoordinates(new Vector3D(5, -21, 2));
        assertEquals(3, b.getX(), 1e-9);
        assertEquals(-1, b.getY(), 1e-9);
        assertEquals(-1, b.getZ(), 1e-9);
    }
}