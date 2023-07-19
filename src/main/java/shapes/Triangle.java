package shapes;

import shading.Material;
import tracing.Ray;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.RTColor;
import utility.SceneDescriptionParser;
import utility.Vector3D;

import java.util.Map;

/**
 * Class for a triangle defined by three vertices,
 * the normal vector to each vertex, with
 * the same color and material at each point on
 * its surface.
 *
 * The three given normals at vertices are used
 * for interpolating surface normals when triangles
 * are used in triangle meshes, i.e. solely for shading.
 * But for ray intersection, we can not use these
 * three explicitly given normals. Instead we must use
 * vector cross product (B-A) X (C-A) of the vertices
 * as the outwards unit normal to the triangle (i.e.
 * following counterclockwise direction A-B-C).
 *
 * TODO - create a Triangle Mesh class and render
 *        more complicated triangle meshes
 */

public class Triangle implements RTShape {
    public static final String shapeID = "triangle";

    /// the outer surface of the triangle is A-B-C counterclockwise
    private final Vector3D vertexA;
    private final Vector3D vertexB;
    private final Vector3D vertexC;

    /// unit normals to triangle mesh at vertices
    private final Vector3D unitNormalA;
    private final Vector3D unitNormalB;
    private final Vector3D unitNormalC;

    private final RTColor diffuseColor;
    private final Material material;

    /// unit normal to surface of triangle

    private final Vector3D surfaceUnitNormal;

    /**
     * Constructors
     */
    /*
       Constructor from three vertices, their normals, diffuse color,
       and the material. The unit normal to surface of the triangle is
       computed immediately.

       The vertex normals need not necessarily be normalised when given
       to the constructor.
     */
    public Triangle(Vector3D verA, Vector3D verB, Vector3D verC, Vector3D norA, Vector3D norB, Vector3D norC, RTColor color, Material material) {
        this.vertexA = verA;
        this.vertexB = verB;
        this.vertexC = verC;

        this.unitNormalA = norA.normalised();
        this.unitNormalB = norB.normalised();
        this.unitNormalC = norC.normalised();

        this.diffuseColor = color;
        this.material = material;

        this.surfaceUnitNormal = this.vertexB.added(this.vertexA.negated()).crossProduct(this.vertexC.added(this.vertexA.negated())).normalised();
    }

    /**
     * Methods
     */
    /*
       Method that intersects a ray with the triangle. It
       gives the closest intersection (minimum non-negative
       value of parameter s in P = O + s * D ray equation)
       if an intersection exists, or 'null' if no intersection
       exists (don't want to use exceptions for control
       flow when rendering).
     */
    public Vector3D intersect(Ray ray) {
        Plane plane = new Plane(this.surfaceUnitNormal, this.vertexA, null, null);
        Vector3D planeIntersectionPoint = plane.intersect(ray);

        /// if ray does not intersect triangle's plane, no intersection exists
        if(planeIntersectionPoint == null) {
            return null;
        }

        Vector3D barycentricCoordinates = this.getBarycentricCoordinates(planeIntersectionPoint);

        /// if not all barycentric coordinates are non-negative, intersection point with plane is outside of triangle
        if(barycentricCoordinates.getX() < 0 || barycentricCoordinates.getY() < 0 || barycentricCoordinates.getZ() < 0) {
            return null;
        }

        return planeIntersectionPoint;
    }
    /*
       Method that returns the unit normal at a given point on
       the surface of the triangle.

       The unit normal must point outwards by convention, which
       for a triangle is the direction given by the right-hand rule,
       i.e. by the counter-clockwise direction A-B-C. Therefore,
       the normal is the same everywhere and is computed as
       (B-A) X (C-A) (take care not to confuse this normal with
       the three vertex normals).

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Vector3D getUnitNormalAt(Vector3D point) {
        return this.surfaceUnitNormal;
    }
    /*
       Method that returns the diffuse color of the triangle at
       a given point on its surface.

       This triangle has the same diffuse color everywhere.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public RTColor getColorAt(Vector3D point) {
        return this.diffuseColor;
    }
    /*
       Method that returns the material (shading
       coefficients) at a given point on the surface of
       the RTShape.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Material getMaterialAt(Vector3D point) {
        return this.material;
    }

    /*
       Method that gives the barycentric coordinates of the given point
       (that is in the plane of the triangle) in the barycentric
       coordinate system defined by this triangle. The barycentric
       coordinates are given in a Vector3D object.

       (alpha,beta,gamma, alpha+beta+gamma = 1,
       point = alpha * vertexA + beta * vertexB + gamma * vertexC)

       If the given point is not in the plane of this triangle,
       behaviour is undefined.

       (thanks to https://ceng2.ktu.edu.tr/~cakir/files/grafikler/Texture_Mapping.pdf)
     */
    public Vector3D getBarycentricCoordinates(Vector3D point) {
        Vector3D c0 = this.vertexB.added(this.vertexA.negated());
        Vector3D c1 = this.vertexC.added(this.vertexA.negated());
        Vector3D c2 = point.added(this.vertexA.negated());

        double a00 = c0.scalarProduct(c0);
        double a01 = c1.scalarProduct(c0);
        double a11 = c1.scalarProduct(c1);
        double b0 = c0.scalarProduct(c2);
        double b1 = c1.scalarProduct(c2);

        double detA = a00*a11 - a01*a01;
        double detAv = b0*a11 - b1*a01;
        double detAw = a00*b1 - a01*b0;

        /* c_0^2c_1^2 = (c_0 dot c_1)^2 i.e.
           by Cauchy-Schwarz, c_0 (B-A) and c_1 (C-A)
           are parallel, i.e invalid triangle (A,B,C collinear).
        */
        assert(Math.abs(detA) > 1e-12);

        double v = detAv / detA;
        double w = detAw / detA;

        return new Vector3D(1 - v - w, v, w);
    }

    /**
     * Static Utility Methods
     */
    /*
       Method that parses a triangle from a Map<String,String>
       mapping attribute names to their values.

       Each triangle is defined by attributes "vertexA", "vertexB",
       "vertexC", "normalA", "normalB", "normalC", "color", and
       "material".

       If the material attribute is missing from the XML
       description of the RTShape, Material.defaultNonReflectiveMaterial
       is set.
     */
    public static Triangle parseShape(Map<String,String> attributes) throws IncorrectSceneDescriptionXMLStructureException {
        Vector3D vertexA = null, vertexB = null, vertexC = null;
        Vector3D normalA = null, normalB = null, normalC = null;
        RTColor color = null;
        Material material = null;

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            switch (attributeName) {
                case "vertexA" -> vertexA = SceneDescriptionParser.parseVector3D(attributeValue);
                case "vertexB" -> vertexB = SceneDescriptionParser.parseVector3D(attributeValue);
                case "vertexC" -> vertexC = SceneDescriptionParser.parseVector3D(attributeValue);

                case "normalA" -> normalA = SceneDescriptionParser.parseVector3D(attributeValue);
                case "normalB" -> normalB = SceneDescriptionParser.parseVector3D(attributeValue);
                case "normalC" -> normalC = SceneDescriptionParser.parseVector3D(attributeValue);

                case "color" -> color = SceneDescriptionParser.parseColor(attributeValue);
                case "material" ->
                {
                    /// first try to parse the material from name, then try to parse from description
                    try {
                        material = Material.parseMaterialFromName(attributeValue);
                    }
                    catch (IncorrectSceneDescriptionXMLStructureException e) {
                        material = Material.parseMaterial(attributeValue);
                    }
                }
                default -> throw new IncorrectSceneDescriptionXMLStructureException();
            }
        }

        if(vertexA == null || vertexB == null || vertexC == null || normalA == null || normalB == null || normalC == null || color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        /// if missing material in XML, set default
        if(material == null) {
            material = Material.defaultNonReflectiveMaterial;
        }

        return new Triangle(vertexA, vertexB, vertexC, normalA, normalB, normalC, color, material);
    }

    /**
     * Getters
     */
    public String getShapeID() {
        return Triangle.shapeID;
    }
    public Vector3D getVertexA() {
        return this.vertexA;
    }
    public Vector3D getVertexB() {
        return this.vertexB;
    }
    public Vector3D getVertexC() {
        return this.vertexC;
    }
    public Vector3D getNormalA() {
        return this.unitNormalA;
    }
    public Vector3D getNormalB() {
        return this.unitNormalB;
    }
    public Vector3D getNormalC() {
        return this.unitNormalC;
    }
}
