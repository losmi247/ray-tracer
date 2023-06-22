package shapes;

import jdk.jshell.spi.ExecutionControl;
import tracing.Ray;
import utility.Vector3D;
import java.awt.Color;

/**
 * A sphere object.
 */
public class Sphere implements Shape {
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
       parameter s in P = O + s * D ray equation)
     */
    public Vector3D intersect(Ray r) throws NoIntersection {
        throw new RuntimeException();
    }
}
