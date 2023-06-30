package utility;

/**
 * A class for a color in RGBA format.
 * The class is inspired by java.awt.Color, but
 * it was modified to suit the need for adding
 * and scaling colors during the evaluation
 * of the shading model in rendering.
 *
 * Everything is assumed to be in the
 * default sRGB color space.
 *
 * RTColor is immutable.
 */

public class RTColor {
    private final double r;
    private final double g;
    private final double b;
    private final double a;

    /**
     * Predefined colors with max opacity 1.0
     */
    public static final RTColor white     = new RTColor(1.0, 1.0, 1.0);
    public static final RTColor gray      = new RTColor(0.5, 0.5, 0.5);
    public static final RTColor black     = new RTColor(0, 0, 0);

    public static final RTColor red       = new RTColor(1.0, 0, 0);
    public static final RTColor green     = new RTColor(0, 1.0, 0);
    public static final RTColor blue      = new RTColor(0, 0, 1.0);

    public static final RTColor cyan      = new RTColor(0, 1.0, 1.0);
    public static final RTColor magenta   = new RTColor(1.0, 0, 1.0);
    public static final RTColor yellow    = new RTColor(1.0, 1.0, 0);

    /**
     * Constructors
     *
     */
    /*
       Constructor that sets the opacity to
       default max value.
     */
    public RTColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1.0;
    }
    /*
       Constructor that customises the opacity.
     */
    public RTColor(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    /*
       Constructor from integer values in range 0-255, that
       sets the opacity to max value.
     */
    public RTColor(int r, int g, int b) {
        this.r = r / 255.0;
        this.g = g / 255.0;
        this.b = b / 255.0;
        this.a = 1.0;
    }
    /*
       Constructor from integer values in range 0-255.
     */
    public RTColor(int r, int g, int b, int a) {
        this.r = r / 255.0;
        this.g = g / 255.0;
        this.b = b / 255.0;
        this.a = a / 255.0;
    }

    /**
     * Methods
     */
    /*
       Method to scale color values by a given coefficient.
     */
    public RTColor scaled(double scalingCoefficient) {
        return new RTColor(this.r * scalingCoefficient, this.g * scalingCoefficient, this.b * scalingCoefficient);
    }
    /*
       Method to add another RTColor to this color, used for
       the shading models.
     */
    public RTColor added(RTColor rtcolor) {
        return new RTColor(this.r + rtcolor.r, this.g + rtcolor.g, this.b + rtcolor.b, this.a  + rtcolor.a);
    }
    /*
       Method to normalise color values in an RTColor object,
       i.e. clip them to range 0.0 to 1.0.

       Basically a form of tone mapping.
     */
    public RTColor normalised() {
        return new RTColor(Math.max(0.0, Math.min(this.r, 1.0)), Math.max(0.0, Math.min(this.g, 1.0)), Math.max(0.0, Math.min(this.b, 1.0)), Math.max(0.0, Math.min(this.a, 1.0)));
    }
    /*
       Method to generate the 32-bit int RGBA value from the 4 channels'
       values, where each of A,R,G,B occupy 8 bits, in that order.
     */
    public int getRGB() {
        int A = this.getAlphaInt();
        int R = this.getRedInt();
        int G = this.getGreenInt();
        int B = this.getBlueInt();
        return ((A & 0xFF) << 24) |
                ((R & 0xFF) << 16) |
                ((G & 0xFF) << 8)  |
                ((B & 0xFF) << 0);
    }

    /**
     * Getters
     */
    /*
       Getters for channel values
       in range 0.0 to 1.0.
     */
    public double getRed() {
        return this.r;
    }
    public double getGreen() {
        return this.g;
    }
    public double getBlue() {
        return this.b;
    }
    public double getAlpha() {
        return this.a;
    }
    /*
       Getters for channel values
       in range 0-255.
     */
    public int getRedInt() {
        return (int) Math.floor(this.r * 255);
    }
    public int getGreenInt() {
        return (int) Math.floor(this.g * 255);
    }
    public int getBlueInt() {
        return (int) Math.floor(this.b * 255);
    }
    public int getAlphaInt() {
        return (int) Math.floor(this.a * 255);
    }
}
