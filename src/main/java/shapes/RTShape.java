package shapes;
import utility.RTColor;
import utility.Vector3D;
import tracing.Ray;

/**
 * Interface for a shape that can be included in
 * the XML scene description.
 *
 * Apart from the methods in this interface, each shape
 * should have a unique String identifier same as its
 * corresponding XML element name.
 *
 * EACH SHAPE MUST ALSO HAVE A
 *      public static RTShape parseShape(Map<String,String> attributes){}
 * TO PARSE THE SHAPE FROM A MAP MAPPING ITS ATTRIBUTES NAMES
 * TO THEIR VALUES.
 *
 * TODO - each shape should have an instance variable that controls the
 *        material properties (coefficients for Phong shading, i.e.
 *        ambient, diffuse, specular, reflectivity)
 */
public interface RTShape {
    /*
       Method that returns the point (as a Vector3D)
       of intersection of the Ray r with the RTShape,
       or throws the checked exception NoIntersection
       if there is no intersection.
     */
    Vector3D intersect(Ray ray);
    /*
       Method that returns the unit normal to the surface
       of the shape at the given point on the surface of
       the shape.

       The unit normal must point outwards by convention.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    Vector3D getUnitNormalAt(Vector3D point);
    /*
       Method that returns the (diffuse) color of the shape
       at a given point on its surface.

       In other words, each RTShape must define its diffuse
       color at each point on its surface.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    RTColor getColorAt(Vector3D point);
}