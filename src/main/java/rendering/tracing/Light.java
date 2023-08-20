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
 *
 * TODO - use Intensity/4r^2pi formula for decreasing light intensity
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
       Getters to get the position, specular color, and intensity of the light source.
     */
    Vector3D getPosition();
    RTColor getColor();
    double getIntensity();
}
