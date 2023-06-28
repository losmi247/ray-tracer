package tracing;

import java.awt.Color;
import utility.Vector3D;

/**
 * Class for a light source.
 */
public class Light {
    private Vector3D position;
    private Color lightSpecularColor;
    private double intensity;

    public Light(Vector3D position, Color lightSpecularColor, double intensity){
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
    public Color getColor() {
        return this.lightSpecularColor;
    }
    public double getIntensity() {
        return this.intensity;
    }
}
