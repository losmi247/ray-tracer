package shapes;

import shading.Material;
import tracing.Intersection;
import tracing.Ray;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.RTColor;
import utility.SceneDescriptionParser;
import utility.Vector3D;
import java.util.Map;

/**
 * Class for a sphere defined by its center and
 * radius, with the same color and material at
 * each point on its surface.
 */
public class Sphere implements RTShape {
    public static final String shapeID = "sphere";

    private Vector3D center;
    private double radius;

    private RTColor diffuseColor;
    private Material material;

    public Sphere(Vector3D center, double radius, RTColor diffuseColor, Material material) {
        this.center = center;
        this.radius = radius;
        this.diffuseColor = diffuseColor;
        this.material = material;
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

       The point of intersection is bundled together with the
       intersected shape, into an Intersection object.
     */
    public Intersection intersect(Ray ray) {
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
                return new Intersection(this, ray.pointAt(s2));
            }

            if(s2 < 0) {
                return new Intersection(this, ray.pointAt(s1));
            }

            return new Intersection(this, ray.pointAt(Math.min(s1,s2)));
        }
    }
    /*
       Method that returns the unit normal at a given point on
       the surface of the sphere, given as an Intersection object.

       The unit normal must point outwards by convention.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Vector3D getUnitNormalAt(Intersection intersection) {
        /// vector from center of the sphere to the given point
        /// is exactly the normal vector to surface of the sphere
        return intersection.getIntersectionPoint().added(this.center.negated()).normalised();
    }
    /*
       Method that returns the diffuse color of the sphere at
       a given point on its surface.

       This sphere has the same diffuse color everywhere.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public RTColor getColorAt(Vector3D point) {
        return this.diffuseColor;
    }
    /*
       Method that returns the material (shading
       coefficients) at a given point on the surface of
       the sphere.

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

       Each sphere is defined by a "center", "radius", "color",
       and a "material" attribute.

       If the material attribute is missing from the XML
       description of the RTShape, Material.defaultNonReflectiveMaterial
       is set.
     */
    public static Sphere parseShape(Map<String, String> attributes) throws IncorrectSceneDescriptionXMLStructureException {
        Vector3D center = null;
        double radius = -1;
        RTColor color = null;
        Material material = null;
        for(Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            switch (attributeName) {
                case "center" -> center = SceneDescriptionParser.parseVector3D(attributeValue);
                case "radius" -> radius = Double.parseDouble(attributeValue);
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

        if(center == null || radius < 0 || color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        /// if missing material in XML, set default
        if(material == null) {
            material = Material.defaultNonReflectiveMaterial;
        }

        return new Sphere(center, radius, color, material);
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
    public RTColor getColor() {
        return this.diffuseColor;
    }
    public String getShapeID() {
        return Sphere.shapeID;
    }
}
