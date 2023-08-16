package utility;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import rendering.shapes.RTShape;
import rendering.shapes.Sphere;
import rendering.tracing.Light;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.RTColor;
import rendering.utility.SceneDescriptionParser;
import rendering.utility.Vector3D;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SceneDescriptionParserTest {
    static SceneDescriptionParser descriptionParser;

    @BeforeAll
    static void prepareScene() throws ParserConfigurationException, IOException, SAXException {
        descriptionParser = new SceneDescriptionParser("src/main/resources/scene descriptions/testScene.xml");
    }

    @Test
    void parseShapesTest() throws IncorrectSceneDescriptionXMLStructureException, IOException {
        ArrayList<RTShape> res = descriptionParser.parseShapes();
        assertEquals(2,res.size());
        Sphere s1 = (Sphere) res.get(0);
        Sphere s2 = (Sphere) res.get(1);

        Vector3D v1 = s1.getCenter();
        assertEquals(0, v1.getX(), 1e-9);
        assertEquals(0, v1.getY(), 1e-9);
        assertEquals(0, v1.getZ(), 1e-9);

        assertEquals(5, s1.getRadius(), 1e-9);

        RTColor c1 = s1.getColor();
        assertEquals(0, c1.getRedInt());
        assertEquals(255, c1.getGreenInt());
        assertEquals(255, c1.getBlueInt());



        v1 = s2.getCenter();
        assertEquals(10, v1.getX(), 1e-9);
        assertEquals(10, v1.getY(), 1e-9);
        assertEquals(10, v1.getZ(), 1e-9);

        assertEquals(1, s2.getRadius(), 1e-9);

        c1 = s2.getColor();
        assertEquals(56, c1.getRedInt());
        assertEquals(200, c1.getGreenInt());
        assertEquals(47, c1.getBlueInt());
    }

    @Test
    void parseLightsTest() throws IncorrectSceneDescriptionXMLStructureException {
        ArrayList<Light> res = descriptionParser.parseLights();
        assertEquals(2,res.size());
        Light l1 = res.get(0);
        Light l2 = res.get(1);

        Vector3D v1 = l1.getPosition();
        assertEquals(10, v1.getX(), 1e-9);
        assertEquals(10, v1.getY(), 1e-9);
        assertEquals(10, v1.getZ(), 1e-9);

        assertEquals(0.8, l1.getIntensity(), 1e-9);

        RTColor c1 = l1.getColor();
        assertEquals(0, c1.getRedInt());
        assertEquals(0, c1.getGreenInt());
        assertEquals(255, c1.getBlueInt());


        v1 = l2.getPosition();
        assertEquals(20, v1.getX(), 1e-9);
        assertEquals(0, v1.getY(), 1e-9);
        assertEquals(0, v1.getZ(), 1e-9);

        assertEquals(0.5, l2.getIntensity(), 1e-9);

        c1 = l2.getColor();
        assertEquals(255, c1.getRedInt());
        assertEquals(0, c1.getGreenInt());
        assertEquals(255, c1.getBlueInt());
    }

    @Test
    void parseVector3DTest() throws IncorrectSceneDescriptionXMLStructureException {
        Vector3D v = SceneDescriptionParser.parseVector3D("(12,-6,843)");
        assertEquals(12.0, v.getX(), 1e-9);
        assertEquals(-6.0, v.getY(), 1e-9);
        assertEquals(843.0, v.getZ(), 1e-9);

        v = SceneDescriptionParser.parseVector3D("(-16624.413423,-6.534534,6456.35345)");
        assertEquals(-16624.413423, v.getX(), 1e-9);
        assertEquals(-6.534534, v.getY(), 1e-9);
        assertEquals(6456.35345, v.getZ(), 1e-9);
    }

    @Test
    void parseColorTest() throws IncorrectSceneDescriptionXMLStructureException {
        RTColor v = SceneDescriptionParser.parseColor("(12,0,156)");
        assertEquals(12, v.getRedInt());
        assertEquals(0, v.getGreenInt());
        assertEquals(156, v.getBlueInt());

        v = SceneDescriptionParser.parseColor("(127,0,19)");
        assertEquals(127, v.getRedInt());
        assertEquals(0, v.getGreenInt());
        assertEquals(19, v.getBlueInt());
    }
}