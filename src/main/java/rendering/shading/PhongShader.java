package rendering.shading;

import rendering.shapes.RTShape;
import rendering.tracing.Intersection;
import rendering.tracing.Light;
import rendering.tracing.Scene;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

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
 * This shader is immutable - the scene it refers to
 * is never changed.
 */

public class PhongShader implements Shader {
    private final Scene scene;

    /**
     * Constructors
     */
    /*
       Constructor from a Scene.
     */
    public PhongShader(Scene scene) {
        this.scene = scene;
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
            /* Check if this light source is occluded at this intersection point, and
               get the coefficient by which to scale down the color value at this
               intersection point.
               This light source can be a point or a sphere light source, but thanks
               to dynamic polymorphism we can invoke 'lightSourceOccluded' method that
               they both implement. If it is a point light source, this method will
               return either 0 or 1 (so we can avoid evaluating diffuse and specular
               illumination components if it's 0), and if it is a sphere light, this
               method returns a coefficient in [0,1] which is the proportion of
               randomly generated shadow rays that are occluded (for soft shadows).
             */
            /// do not calculate other components if light source is completely occluded
            double lightOcclusionCoefficient = light.getOcclusionCoefficient(intersection, this.scene);
            if(lightOcclusionCoefficient > 0) {
                /// get diffuse illumination from this light source
                RTColor lightDiffuseContribution = this.getDiffuseComponent(intersection, light);
                /// scale down the diffuse illumination from this light source by its occlusion coefficient
                lightDiffuseContribution = lightDiffuseContribution.scaled(lightOcclusionCoefficient);
                /// add to diffuse component
                diffuseComponent = diffuseComponent.added(lightDiffuseContribution);

                /// get specular illumination from this light source
                RTColor lightSpecularContribution = this.getSpecularComponent(intersection, light);
                /// scale down the specular illumination from this light source by its occlusion coefficient
                lightSpecularContribution = lightSpecularContribution.scaled(lightOcclusionCoefficient);
                /// add to specular component
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
        /// scale each channel of the ambient color by the Vector3D ambient coefficient
        return intersectedShape.getColorAt(intersectionPoint).scaledVector3D(intersectedShape.getMaterialAt(intersectionPoint).getAmbientCoefficient());
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
        Vector3D unitNormal = intersectedShape.getUnitNormalAt(intersection);

        /// multiply by illumination intensity at the intersection point due to the given light source
        /// take max with 0, so that only the side of the surface facing the light is illuminated
        double diffuseMultiplier = light.getIlluminationIntensityAt(intersectionPoint) * Math.max(0.0, lightDirection.scalarProduct(unitNormal));

        /// scale the diffuse color by the multiplier, and scale each color channel by diffuse coefficient
        return intersectedShape.getColorAt(intersectionPoint).scaled(diffuseMultiplier)
                .scaledVector3D(intersectedShape.getMaterialAt(intersectionPoint).getDiffuseCoefficient());
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
        Vector3D unitNormal = intersectedShape.getUnitNormalAt(intersection);
        /// unit vector from the given point to the camera
        Vector3D viewDirection = (new Vector3D(0,0,0)).added(intersectionPoint.negated()).normalised();
        /// perfect specular reflection direction of the light at this point
        Vector3D reflectionDirection = lightDirection.reflected(unitNormal);

        Material material = intersectedShape.getMaterialAt(intersectionPoint);

        /// take max with 0, so that only the side of the surface facing the light is illuminated
        double phongMultiplier = Math.pow(Math.max(0.0, reflectionDirection.scalarProduct(viewDirection)), material.getPhongRoughnessCoefficient());
        /// multiply by illumination intensity at the intersection point due to the given light source
        double specularMultiplier = light.getIlluminationIntensityAt(intersectionPoint) * phongMultiplier;

        return light.getColor().scaled(specularMultiplier)
                .scaledVector3D(material.getSpecularCoefficient());
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
    public RTColor mixReflectedColor(RTColor intersectionColor, RTColor reflectionColor, double reflectionCoefficient) {
        return intersectionColor.scaled(1 - reflectionCoefficient).added(reflectionColor.scaled(reflectionCoefficient));
    }
}
