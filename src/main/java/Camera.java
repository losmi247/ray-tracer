import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tracing.Scene;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.SceneDescriptionParser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Class to encapsulate a camera at the origin (0,0,0),
 * pointing in the positive direction of z-axis.
 * along with a screen plane at z=depth, with specified
 * height and screen ratio.
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

    /**
     * Constructors
     */
    /*
       Default constructor.
     */
    public Camera() {
        this.screenPlaneHeight = 100;
        this.screenPlaneDepth = 10;
        this.screenPlaneWidthToHeightRatio = 1.5;
        this.screenPlaneHeightInPixels = 320;
    }
    /*
       Constructor to customise screen plane parameters.
     */
    public Camera(double height, double depth, double widthToHeightRatio, int heightInPixels) {
        this.screenPlaneHeight = height;
        this.screenPlaneDepth = depth;
        this.screenPlaneWidthToHeightRatio = widthToHeightRatio;
        this.screenPlaneHeightInPixels = heightInPixels;
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
        Scene scene = new Scene(sceneDescriptionPath, Color.YELLOW, 0.05);

        BufferedImage digitalImage = new BufferedImage(this.getScreenPlaneWidthInPixels(), this.screenPlaneHeightInPixels, BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y < this.screenPlaneHeightInPixels; y++){
            for(int x = 0; x < this.getScreenPlaneWidthInPixels(); x++){
                Color col = new Color(227, 4, 49, 255);
                digitalImage.setRGB(x,y, col.getRGB());
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
        BufferedImage b = c.render("src/main/resources/scene.xml");
        Camera.saveImage(b);
    }
}
