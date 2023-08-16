package utility;

import org.junit.jupiter.api.Test;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import static org.junit.jupiter.api.Assertions.*;

class RTColorTest {

    @Test
    void scaled() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        RTColor nc = c.scaled(2);
        assertEquals(0.4, nc.getRed(), 1e-9);
        assertEquals(1, nc.getGreen(), 1e-9);
        assertEquals(0.2, nc.getBlue(), 1e-9);
    }

    @Test
    void added() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        RTColor nc = c.added(new RTColor(0.3, 0.6, 0.05));
        assertEquals(0.5, nc.getRed(), 1e-9);
        assertEquals(1.1, nc.getGreen(), 1e-9);
        assertEquals(0.15, nc.getBlue(), 1e-9);
    }

    @Test
    void normalised() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        RTColor nc = c.normalised();
        assertEquals(0.2, nc.getRed(), 1e-9);
        assertEquals(0.5, nc.getGreen(), 1e-9);
        assertEquals(0.1, nc.getBlue(), 1e-9);

        c = new RTColor(-0.2, 13.2, 0.1);
        nc = c.normalised();
        assertEquals(0.0, nc.getRed(), 1e-9);
        assertEquals(1.0, nc.getGreen(), 1e-9);
        assertEquals(0.1, nc.getBlue(), 1e-9);
    }

    @Test
    void getRGB() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        int rgb = c.getRGB();
        assertEquals((51<<16)+(127<<8)+25,rgb);
    }

    @Test
    void getRedInt() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        assertEquals(51, c.getRedInt());
    }

    @Test
    void getGreenInt() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        assertEquals(127, c.getGreenInt());
    }

    @Test
    void getBlueInt() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        assertEquals(25, c.getBlueInt());
    }

    @Test
    void scaledVector3DTest() {
        RTColor c = new RTColor(0.2, 0.5, 0.1);
        RTColor nc = c.scaledVector3D(new Vector3D(0.5, 0.2, 0.3));
        assertEquals(0.1, nc.getRed(), 1e-9);
        assertEquals(0.1, nc.getGreen(), 1e-9);
        assertEquals(0.03, nc.getBlue(), 1e-9);
    }
}