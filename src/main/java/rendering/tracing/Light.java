package rendering.tracing;

import rendering.utility.RTColor;
import rendering.utility.Vector3D;

/**
 * Interface for a light source.
 *
 * Point light sources have XML
 * description nodes named "point-light",
 * and sphere light sources have XML
 * description nodes named "sphere-light".
 */

public interface Light {
    /*
       Method that takes an intersection point and a scene and checks how much this
       light source is occluded at the given intersection point by RTShape's of this
       scene.

       Point light sources return either 0 or 1 and cast one shadow ray for this.
       Sphere light source return any occlusion coefficient in [0,1] by casting
       random shadow rays.
     */
    double getOcclusionCoefficient(Intersection intersection, Scene scene);

    /*
       Method that takes a point in 3D space, and returns the illumination intensity
       at that given point due to this light source. Each light source has a
       defined intensity (power) at its position - depending on how this method is
       implemented, the illumination intensity can either stay the same everywhere
       if you return the same intensity as the one at the light's position, or it
       can fall off in some way as distance increases.
    */
    double getIlluminationIntensityAt(Vector3D point);

    /*
       Getters to get the position, specular color, and intensity of the light source at
       its position.
     */
    Vector3D getPosition();
    RTColor getColor();
    double getIntensity();
}
