import org.xml.sax.SAXException;
import shading.PhongShader;
import shading.Shader;
import tracing.Ray;
import tracing.Scene;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.RTColor;
import utility.Vector3D;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Class to encapsulate a camera at the origin (0,0,0),
 * pointing in the positive direction of z-axis.
 * along with a screen plane at z=depth, with specified
 * height and screen ratio. The 'camera up' direction is
 * positive direction of y axis, and the coordinate
 * system is right-handed (so x axis is "to the left").
 *
 *                        y
 *                        ^
 *                    x < X z (z is into plane)
 *
 * This is a PINHOLE CAMERA, meaning that we are
 * effectively projecting all shapes onto the screen plane,
 * which may cause distortions.
 *
 * TODO - finite aperture camera in a separate class
 *
 * The camera position and orientation can not be customised,
 * but screen plane depth (z coordinate of the plane),
 * its height, width-to-height ratio, and height in pixels
 * can be varied.
 */
public class Camera {
    private final double screenPlaneHeight;
    /// z component of the plane
    private final double screenPlaneDepth;
    private final double screenPlaneWidthToHeightRatio;
    private final int screenPlaneHeightInPixels;

    private final int reflectionTracingLimit;

    /**
     * Constructors
     */
    /*
       Default constructor.

       Puts the screen plane at z = 2, and makes FOV angles
       equal to 45 degrees in each direction to minimise
       distortion due to a pinhole camera (we're basically
       perspective-projecting shapes onto the screen plane
       which can cause distortions).
     */
    public Camera() {
        this.screenPlaneHeight = 4;
        this.screenPlaneDepth = 2;
        this.screenPlaneWidthToHeightRatio = 1;
        this.screenPlaneHeightInPixels = 1200;

        this.reflectionTracingLimit = 4;
    }
    /*
       Constructor to customise screen plane parameters.
     */
    public Camera(double height, double depth, double widthToHeightRatio, int heightInPixels, int reflectionTracingLimit) {
        this.screenPlaneHeight = height;
        this.screenPlaneDepth = depth;
        this.screenPlaneWidthToHeightRatio = widthToHeightRatio;
        this.screenPlaneHeightInPixels = heightInPixels;

        this.reflectionTracingLimit = reflectionTracingLimit;
    }

    /**
     * Methods
     */
    /*
       Method to render a scene description into a
       digital image, from the point of view of this particular camera.

       Takes the path to the .xml file that describes the scene (e.g. in the
       '/src/main/resources' directory) as an argument
       , and creates a 'result.png' file in the project root directory.
     */
    public BufferedImage render(String sceneDescriptionPath) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        Scene scene = new Scene(sceneDescriptionPath);
        Shader shader = new PhongShader(scene);

        BufferedImage digitalImage = new BufferedImage(this.getScreenPlaneWidthInPixels(), this.screenPlaneHeightInPixels, BufferedImage.TYPE_INT_RGB);
        double pixelWidth = this.getPixelWidth();
        double pixelHeight = this.getPixelHeight();
        for(int y = 0; y < this.screenPlaneHeightInPixels; y++) {
            for (int x = 0; x < this.getScreenPlaneWidthInPixels(); x++) {
                /*Color col = new Color(227, 4, 49, 255);
                digitalImage.setRGB(x,y, col.getRGB());*/

                /*RTColor col = RTColor.cyan;
                digitalImage.setRGB(x,y, col.getRGB());*/

                /// x,y coordinates of pixel from image origin (top left)
                double pixelCenterX = x * pixelWidth + 0.5 * pixelWidth;
                double pixelCenterY = y * pixelHeight + 0.5 * pixelHeight;
                /// transform to x,y coordinates where both x,y axes are in
                /// opposite directions from the standard image axes
                pixelCenterX = this.getScreenPlaneWidth() / 2 - pixelCenterX;
                pixelCenterY = this.screenPlaneHeight / 2 - pixelCenterY;

                /// create a ray to be cast from the camera through the center of the current pixel
                Ray r = new Ray(new Vector3D(0, 0, 0), new Vector3D(pixelCenterX, pixelCenterY, this.screenPlaneDepth));
                //RTColor rayColorValue = r.trace(scene, shader);  <- tracing without reflections
                RTColor rayColorValue = r.traceWithReflections(scene, shader, this.reflectionTracingLimit);

                /// clip the color values to 0.0 to 1.0 range, and store it
                RTColor rayColorValueNormed = rayColorValue.normalised();
                digitalImage.setRGB(x, y, rayColorValueNormed.getRGB());
            }
        }

        return digitalImage;
    }

    /**
     * Getters
     */
    public int getScreenPlaneWidthInPixels() {
        return (int) Math.ceil(this.screenPlaneWidthToHeightRatio * this.screenPlaneHeightInPixels);
    }
    public double getScreenPlaneWidth() {
        return this.screenPlaneHeight * this.screenPlaneWidthToHeightRatio;
    }
    /*
       Getter to compute the height of a single pixel from
       the height of the screen plane and its height in pixels.
     */
    public double getPixelHeight() {
        return this.screenPlaneHeight / this.screenPlaneHeightInPixels;
    }
    /*
       Getter to compute the width of a single pixel from the width
       of the screen plane and its width in pixels.
     */
    public double getPixelWidth() {
        return this.getScreenPlaneWidth() / this.getScreenPlaneWidthInPixels();
    }
    /*
       Getter for the screen plane width to height ratio.
     */
    public double getScreenPlaneWidthToHeightRatio() {
        return this.screenPlaneWidthToHeightRatio;
    }
    /*
       Getter for the screen plane height.
     */
    public double getScreenPlaneHeight() {
        return this.screenPlaneHeight;
    }

    /**
     * Static Utility Methods
     */
    /*
       Method to output a digital image.
       Given a BufferedImage, it creates a 'result.png'
       output image in project root folder.
     */
    public static void saveImage(BufferedImage image) {
        try{
            File f = new File("./result.png");
            ImageIO.write(image, "png", f);
        }
        catch(IOException e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        Camera c = new Camera();
        BufferedImage b = c.render("src/main/resources/scene1.xml");
        Camera.saveImage(b);
    }
}
