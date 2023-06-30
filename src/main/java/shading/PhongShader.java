package shading;

import shapes.RTShape;
import tracing.Light;
import tracing.Scene;
import utility.RTColor;
import utility.Vector3D;

import java.util.ArrayList;
import java.util.Vector;

/**
 * A class that implements
 * the Phong's shading model.
 *
 * Each shader has a list of lights in the scene
 * that does not change throughout rendering.
 * Shaders are immutable.
 *
 * Shaders are created exclusively from a specific Scene object,
 * so each shader has a scene that it is intended for, so that
 * it can contain the specific list of lights from that scene
 * (instead of having to pass the list of lights as an argument
 * each time, you just instantiate a separate Shader using one
 * list of lights) and hopefully evaluate the model faster
 * during rendering.
 */

public class PhongShader implements Shader {
    private final ArrayList<Light> lights;
    private final RTColor ambientColor;
    private final double ambientComponentCoefficient;
    private final double diffuseComponentCoefficient;
    private final double specularComponentCoefficient;

    /**
     * Constructors
     */
    /*
       Constructor from a Scene, that sets the ambient color
       and the three coefficients to default values.

       The values of the coefficients have been experimented
       with to minimise the chance of color values leaving
       the range 0.0 to 1.0, so it is recommended to use this
       default constructor.
     */
    public PhongShader(Scene scene) {
        this.lights = scene.getLights();
        this.ambientColor = new RTColor(0.0, 1.0, 0.0, 1.0);
        this.ambientComponentCoefficient = 0.05;
        this.diffuseComponentCoefficient = 0.9;
        this.specularComponentCoefficient = 0.5;
    }
    /*
       Constructor from a Scene, that customises the ambient
       color and the three coefficients.
     */
    public PhongShader(Scene scene, RTColor ambientColor, double ka, double kd, double ks) {
        this.lights = scene.getLights();
        this.ambientColor = RTColor.black;
        this.ambientComponentCoefficient = ka;
        this.diffuseComponentCoefficient = kd;
        this.specularComponentCoefficient = ks;
    }

    /**
     * Methods
     */
    /*
       Method that evaluates the Phong's shading model at a given point
       on the surface of the given shape.

       Phong's shading model consists of three components:
            Ambient component (models indirect illumination)
            Diffuse component (models Lambertian illumination)
            Specular component (models imperfect specular illumination)
     */
    public RTColor evaluateShadingModel(RTShape shape, Vector3D point) {
        /// ambient illumination
        RTColor ambientComponent = this.getAmbientComponent();

        /// diffuse illumination
        RTColor diffuseComponent = RTColor.blank;
        for (Light light : this.lights) {
            /// TODO - check if light is occluded by another object (cast and trace a shadow ray)
            /// TODO - check if light is occluded by this object itself (self shadowing)
            RTColor lightContribution = this.getDiffuseComponent(shape, point, light);
            diffuseComponent = diffuseComponent.added(lightContribution);
        }

        /// TODO - specular illumination
        RTColor specularComponent = RTColor.blank;

        return ambientComponent.added(diffuseComponent).added(specularComponent);
    }
    /*
       Method to evaluate the ambient illumination component.
     */
    private RTColor getAmbientComponent() {
        return this.ambientColor.scaled(this.ambientComponentCoefficient);
    }
    /*
       Method to evaluate the diffuse illumination component,
       for a given Light object.
     */
    private RTColor getDiffuseComponent(RTShape shape, Vector3D point, Light light) {
        /// unit vector from the given point to this light
        Vector3D lightDirection = light.getPosition().added(point.negated()).normalised();
        /// unit normal to surface of the shape at the given point
        Vector3D unitNormal = shape.getUnitNormalAt(point);

        /// take max with 0, so that only the side of the surface facing the light is illuminated
        double diffuseCoefficient = this.diffuseComponentCoefficient * light.getIntensity() * Math.max(0.0, lightDirection.scalarProduct(unitNormal));

        return shape.getColorAt(point).scaled(diffuseCoefficient);
    }
}
