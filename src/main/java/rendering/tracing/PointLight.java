package rendering.tracing;

import rendering.utility.RTColor;
import rendering.utility.Vector3D;

/**
 * Class for a point light source.
 *
 * Point light sources have XML description
 * nodes named "point-light".
 */
public class PointLight implements Light {
    private final Vector3D position;
    private final RTColor lightSpecularColor;
    /// the intensity is the power of the light source at its position
    private final double intensity;

    public PointLight(Vector3D position, RTColor lightSpecularColor, double intensity){
        this.position = position;
        this.lightSpecularColor = lightSpecularColor;
        this.intensity = intensity;
    }

    /**
     * Methods
     */
    /*
       Method that spawns and casts the necessary shadow rays to check if this
       light source is occluded at the given intersection point by any of the
       RTShape's in the given scene.

       Only one shadow ray is cast, from the intersection point to the location
       of the point light source, and if it hits an RTShape before target, 0 is
       returned as occlusion coefficient, otherwise 1.
     */
    public double getOcclusionCoefficient(Intersection intersection, Scene scene) {
        /// spawn a shadow ray to this point light source
        ShadowRay shadowRay = new ShadowRay(intersection, this);
        /// check if the shadow ray intersects any RTShape before reaching the target
        if(shadowRay.targetPointOccluded(scene)) {
            /// point light source is occluded
            return 0;
        }
        else {
            /// point light source is not occluded
            return 1;
        }
    }
    /*
       Method that takes a point in 3D space, and returns the illumination intensity
       at that given point due to this light source.

       This method calculates the distance from the point light source to the given
       point and makes the illumination intensity fall off as 1/distance^2.
    */
    public double getIlluminationIntensityAt(Vector3D point) {
        return this.intensity / (4 * this.position.added(point.negated()).magnitudeSquared() * Math.PI);
    }

    /**
     * Getters
     */
    public Vector3D getPosition() {
        return this.position;
    }
    public RTColor getColor() {
        return this.lightSpecularColor;
    }
    public double getIntensity() {
        return this.intensity;
    }
}
