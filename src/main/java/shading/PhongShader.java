package shading;

import shapes.RTShape;
import tracing.Light;
import tracing.Scene;
import utility.Vector3D;

import java.awt.Color;
import java.util.ArrayList;

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
    private final Color ambientColor;
    private final double ambientComponentCoefficient;
    private final double diffuseComponentCoefficient;
    private final double specularComponentCoefficient;

    /**
     * Constructors
     */
    /*
       Constructor from a Scene, that sets the ambient color
       and the three coefficients to default values.
     */
    public PhongShader(Scene scene) {
        this.lights = scene.getLights();
        this.ambientColor = Color.orange;
        this.ambientComponentCoefficient = 0.01;
        this.diffuseComponentCoefficient = 0.8;
        this.specularComponentCoefficient = 0.2;
    }
    /*
       Constructor from a Scene, that customises the ambient
       color and the three coefficients.
     */
    public PhongShader(Scene scene, Color ambientColor, double ka, double kd, double ks) {
        this.lights = scene.getLights();
        this.ambientColor = Color.BLACK;
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
    public Color evaluateShadingModel(RTShape shape, Vector3D point) {
        /// TODO
        return null;
    }
}
