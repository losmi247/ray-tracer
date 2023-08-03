package rendering.shapes;

import rendering.shading.Material;
import rendering.tracing.Intersection;
import rendering.tracing.Ray;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.RTColor;
import rendering.utility.SceneDescriptionParser;
import rendering.utility.Vector3D;

import java.util.Map;

/**
 * Class for a plane defined by a unit normal to it,
 * and a single point that belongs to the plane, with
 * the same color and material at each point on
 * its surface.
 */

public class Plane implements RTShape {
    public static final String shapeID = "plane";

    private Vector3D unitNormal;
    private Vector3D pointInPlane;

    private RTColor diffuseColor;
    private Material material;

    /**
     * Constructors
     */
    /*
       Constructor from a normal to surface of the plane (not
       necessarily normalised), a point that belongs to it,
       and its diffuse color.
     */
    public Plane(Vector3D normal, Vector3D point, RTColor color, Material material) {
        this.unitNormal = normal.normalised();
        this.pointInPlane = point;
        this.diffuseColor = color;
        this.material = material;
    }

    /**
     * Methods
     */
    /*
       Method that intersects a ray with the plane. It
       gives the closest intersection (minimum non-negative
       value of parameter s in P = O + s * D ray equation)
       if an intersection exists, or 'null' if no intersection
       exists (don't want to use exceptions for control
       flow when rendering).

       The point of intersection is bundled together with the
       intersected shape, into an Intersection object.
     */
    public Intersection intersect(Ray ray) {
        /*
        (O+s*D - A) dot N = 0

        (O-A) dot N + s (D dot N) = 0
         */

        double denominator = ray.getDirection().scalarProduct(this.unitNormal);

        /// if ray is parallel to plane, there is no intersection
        if(Math.abs(denominator) < 1e-12) {
            return null;
        }
        else {
            double numerator = (ray.getOrigin().added(this.pointInPlane.negated())).scalarProduct(this.unitNormal);
            double s = -numerator / denominator;

            if(s > 0) {
                return new Intersection(this, ray.pointAt(s));
            }
            else {
                return null;
            }
        }
    }
    /*
       Method that returns the unit normal at a given point on
       the surface of the plane, given as an Intersection object.

       The unit normal must point outwards by convention, which
       for a plane is the direction in which the unit normal is
       defined when passed to the constructor. For a plane,
       unit normal is the same everywhere.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Vector3D getUnitNormalAt(Intersection intersection) {
        return this.unitNormal;
    }
    /*
       Method that returns the diffuse color of the plane at
       a given point on its surface.

       This plane has the same diffuse color everywhere.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public RTColor getColorAt(Vector3D point) {
        return this.diffuseColor;
    }
    /*
       Method that returns the material (shading
       coefficients) at a given point on the surface of
       the plane.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Material getMaterialAt(Vector3D point) {
        return this.material;
    }

    /**
     * Static Utility Methods
     */
    /*
       Method that parses a sphere from a Map<String,String>
       mapping attribute names to their values.

       Each plane is defined by a "normal", a "point", and
       a "color" attribute.

       If the material attribute is missing from the XML
       description of the RTShape, Material.defaultNonReflectiveMaterial
       is set.
     */
    public static Plane parseShape(Map<String,String> leafAttributes) throws IncorrectSceneDescriptionXMLStructureException {
        Vector3D normal = null;
        Vector3D point = null;
        RTColor color = null;
        Material material = null;

        for (Map.Entry<String, String> entry : leafAttributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            switch (attributeName) {
                case "normal" -> normal = SceneDescriptionParser.parseVector3D(attributeValue);
                case "point" -> point = SceneDescriptionParser.parseVector3D(attributeValue);
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
                default -> throw new IncorrectSceneDescriptionXMLStructureException("Undefined attribute in Plane description.");
            }
        }

        if(normal == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'normal' attribute in TriangleMesh description.");
        }
        else if(point == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'point' attribute in TriangleMesh description.");
        }
        else if(color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'color' attribute in TriangleMesh description.");
        }

        /// if missing material in XML, set default
        if(material == null) {
            material = Material.defaultNonReflectiveMaterial;
        }

        return new Plane(normal, point, color, material);
    }

    /**
     * Getters
     */
    public Vector3D getUnitNormal() {
        return this.unitNormal;
    }
    public Vector3D getPointInPlane() {
        return this.pointInPlane;
    }
    public String getShapeID() {
        return Plane.shapeID;
    }
}
