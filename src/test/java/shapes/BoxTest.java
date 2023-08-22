package shapes;

import org.junit.jupiter.api.Test;
import rendering.shapes.Box;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.Matrix4D;

import java.util.Map;

class BoxTest {

    @Test
    void parseShape() throws IncorrectSceneDescriptionXMLStructureException {
        Box.parseShape(Map.of("minX", "1", "maxX", "3", "minY", "-2", "maxY", "3", "minZ", "10", "maxZ", "40", "color", "(255,0,255)", "material", "default-nonreflective"), Matrix4D.identity);
    }
}