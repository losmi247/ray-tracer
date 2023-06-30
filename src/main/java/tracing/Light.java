package tracing;

import utility.RTColor;
import utility.Vector3D;

/**
 * Class for a light source.
 */
public class Light {
    private Vector3D position;
    private RTColor lightSpecularColor;
    private double intensity;

    public Light(Vector3D position, RTColor lightSpecularColor, double intensity){
        this.position = position;
        this.lightSpecularColor = lightSpecularColor;
        this.intensity = intensity;
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
