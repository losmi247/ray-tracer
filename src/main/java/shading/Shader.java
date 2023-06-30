package shading;

import shapes.RTShape;
import utility.RTColor;
import utility.Vector3D;

/**
 * An interface for a shader - any class that encapsulates
 * a shading model and is used to evaluate the model at a given
 * point (e.g. Phong's shading model).
 *
 * Apart from these methods, every shader
 * will have a list of lights in the scene as a field.
 *
 * Shaders are created exclusively from a specific Scene object,
 * so each shader has a Scene that it is intended for, so that
 * it can contain the specific list of lights from that scene
 * (instead of having to pass the list of lights as an argument
 * each time, you just instantiate a separate Shader using one
 * list of lights) and hopefully evaluate the model faster
 * during rendering.
 */

public interface Shader {
    /*
       Method that evaluates the specific shading model
       at the given point on the surface of the given shape.
     */
    RTColor evaluateShadingModel(RTShape shape, Vector3D point);
}
