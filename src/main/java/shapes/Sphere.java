package shapes;

import tracing.Ray;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.SceneDescriptionParser;
import utility.Vector3D;
import java.awt.Color;
import java.util.Map;

/**
 * A sphere object.
 */
public class Sphere implements RTShape {
    private static final String shapeID = "sphere";
    private Vector3D center;
    private double radius;
    private Color diffuseColor;

    public Sphere(Vector3D center, double radius, Color diffuseColor) {
        this.center = center;
        this.radius = radius;
        this.diffuseColor = diffuseColor;
    }

    /**
     * Methods
     */
    /*
       Method that intersects a ray with the sphere. It
       gives the closest intersection (minimum non-negative
       value of parameter s in P = O + s * D ray equation)
       if an intersection exists, or 'null' if no intersection
       exists (don't want to use exceptions for control
       flow when rendering).
     */
    public Vector3D intersect(Ray ray) {
        /// quadratic equation a*s^2 + b*s + c = 0
        Vector3D D = ray.getDirection();
        Vector3D O = ray.getOrigin();
        Vector3D C = this.center;
        double r = this.radius;

        double a = D.magnitudeSquared();
        assert(a > 0);
        double b = 2 * D.scalarProduct(O.added(C.negated()));
        double c = O.added(C.negated()).magnitudeSquared() - r*r;

        double discriminant = b*b - 4*a*c;

        if(discriminant < 0) { /// no intersection
            return null;
        }
        else {
            double s1 = (-b + Math.sqrt(discriminant)) / (2*a);
            double s2 = (-b - Math.sqrt(discriminant)) / (2*a);

            if(s1 < 0 && s2 < 0) {
                return null;
            }

            if(s1 < 0) {
                return ray.pointAt(s2);
            }

            if(s2 < 0) {
                return ray.pointAt(s1);
            }

            return ray.pointAt(Math.min(s1,s2));
        }
    }
    /*
       Method that parses a sphere from a map mapping attribute names
       to their values.
     */
    public static Sphere parseShape(Map<String, String> attributes) throws IncorrectSceneDescriptionXMLStructureException {
        Vector3D center = null;
        double radius = -1;
        Color color = null;
        for(Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            if(attributeName.equals("center")) {
                center = SceneDescriptionParser.parseVector3D(attributeValue);
            }
            if(attributeName.equals("radius")) {
                radius = Double.parseDouble(attributeValue);
            }
            if(attributeName.equals("color")){
                color = SceneDescriptionParser.parseColor(attributeValue);
            }
        }

        if(center == null || radius < 0 || color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        return new Sphere(center, radius, color);
    }

    /**
     * Getters
     */
    public Vector3D getCenter() {
        return this.center;
    }
    public double getRadius() {
        return this.radius;
    }
    public Color getColor() {
        return this.diffuseColor;
    }
}
