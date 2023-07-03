package shading;

import shapes.RTShape;
import tracing.Light;
import tracing.Scene;
import tracing.ShadowRay;
import utility.RTColor;
import utility.Vector3D;

import java.util.ArrayList;

/**
 * A class that implements Phong's shading model.
 *
 * This shader is created exclusively from a specific Scene object,
 * so each instance has a scene that it is intended for, so that
 * it always refers to a specific list of lights from that scene
 * (which it needs for calculating the diffuse and specular
 * illumination components) and a specific list of RTShape's
 * from that scene (which it needs for checking if shadow rays
 * intersect a shape before reaching the light source).
 *
 * This shader is immutable - the scene it refers to and its
 * parameters are never changed.
 */

public class PhongShader implements Shader {
    private final Scene scene;
    private final RTColor ambientColor;
    private final double ambientComponentCoefficient;
    private final double diffuseComponentCoefficient;
    private final double specularComponentCoefficient;
    private final double phongRoughnessCoefficient;

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
        this.scene = scene;
        this.ambientColor = new RTColor(0.0, 1.0, 0.0, 1.0);
        this.ambientComponentCoefficient = 0.1;
        this.diffuseComponentCoefficient = 0.9;
        this.specularComponentCoefficient = 0.5;

        this.phongRoughnessCoefficient = 8;
    }
    /*
       Constructor from a Scene, that customises the ambient
       color and the three coefficients.
     */
    public PhongShader(Scene scene, RTColor ambientColor, double ka, double kd, double ks, double n) {
        this.scene = scene;
        this.ambientColor = ambientColor;
        this.ambientComponentCoefficient = ka;
        this.diffuseComponentCoefficient = kd;
        this.specularComponentCoefficient = ks;

        this.phongRoughnessCoefficient = n;
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
    public RTColor evaluateShadingModel(RTShape intersectedShape, Vector3D intersectionPoint) {
        ArrayList<Light> lights = this.scene.getLights();

        /// ambient illumination
        RTColor ambientComponent = this.getAmbientComponent();

        /// diffuse illumination
        RTColor diffuseComponent = RTColor.blank;
        /// specular illumination
        RTColor specularComponent = RTColor.blank;
        for (Light light : lights) {
            ShadowRay shadowRay = new ShadowRay(intersectionPoint, intersectedShape, light);
            if(!shadowRay.lightSourceOccluded(this.scene)) {
                /// diffuse illumination
                RTColor lightDiffuseContribution = this.getDiffuseComponent(intersectedShape, intersectionPoint, light);
                diffuseComponent = diffuseComponent.added(lightDiffuseContribution);

                /// specular illumination
                RTColor lightSpecularContribution = this.getSpecularComponent(intersectedShape, intersectionPoint, light);
                specularComponent = specularComponent.added(lightSpecularContribution);
            }
        }

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

       Diffuse component is view independent, i.e. does not depend
       on the position of the camera.
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
    /*
       Method to evaluate the specular illumination component,
       for a given Light object.

       Specular component is view-dependent, it creates shininess due to
       imperfect specular reflections.

       The specular component uses the (specular) color of the light source,
       not the (diffuse) color of the shape itself.

       TODO - The camera is currently fixed at (0,0,0) - think about being able to change camera position
     */
    private RTColor getSpecularComponent(RTShape shape, Vector3D point, Light light) {
        /// unit vector from the given point to this light
        Vector3D lightDirection = light.getPosition().added(point.negated()).normalised();
        /// unit normal to surface of the shape at the given point
        Vector3D unitNormal = shape.getUnitNormalAt(point);
        /// unit vector from the given point to the camera
        Vector3D viewDirection = (new Vector3D(0,0,0)).added(point.negated()).normalised();
        /// perfect specular reflection direction of the light at this point
        Vector3D reflectionDirection = lightDirection.reflected(unitNormal);

        /// take max with 0, so that only the side of the surface facing the light is illuminated
        double phongCoefficient = Math.pow(Math.max(0.0, reflectionDirection.scalarProduct(viewDirection)), this.phongRoughnessCoefficient);
        double specularCoefficient = this.specularComponentCoefficient * light.getIntensity() * phongCoefficient;

        return light.getColor().scaled(specularCoefficient);
    }
}
