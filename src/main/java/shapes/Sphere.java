package shapes;

import tracing.Ray;
import utility.Vector3D;
import java.awt.Color;

/**
 * A sphere object.
 */
public class Sphere implements Shape {
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
       gives the closest intersection (minimum value of
       parameter s in P = O + s * D ray equation) if an
       intersection exists, or 'null' if no intersection
       exists (don't want to use exceptions for control
       flow when rendering).
     */
    public Vector3D intersect(Ray r) {
        throw new RuntimeException();
    }
}
