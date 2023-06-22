package shapes;

import jdk.jshell.spi.ExecutionControl;
import tracing.Ray;
import utility.Vector3D;

public class Sphere implements Shape {
    private Vector3D center;
    private double radius;

    public Sphere(Vector3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vector3D intersect(Ray r) throws NoIntersection {
        throw new RuntimeException();
    }
}
