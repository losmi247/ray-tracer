package shading;

import shapes.RTShape;
import tracing.Intersection;
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
    private final double ambientComponentCoefficient;
    private final double diffuseComponentCoefficient;
    private final double specularComponentCoefficient;
    private final double phongRoughnessCoefficient;

    private final double reflectionCoefficient;

    /**
     * Constructors
     */
    /*
       Constructor from a Scene, that sets the four coefficients
       to default values.

       The values of the coefficients have been experimented
       with to minimise the chance of color values leaving
       the range 0.0 to 1.0, so it is recommended to use this
       default constructor.
     */
    public PhongShader(Scene scene) {
        this.scene = scene;
        this.ambientComponentCoefficient = 0.4;
        this.diffuseComponentCoefficient = 0.9;
        this.specularComponentCoefficient = 0.6;

        this.phongRoughnessCoefficient = 40;

        this.reflectionCoefficient = 0.4;
    }
    /*
       Constructor from a Scene, that allows
       customisation of the four coefficients.
     */
    public PhongShader(Scene scene, double ka, double kd, double ks, double n, double r) {
        this.scene = scene;
        this.ambientComponentCoefficient = ka;
        this.diffuseComponentCoefficient = kd;
        this.specularComponentCoefficient = ks;

        this.phongRoughnessCoefficient = n;

        this.reflectionCoefficient = r;
    }

    /**
     * Methods
     */
    /*
       Method that evaluates the Phong's shading model at a given point
       on the surface of the given shape, i.e. a given Intersection
       object.

       Phong's shading model consists of three components:
            Ambient component (models indirect illumination)
            Diffuse component (models Lambertian illumination)
            Specular component (models imperfect specular illumination)
     */
    public RTColor evaluateShadingModel(Intersection intersection) {
        RTShape intersectedShape = intersection.getIntersectedShape();
        Vector3D intersectionPoint = intersection.getIntersectionPoint();
        ArrayList<Light> lights = this.scene.getLights();

        /// ambient illumination
        RTColor ambientComponent = this.getAmbientComponent(intersection);

        /// diffuse illumination
        RTColor diffuseComponent = RTColor.blank;
        /// specular illumination
        RTColor specularComponent = RTColor.blank;
        for (Light light : lights) {
            ShadowRay shadowRay = new ShadowRay(intersection, light);
            if(!shadowRay.lightSourceOccluded(this.scene)) {
                /// diffuse illumination
                RTColor lightDiffuseContribution = this.getDiffuseComponent(intersection, light);
                diffuseComponent = diffuseComponent.added(lightDiffuseContribution);

                /// specular illumination
                RTColor lightSpecularContribution = this.getSpecularComponent(intersection, light);
                specularComponent = specularComponent.added(lightSpecularContribution);
            }
        }

        return ambientComponent.added(diffuseComponent).added(specularComponent);
    }
    /*
       Method to evaluate the ambient illumination component,
       for a given RTShape.
     */
    private RTColor getAmbientComponent(Intersection intersection) {
        RTShape intersectedShape = intersection.getIntersectedShape();
        Vector3D intersectionPoint = intersection.getIntersectionPoint();
        return intersectedShape.getColorAt(intersectionPoint).scaled(this.ambientComponentCoefficient);
    }
    /*
       Method to evaluate the diffuse illumination component,
       for a given Light object.

       Diffuse component is view independent, i.e. does not depend
       on the position of the camera.
     */
    private RTColor getDiffuseComponent(Intersection intersection, Light light) {
        RTShape intersectedShape = intersection.getIntersectedShape();
        Vector3D intersectionPoint = intersection.getIntersectionPoint();

        /// unit vector from the given point to this light
        Vector3D lightDirection = light.getPosition().added(intersectionPoint.negated()).normalised();
        /// unit normal to surface of the shape at the given point
        Vector3D unitNormal = intersectedShape.getUnitNormalAt(intersectionPoint);

        /// take max with 0, so that only the side of the surface facing the light is illuminated
        double diffuseCoefficient = this.diffuseComponentCoefficient * light.getIntensity() * Math.max(0.0, lightDirection.scalarProduct(unitNormal));

        return intersectedShape.getColorAt(intersectionPoint).scaled(diffuseCoefficient);
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
    private RTColor getSpecularComponent(Intersection intersection, Light light) {
        RTShape intersectedShape = intersection.getIntersectedShape();
        Vector3D intersectionPoint = intersection.getIntersectionPoint();

        /// unit vector from the given point to this light
        Vector3D lightDirection = light.getPosition().added(intersectionPoint.negated()).normalised();
        /// unit normal to surface of the shape at the given point
        Vector3D unitNormal = intersectedShape.getUnitNormalAt(intersectionPoint);
        /// unit vector from the given point to the camera
        Vector3D viewDirection = (new Vector3D(0,0,0)).added(intersectionPoint.negated()).normalised();
        /// perfect specular reflection direction of the light at this point
        Vector3D reflectionDirection = lightDirection.reflected(unitNormal);

        /// take max with 0, so that only the side of the surface facing the light is illuminated
        double phongCoefficient = Math.pow(Math.max(0.0, reflectionDirection.scalarProduct(viewDirection)), this.phongRoughnessCoefficient);
        double specularCoefficient = this.specularComponentCoefficient * light.getIntensity() * phongCoefficient;

        return light.getColor().scaled(specularCoefficient);
    }
    /*
       Method that mixes the color value obtained by shading
       the intersection point (intersectionColor)
       with the color value obtained by tracing the
       reflected ray (reflectionColor).

       It uses this shader's reflectionCoefficient, and simply
       adds the 'reflectionColor' scaled by that coefficient
       (dims it) to the original 'intersectionColor'.
     */
    public RTColor mixReflectedColor(RTColor intersectionColor, RTColor reflectionColor) {
        return intersectionColor.scaled(1 - this.reflectionCoefficient).added(reflectionColor.scaled(this.reflectionCoefficient));
    }
}
