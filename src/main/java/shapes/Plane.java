package shapes;

import tracing.Ray;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.RTColor;
import utility.SceneDescriptionParser;
import utility.Vector3D;

import java.util.Map;

/**
 * Class for a plane defined by a unit normal to it,
 * and a single point that belongs to the plane, with
 * the same color at each point on its surface.
 */

public class Plane implements RTShape {
    public static final String shapeID = "plane";
    private Vector3D unitNormal;
    private Vector3D pointInPlane;
    private RTColor diffuseColor;

    /**
     * Constructors
     */
    /*
       Constructor from a normal to surface of the plane (not
       necessarily normalised), a point that belongs to it,
       and its diffuse color.
     */
    public Plane(Vector3D normal, Vector3D point, RTColor color) {
        this.unitNormal = normal.normalised();
        this.pointInPlane = point;
        this.diffuseColor = color;
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
     */
    public Vector3D intersect(Ray ray) {
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
                return ray.pointAt(s);
            }
            else {
                return null;
            }
        }
    }
    /*
       Method that returns the unit normal at a given point on
       the surface of the plane.

       The unit normal must point outwards by convention, which
       for a plane is the direction in which the unit normal is
       defined when passed to the constructor. For a plane,
       unit normal is the same everywhere.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Vector3D getUnitNormalAt(Vector3D point) {
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

    /**
     * Static Utility Methods
     */
    /*
       Method that parses a sphere from a Map<String,String>
       mapping attribute names to their values.

       Each plane is defined by a "normal", a "point", and
       a "color" attribute.
     */
    public static Plane parseShape(Map<String,String> attributes) throws IncorrectSceneDescriptionXMLStructureException {
        Vector3D normal = null;
        Vector3D point = null;
        RTColor color = null;

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            switch (attributeName) {
                case "normal" -> normal = SceneDescriptionParser.parseVector3D(attributeValue);
                case "point" -> point = SceneDescriptionParser.parseVector3D(attributeValue);
                case "color" -> color = SceneDescriptionParser.parseColor(attributeValue);
                default -> throw new IncorrectSceneDescriptionXMLStructureException();
            }
        }

        if(normal == null || point == null || color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        return new Plane(normal, point, color);
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
    public RTColor getDiffuseColor() {
        return this.diffuseColor;
    }
}
