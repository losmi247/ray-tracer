package shapes;
import shading.Material;
import tracing.Intersection;
import utility.RTColor;
import utility.Vector3D;
import tracing.Ray;

/**
 * Interface for a shape that can be included in
 * the XML scene description.
 *
 * Apart from the methods in this interface, each shape
 * should have a unique String identifier that is the
 * same as its corresponding XML element name.
 *
 * EACH SHAPE MUST ALSO HAVE A
 *      public static RTShape parseShape(Map<String,String> attributes){}
 * TO PARSE THE SHAPE FROM A MAP MAPPING ITS ATTRIBUTES NAMES
 * TO THEIR VALUES. If the "material" attribute is missing from the XML
 * description, this method should set the material to the default
 * Material.defaultNonReflectiveMaterial class varible.
 *
 * Each shape defines the unit normal, color, and material (shading
 * coefficients) at every point on its surface.
 */
public interface RTShape {
    /*
       Method to get the String shapeID of the particular
       implementation of the RTShape interface.

       It must be consistent, i.e. return the same string
       on every invocation.
     */
    String getShapeID();

    /**
     * Methods For Tracing
     */
    /*
       Method that returns the point (as a Vector3D)
       of intersection of the Ray r with the RTShape,
       bundled with the intersected shape into an
       Intersection object, or returns null if there
       is no intersection.
     */
    Intersection intersect(Ray ray);

    /**
     * Methods For Shading
     */
    /*
       Method that returns the unit normal to the surface
       of the RTshape at the given point on the surface
       the RTshape.

       The unit normal must point outwards by convention.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    Vector3D getUnitNormalAt(Vector3D point);
    /*
       Method that returns the (diffuse) color of the shape
       at a given point on RTShape's surface.

       In other words, each RTShape must define its diffuse
       color at each point on its surface.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    RTColor getColorAt(Vector3D point);
    /*
       Method that returns the material (shading
       coefficients) at a given point on the surface of
       the RTShape.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    Material getMaterialAt(Vector3D point);
}