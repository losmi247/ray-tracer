import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rendering.Camera;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {
    static Camera c;
    @BeforeAll
    static void prepare() {
        c = new Camera();
    }

    @Test
    void getScreenPlaneWidthInPixelsTest() {
        assertEquals(c.getScreenPlaneWidth() / c.getPixelWidth(), c.getScreenPlaneWidthInPixels(), 1e-9);
    }

    @Test
    void getScreenPlaneWidthTest() {
        assertEquals(c.getScreenPlaneHeight() * c.getScreenPlaneWidthToHeightRatio(), c.getScreenPlaneWidth(), 1e-9);
    }

    @Test
    void getPixelWidthTest() {
        assertEquals(c.getPixelHeight() * c.getScreenPlaneWidthToHeightRatio(), c.getPixelWidth(), 1e-9);
    }
}