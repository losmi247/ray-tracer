package shapes;

import org.junit.jupiter.api.Test;
import rendering.shading.Material;
import rendering.shapes.Triangle;
import rendering.shapes.TriangleMesh;
import rendering.tracing.Intersection;
import rendering.tracing.Ray;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.Matrix4D;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TriangleMeshTest {

    @Test
    void testConstructor() throws IOException {
        TriangleMesh tm = new TriangleMesh("src/main/resources/meshes/pawnFlatShadedWithNormals.obj", Matrix4D.identity, RTColor.red, Material.defaultNonReflectiveMaterial);
        ArrayList<Vector3D> vs = tm.getVertices();
        ArrayList<Vector3D> ns = tm.getNormals();
        ArrayList<Triangle> ts = tm.getTriangles();

        assertEquals(417, vs.size());
        /// for some reason there are 421 rather than 417 normals
        assertEquals(421, ns.size());
        assertEquals(830, ts.size());

        Vector3D v = vs.get(7);
        assertEquals(0.344415, v.getX(), 1e-6);
        assertEquals(0.000000, v.getY(), 1e-6);
        assertEquals(0.831492, v.getZ(), 1e-6);

        v = ns.get(0);
        assertEquals(-0.1637, v.getX(), 1e-6);
        assertEquals(0.9804, v.getY(), 1e-6);
        assertEquals(-0.1094, v.getZ(), 1e-6);
        v = ns.get(9);
        assertEquals(0.1637, v.getX(), 1e-6);
        assertEquals(0.9804, v.getY(), 1e-6);
        assertEquals(-0.1094, v.getZ(), 1e-6);

        Triangle t = ts.get(9);
        assert(t.getVertexA() == vs.get(413-1));
        assert(t.getVertexB() == vs.get(411-1));
        assert(t.getVertexC() == vs.get(17-1));
        assertEquals(t.getNormalA().getX(), ns.get(10-1).getX(), 1e-4);
        assertEquals(t.getNormalB().getX(), ns.get(10-1).getX(), 1e-4);
        assertEquals(t.getNormalC().getX(), ns.get(10-1).getX(), 1e-4);
        assertEquals(t.getNormalA().getY(), ns.get(10-1).getY(), 1e-4);
        assertEquals(t.getNormalB().getY(), ns.get(10-1).getY(), 1e-4);
        assertEquals(t.getNormalC().getY(), ns.get(10-1).getY(), 1e-4);
        assertEquals(t.getNormalA().getZ(), ns.get(10-1).getZ(), 1e-4);
        assertEquals(t.getNormalB().getZ(), ns.get(10-1).getZ(), 1e-4);
        assertEquals(t.getNormalC().getZ(), ns.get(10-1).getZ(), 1e-4);
    }

    @Test
    void intersect() throws IOException {
        TriangleMesh tm = new TriangleMesh("src/main/resources/meshes/pawnFlatShadedWithNormals.obj", Matrix4D.identity, RTColor.red, Material.defaultNonReflectiveMaterial);
        Ray r = new Ray(new Vector3D(10,0,0), new Vector3D(-1, 0, 0));
        Intersection inter = tm.intersect(r);
        Vector3D v = inter.getIntersectionPoint();
        assertEquals(0.9, v.getX(), 1e-1);
        assertEquals(0, v.getY(), 1e-1);
        assertEquals(0, v.getZ(), 1e-1);

        r = new Ray(new Vector3D(0,10,0), new Vector3D(0, -1, 0));
        inter = tm.intersect(r);
        v = inter.getIntersectionPoint();
        assertEquals(0, v.getX(), 1e-6);
        assertEquals(3.517765, v.getY(), 1e-6);
        assertEquals(0, v.getZ(), 1e-6);

        r = new Ray(new Vector3D(0,-10,0), new Vector3D(0, 1, 0));
        inter = tm.intersect(r);
        v = inter.getIntersectionPoint();
        assertEquals(0, v.getX(), 1e-6);
        assertEquals(0, v.getY(), 1e-6);
        assertEquals(0, v.getZ(), 1e-6);

        r = new Ray(new Vector3D(0,10,0), new Vector3D(1, 1, 0));
        inter = tm.intersect(r);
        assert(inter == null);

        r = new Ray(new Vector3D(0,10,-0.46089), new Vector3D(0, -1, 0));
        inter = tm.intersect(r);
        v = inter.getIntersectionPoint();
        assertEquals(0, v.getX(), 1e-6);
        assertEquals(3.2975,  v.getY(), 1e-4);
        assertEquals(-0.46089, v.getZ(), 1e-4);

        r = new Ray(new Vector3D(0,1.7575,-10), new Vector3D(0, 0, 1));
        inter = tm.intersect(r);
        v = inter.getIntersectionPoint();
        assertEquals(0, v.getX(), 1e-6);
        assertEquals(1.7575,  v.getY(), 1e-4);
        assertEquals(-0.37387, v.getZ(), 1e-5);
    }

    @Test
    void getUnitNormalAt() throws IOException {
        /// smooth shading i.e. normal interpolation
        TriangleMesh tm = new TriangleMesh("src/main/resources/meshes/pawnAutoSmoothShadedWithNormals.obj", Matrix4D.identity, RTColor.red, Material.defaultNonReflectiveMaterial);
        Ray r = new Ray(new Vector3D(0,10,0), new Vector3D(0, -1, 0));
        Intersection inter = tm.intersect(r);
        Vector3D v = tm.getUnitNormalAt(inter);
        assertEquals(0, v.getX(), 1e-6);
        assertEquals(1, v.getY(), 1e-6);
        assertEquals(0, v.getZ(), 1e-6);

        r = new Ray(new Vector3D(0,-10,0), new Vector3D(0, 1, 0));
        inter = tm.intersect(r);
        v = tm.getUnitNormalAt(inter);
        assertEquals(0, v.getX(), 1e-6);
        assertEquals(-1, v.getY(), 1e-6);
        assertEquals(0, v.getZ(), 1e-6);

        r = new Ray(new Vector3D(10,0.1,0), new Vector3D(-1, 0, 0));
        inter = tm.intersect(r);
        v = tm.getUnitNormalAt(inter);
        assertEquals(1, v.getX(), 1e-6);
        assertEquals(0, v.getY(), 1e-6);
        assertEquals(0, v.getZ(), 1e-6);

        r = new Ray(new Vector3D(10,0.1,10), new Vector3D(-1, 0, -1));
        inter = tm.intersect(r);
        v = tm.getUnitNormalAt(inter);
        assertEquals(1 / Math.sqrt(2), v.getX(), 1e-6);
        assertEquals(0, v.getY(), 1e-6);
        assertEquals(1 / Math.sqrt(2), v.getZ(), 1e-6);

        r = new Ray(new Vector3D(10,0,10), new Vector3D(-1, 0, -1));
        inter = tm.intersect(r);
        v = tm.getUnitNormalAt(inter);
        assertEquals(1 / Math.sqrt(2), v.getX(), 1e-6);
        assertEquals(0, v.getY(), 1e-6);
        assertEquals(1 / Math.sqrt(2), v.getZ(), 1e-6);
    }

    @Test
    void parseShape() throws IOException, IncorrectSceneDescriptionXMLStructureException {
        TriangleMesh tm = TriangleMesh.parseShape(Map.of("path-to-obj-file", "src/main/resources/meshes/pawnFlatShadedWithNormals.obj",
                "color", "(255,0,0)", "material", "brass"), Matrix4D.identity);

        ArrayList<Vector3D> vs = tm.getVertices();
        ArrayList<Vector3D> ns = tm.getNormals();
        ArrayList<Triangle> ts = tm.getTriangles();

        assertEquals(417, vs.size());
        /// for some reason there are 421 rather than 417 normals
        assertEquals(421, ns.size());
        assertEquals(830, ts.size());

        Vector3D v = vs.get(7);
        assertEquals(0.344415, v.getX(), 1e-6);
        assertEquals(0.000000, v.getY(), 1e-6);
        assertEquals(0.831492, v.getZ(), 1e-6);

        v = ns.get(0);
        assertEquals(-0.1637, v.getX(), 1e-6);
        assertEquals(0.9804, v.getY(), 1e-6);
        assertEquals(-0.1094, v.getZ(), 1e-6);
        v = ns.get(9);
        assertEquals(0.1637, v.getX(), 1e-6);
        assertEquals(0.9804, v.getY(), 1e-6);
        assertEquals(-0.1094, v.getZ(), 1e-6);

        Triangle t = ts.get(9);
        assert(t.getVertexA() == vs.get(413-1));
        assert(t.getVertexB() == vs.get(411-1));
        assert(t.getVertexC() == vs.get(17-1));
        assertEquals(t.getNormalA().getX(), ns.get(10-1).getX(), 1e-4);
        assertEquals(t.getNormalB().getX(), ns.get(10-1).getX(), 1e-4);
        assertEquals(t.getNormalC().getX(), ns.get(10-1).getX(), 1e-4);
        assertEquals(t.getNormalA().getY(), ns.get(10-1).getY(), 1e-4);
        assertEquals(t.getNormalB().getY(), ns.get(10-1).getY(), 1e-4);
        assertEquals(t.getNormalC().getY(), ns.get(10-1).getY(), 1e-4);
        assertEquals(t.getNormalA().getZ(), ns.get(10-1).getZ(), 1e-4);
        assertEquals(t.getNormalB().getZ(), ns.get(10-1).getZ(), 1e-4);
        assertEquals(t.getNormalC().getZ(), ns.get(10-1).getZ(), 1e-4);
    }
}