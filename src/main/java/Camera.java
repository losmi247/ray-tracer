import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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
 * pointing in the positive direction of z-axis,
 * along with a screen plane at z=depth, with specified
 * height and screen ratio.
 */
public class Camera {
    private final double screenPlaneHeight = 100;
    private final double screenPlaneDepth = 10;
    private final double screenPlaneWidthToHeightRatio = 1.5;
    private final int screenPlaneHeightInPixels = 320;
    private BufferedImage digitalImage;

    /**
     * Constructors
     */
    public Camera() {
        this.digitalImage = new BufferedImage(this.getScreenPlaneWidthInPixels(),
                                            this.screenPlaneHeightInPixels,
                                            BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Methods
     */
    /*
       Method to set the pixel value (0-255) at a particular position.
     */
    private void setPixel(int x, int y, int colorValue) {
        this.digitalImage.setRGB(x, y, colorValue);
    }
    /*
       The main method for rendering a scene description into a
       digital image.
       Takes the path to the .xml file (e.g. in the
       '/src/main/resources' directory) as an argument
       , and creates a 'result.png' file in the main project directory.
     */
    public static void render(String sceneDescriptionPath) throws ParserConfigurationException, IOException, SAXException {
        SceneDescriptionParser sceneParser = new SceneDescriptionParser(sceneDescriptionPath);

        Camera c = new Camera();

        for(int y = 0; y < c.screenPlaneHeightInPixels; y++){
            for(int x = 0; x < c.getScreenPlaneWidthInPixels(); x++){
                Color col = new Color(227, 4, 49, 255);
                c.setPixel(x,y, col.getRGB());
            }
        }

        /// output the rendered image
        try{
            File f = new File("./result.png");
            ImageIO.write(c.digitalImage, "png", f);
        }
        catch(IOException e) {
            throw new RuntimeException();
        }
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

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        render("src/main/resources/scene.xml");
    }
}
