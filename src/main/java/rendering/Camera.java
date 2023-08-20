package rendering;

import org.xml.sax.SAXException;

import rendering.shading.PhongShader;
import rendering.shading.Shader;
import rendering.tracing.Ray;
import rendering.tracing.Scene;
import rendering.utility.IncorrectSceneDescriptionXMLStructureException;
import rendering.utility.RTColor;
import rendering.utility.Vector3D;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import javafx.concurrent.Task;

import com.aparapi.Kernel;
import com.aparapi.Range;

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

    /// limit for tracing reflected rays
    private final int reflectionTracingLimit;
    /* 
       Antialiasing by jittered super-sampling,
       i.e. we split each pixel into a regular grid
       of 'samplesPerPixelSide' X 'samplesPerPixelSide'
       sub-pixels, then sample one random point from
       each sub-pixel.

       If samplesPerPixelSide = 1, then only a single
       ray is cast exactly through the center of every
       pixel.
     */
    private final int samplesPerPixelSide;

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
        this.screenPlaneHeightInPixels = 800;

        this.reflectionTracingLimit = 5;
        this.samplesPerPixelSide = 3;

        /// can crank up to resolution 1000, 10 reflections, 5x5 samples
    }
    /*
       Constructor to customise screen plane parameters.
     */
    public Camera(double height, double depth, double widthToHeightRatio, int heightInPixels, int reflectionTracingLimit, int samplesPerPixelSide) {
        this.screenPlaneHeight = height;
        this.screenPlaneDepth = depth;
        this.screenPlaneWidthToHeightRatio = widthToHeightRatio;
        this.screenPlaneHeightInPixels = heightInPixels;

        this.reflectionTracingLimit = reflectionTracingLimit;
        this.samplesPerPixelSide = samplesPerPixelSide;
    }

    /**
     * Methods
     */
    /*
       Method to render a scene description into a
       digital image, from the point of view of this particular camera.

       Takes the path to the .xml file that describes the scene (e.g. in the
       '/src/main/resources' directory) as an argument
       , and returns the rendered BufferedImage.

       If samplesPerPixelSide = 1, then only a single
       ray is cast exactly through the center of every
       pixel, otherwise jittered super-sampling is performed
       on a 'samplesPerPixelSide' X 'samplesPerPixelSide' regular
       grid of sub-pixels.

       This method renders the image using a single thread, i.e. on
       a single core of the CPU.

       This method can be only used for pure rendering without the
       JavaFX UI, it does not connect to it, i.e. does not have an
       optional Consumer<Double> argument that allows the user to
       update the rendering progress in the UI like the 
       'renderWithCPUCoreParallelization' does.
     */
    public BufferedImage render(String sceneDescriptionPath) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        Scene scene = new Scene(sceneDescriptionPath);
        Shader shader = new PhongShader(scene);

        BufferedImage digitalImage = new BufferedImage(this.getScreenPlaneWidthInPixels(), this.screenPlaneHeightInPixels, BufferedImage.TYPE_INT_RGB);
        int screenPlaneWidthInPixels = this.getScreenPlaneWidthInPixels();
        double pixelWidth = this.getPixelWidth();
        double pixelHeight = this.getPixelHeight();
        double subPixelWidth = pixelWidth / this.samplesPerPixelSide;
        double subPixelHeight = pixelHeight / this.samplesPerPixelSide;

        /// setup timing and progress
        long startTime = System.currentTimeMillis();
        int milestone = (this.screenPlaneHeightInPixels * screenPlaneWidthInPixels) / 20;

        for(int y = 0; y < this.screenPlaneHeightInPixels; y++) {
            for (int x = 0; x < screenPlaneWidthInPixels; x++) {

                /// if we want just one sample per pixel side, just cast one ray through pixel center
                if(this.samplesPerPixelSide == 1) {
                    /// x,y coordinates of pixel center from image origin (top left)
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

                    /// clip the color values to 0.0 to 1.0 range, and store them
                    RTColor rayColorValueNormed = rayColorValue.normalised();
                    digitalImage.setRGB(x, y, rayColorValueNormed.getRGB());
                }
                else { /// otherwise perform antialiasing by jittered super-sampling
                    Random rnd = new Random();
                    RTColor finalColorValue = RTColor.blank;
                    for(int i = 0; i < this.samplesPerPixelSide; i++) {
                        for(int j = 0; j < this.samplesPerPixelSide; j++) {
                            /// x,y coordinates of the point in this sub-pixel
                            // which we'll shoot the ray through, from image origin (top left)
                            double subpixelSampleX = x * pixelWidth + j * subPixelWidth + rnd.nextDouble() * subPixelWidth;
                            double subpixelSampleY = y * pixelHeight + i * subPixelHeight + rnd.nextDouble() * subPixelHeight;

                            /// transform to x,y coordinates where both x,y axes are in
                            /// opposite directions from the standard image axes
                            subpixelSampleX = this.getScreenPlaneWidth() / 2 - subpixelSampleX;
                            subpixelSampleY = this.screenPlaneHeight / 2 - subpixelSampleY;

                            /// create a ray to be cast from the camera through the selected sample point
                            Ray r = new Ray(new Vector3D(0, 0, 0), new Vector3D(subpixelSampleX, subpixelSampleY, this.screenPlaneDepth));
                            RTColor rayColorValue = r.traceWithReflections(scene, shader, this.reflectionTracingLimit);

                            /// add this ray's contribution
                            finalColorValue = finalColorValue.added(rayColorValue);
                        }
                    }

                    /// take the average of samples' contributions
                    finalColorValue = finalColorValue.scaled(1 / (double) (this.samplesPerPixelSide * this.samplesPerPixelSide));

                    /// clip the color values to 0.0 to 1.0 range, and store them
                    RTColor finalColorValueNormed = finalColorValue.normalised();
                    digitalImage.setRGB(x, y, finalColorValueNormed.getRGB());
                }

                /// update progress
                if((x+y != 0) && (y*screenPlaneWidthInPixels + x) % milestone == 0) {
                    double done = (double) (y*screenPlaneWidthInPixels + x) / (double) (this.screenPlaneHeightInPixels * screenPlaneWidthInPixels);
                    double eta = (double) (System.currentTimeMillis() - startTime) * ((1 - done) / done);
                    System.out.println(done * 100 + "% done. ETA: " + Double.toString(Math.round(eta/ 1000)) + " seconds");
                }
            }
        }

        System.out.println("100% done. Total time: " + (double) (System.currentTimeMillis() - startTime) / 1000 + " seconds");

        return digitalImage;
    }
    /*
       Method that returns a JavaFX Task that renders a scene description into a
       digital image, from the point of view of this particular camera. It uses
       the 'renderWithCPUCoreParallelization' method to do so, and gives it a
       Consumer lambda that updates the progress property of the task created
       from the 'renderWithCPUCoreParallelization' method.

       The JavaFX Task created by this method first renders the given scene description
       into a BufferedImage which it then saves at the default location
       "./src/main/resources/rendered images/result.png".
     */
    public Task<Void> getRenderWithCPUCoreParallelizationTask(String sceneDescriptionPath) {
        return new Task<Void>() {
            @Override
            public Void call() throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
                /// render the scene description, and give the method a consumer to update the progress property of the task
                BufferedImage digitalImage = Camera.this.renderWithCPUCoreParallelization(sceneDescriptionPath, new Consumer<Double>() {
                    @Override
                    public void accept(Double progress) {
                        updateProgress(progress, 1);
                    }
                });

                Camera.saveImage(digitalImage);

                return null;
            }
        };
    }
    /*
       Method to render a scene description into a
       digital image, from the point of view of this particular camera.

       Takes the path to the .xml file that describes the scene (e.g. in the
       '/src/main/resources' directory) as an argument
       , and returns the rendered BufferedImage.

       If samplesPerPixelSide = 1, then only a single
       ray is cast exactly through the center of every
       pixel, otherwise jittered super-sampling is performed
       on a 'samplesPerPixelSide' X 'samplesPerPixelSide' regular
       grid of sub-pixels.

       This method uses Java Streams API to distribute rays to be traced
       among the cores of the CPU, i.e. rays through different pixels
       are traced in parallel in different threads.

       This method has an additional optional Consumer<Double> argument
       'progressUpdaterConsumer' that can be passed to this method and
       used to update the rendering progress in any chosen way in which
       its 'accept' method is implemented (this method gives the
       Consumer the progress as a real value between 0 and 1). For instance,
       the 'getRenderWithGPUCoreParallelization' method creates a JavaFX Task
       for rendering on GPU cores and uses the Consumer argument to update
       the progress property of the Task from
       'getRenderWithGPUCoreParallelizationTask', i.e. the
       'renderWithGPUCoreParallelization' method.
     */
    public BufferedImage renderWithCPUCoreParallelization(String sceneDescriptionPath, Consumer<Double> progressUpdaterConsumer) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        Scene scene = new Scene(sceneDescriptionPath);
        Shader shader = new PhongShader(scene);

        BufferedImage digitalImage = new BufferedImage(this.getScreenPlaneWidthInPixels(), this.screenPlaneHeightInPixels, BufferedImage.TYPE_INT_RGB);
        int screenPlaneWidthInPixels = this.getScreenPlaneWidthInPixels();
        double pixelWidth = this.getPixelWidth();
        double pixelHeight = this.getPixelHeight();
        double subPixelWidth = pixelWidth / this.samplesPerPixelSide;
        double subPixelHeight = pixelHeight / this.samplesPerPixelSide;

        /// setup timing and progress
        long startTime = System.currentTimeMillis();
        AtomicInteger progress = new AtomicInteger();
        /// if a progress updater is given, set progress to 0
        if(progressUpdaterConsumer != null) {
            progressUpdaterConsumer.accept(.0);
        }

        /// create a list of pixel positions, pixel (i,j) is coded as i*screenPlaneWidthInPixels + j
        ArrayList<Integer> pixelPositions = new ArrayList<>();
        for(int y = 0; y < this.screenPlaneHeightInPixels; y++) {
            for (int x = 0; x < screenPlaneWidthInPixels; x++) {
                pixelPositions.add(y*screenPlaneWidthInPixels + x);
            }
        }

        /// parallelize tracing of rays across CPU cores for each pixel using Java Stream API
        pixelPositions.parallelStream().forEach((Integer codedPixelPosition) -> {
            /// decode pixel position
            int y = codedPixelPosition / screenPlaneWidthInPixels;
            int x = codedPixelPosition - y*screenPlaneWidthInPixels;

            /// now for each pixel, trace all rays through it

            /// if we want just one sample per pixel side, just cast one ray through pixel center
            if(this.samplesPerPixelSide == 1) {
                /// x,y coordinates of pixel center from image origin (top left)
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

                /// clip the color values to 0.0 to 1.0 range, and store them
                RTColor rayColorValueNormed = rayColorValue.normalised();
                digitalImage.setRGB(x, y, rayColorValueNormed.getRGB());
            }
            else { /// otherwise perform antialiasing by jittered super-sampling
                Random rnd = new Random();
                RTColor finalColorValue = RTColor.blank;
                for(int i = 0; i < this.samplesPerPixelSide; i++) {
                    for(int j = 0; j < this.samplesPerPixelSide; j++) {
                        /// x,y coordinates of the point in this sub-pixel
                        // which we'll shoot the ray through, from image origin (top left)
                        double subpixelSampleX = x * pixelWidth + j * subPixelWidth + rnd.nextDouble() * subPixelWidth;
                        double subpixelSampleY = y * pixelHeight + i * subPixelHeight + rnd.nextDouble() * subPixelHeight;

                        /// transform to x,y coordinates where both x,y axes are in
                        /// opposite directions from the standard image axes
                        subpixelSampleX = this.getScreenPlaneWidth() / 2 - subpixelSampleX;
                        subpixelSampleY = this.screenPlaneHeight / 2 - subpixelSampleY;

                        /// create a ray to be cast from the camera through the selected sample point
                        Ray r = new Ray(new Vector3D(0, 0, 0), new Vector3D(subpixelSampleX, subpixelSampleY, this.screenPlaneDepth));
                        RTColor rayColorValue = r.traceWithReflections(scene, shader, this.reflectionTracingLimit);

                        /// add this ray's contribution
                        finalColorValue = finalColorValue.added(rayColorValue);
                    }
                }

                /// take the average of samples' contributions
                finalColorValue = finalColorValue.scaled(1 / (double) (this.samplesPerPixelSide * this.samplesPerPixelSide));

                /// clip the color values to 0.0 to 1.0 range, and store them
                RTColor finalColorValueNormed = finalColorValue.normalised();
                digitalImage.setRGB(x, y, finalColorValueNormed.getRGB());
            }

            /// update progress
            double done = (double) progress.incrementAndGet() / (double) (this.screenPlaneHeightInPixels * screenPlaneWidthInPixels);
            if(Math.abs((int)(done*100) - (done*100)) < 1e-9 && (int)(done*100) % 5 == 0) {
                double eta = (double) (System.currentTimeMillis() - startTime) * ((1 - done) / done);
                System.out.println((int)(done * 100) + "% done. ETA: " + Double.toString(Math.round(eta/ 1000)) + " seconds");
            }
            /// if a progress updater is given, update progress
            if(progressUpdaterConsumer != null) {
                progressUpdaterConsumer.accept(done);
            }
        });

        System.out.println("Total time: " + (double) (System.currentTimeMillis() - startTime) / 1000 + " seconds");

        return digitalImage;
    }
    /*
       Method that returns a JavaFX Task that renders a scene description into a
       digital image, from the point of view of this particular camera. It uses
       the 'renderWithGPUCoreParallelization' method to do so, and gives it a
       Consumer lambda that updates the progress property of the task created
       from the 'renderWithGPUCoreParallelization' method.
    
       The JavaFX Task created by this method first renders the given scene description
       into a BufferedImage which it then saves at the default location
       "./src/main/resources/rendered images/result.png".
     */
    public Task<Void> getRenderWithGPUCoreParallelizationTask(String sceneDescriptionPath) {
        return new Task<Void>() {
            @Override 
            public Void call() throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
                /// render the scene description, and give the method a consumer to update the progress property of the task
                BufferedImage digitalImage = Camera.this.renderWithGPUCoreParallelization(sceneDescriptionPath, new Consumer<Double>() {
                    @Override
                    public void accept(Double progress) {
                        updateProgress(progress, 1);
                    }
                });

                Camera.saveImage(digitalImage);

                return null;
            }
        }; 
    }
    /*
       Method to render a scene description into a
       digital image, from the point of view of this particular camera.

       Takes the path to the .xml file that describes the scene (e.g. in the
       '/src/main/resources' directory) as an argument
       , and returns the rendered BufferedImage.

       If samplesPerPixelSide = 1, then only a single
       ray is cast exactly through the center of every
       pixel, otherwise jittered super-sampling is performed
       on a 'samplesPerPixelSide' X 'samplesPerPixelSide' regular
       grid of sub-pixels.

       This method uses Aparapi to distribute rays to be traced
       among the cores of the GPU, i.e. rays through different pixels
       are traced in parallel in different threads.

       This method has an additional optional Consumer<Double> argument
       'progressUpdaterConsumer' that can be passed to this method and
       used to update the rendering progress in any chosen way in which
       its 'accept' method is implemented (this method gives the
       Consumer the progress as a real value between 0 and 1). For instance,
       the 'getRenderWithCPUCoreParallelization' method creates a JavaFX Task
       for rendering and uses the Consumer argument to update the progress
       property of the Task from this method.
     */
    public BufferedImage renderWithGPUCoreParallelization(String sceneDescriptionPath, Consumer<Double> progressUpdaterConsumer) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        Scene scene = new Scene(sceneDescriptionPath);
        Shader shader = new PhongShader(scene);

        BufferedImage digitalImage = new BufferedImage(this.getScreenPlaneWidthInPixels(), this.screenPlaneHeightInPixels, BufferedImage.TYPE_INT_RGB);
        int screenPlaneWidthInPixels = this.getScreenPlaneWidthInPixels();
        double pixelWidth = this.getPixelWidth();
        double pixelHeight = this.getPixelHeight();
        double subPixelWidth = pixelWidth / this.samplesPerPixelSide;
        double subPixelHeight = pixelHeight / this.samplesPerPixelSide;

        /// setup timing and progress
        long startTime = System.currentTimeMillis();
        AtomicInteger progress = new AtomicInteger();
        /// if a progress updater is given, set progress to 0
        if(progressUpdaterConsumer != null) {
            progressUpdaterConsumer.accept(.0);
        }

        /// Pixel (i,j) is coded as i*screenPlaneWidthInPixels + j (i is horizontal, j is vertical component, from top left origin pixel (0,0)).
        /// There are this.screenPlaneHeightInPixels * screenPlaneWidthInPixels pixels.

        /// Create a (this.screenPlaneHeightInPixels X screenPlaneWidthInPixels) matrix of integers where we will store 32 bit RGB values computed
        /// in the data parallel ray tracing algorithm below, so that we can write them to the previously created BufferedImage later. We can't 
        /// write the color values in the BufferedImage inside the Kernel because then the algorithm would not be data parallel as many threads
        /// would have to access same BufferedImage object.
        int[][] computedRGBValues = new int[this.screenPlaneHeightInPixels][screenPlaneWidthInPixels];

        /// Extend Kernel class to create a data parallel algorithm to run on GPU.
        /// We have to specify 'Camera.this' because inside the inner class (that extends
        /// Kernel), 'this' refers to this Kernel object, while 'Camera.this' refers to
        /// this outer Camera object.
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int codedPixelPosition = getGlobalId();

                /// decode pixel position
                int y = codedPixelPosition / screenPlaneWidthInPixels;
                int x = codedPixelPosition - y*screenPlaneWidthInPixels;

                /// now for each pixel, trace all rays through it

                /// if we want just one sample per pixel side, just cast one ray through pixel center
                if(Camera.this.samplesPerPixelSide == 1) {
                    /// x,y coordinates of pixel center from image origin (top left)
                    double pixelCenterX = x * pixelWidth + 0.5 * pixelWidth;
                    double pixelCenterY = y * pixelHeight + 0.5 * pixelHeight;
                    /// transform to x,y coordinates where both x,y axes are in
                    /// opposite directions from the standard image axes
                    pixelCenterX = Camera.this.getScreenPlaneWidth() / 2 - pixelCenterX;
                    pixelCenterY = Camera.this.screenPlaneHeight / 2 - pixelCenterY;

                    /// create a ray to be cast from the camera through the center of the current pixel
                    Ray r = new Ray(new Vector3D(0, 0, 0), new Vector3D(pixelCenterX, pixelCenterY, Camera.this.screenPlaneDepth));
                    //RTColor rayColorValue = r.trace(scene, shader);  <- tracing without reflections
                    RTColor rayColorValue = r.traceWithReflections(scene, shader, Camera.this.reflectionTracingLimit);

                    /// clip the color values to 0.0 to 1.0 range
                    RTColor rayColorValueNormed = rayColorValue.normalised();

                    /// now store the computed 32 bit RGB value for later instead of writing to BufferedImage immediately
                    //digitalImage.setRGB(x, y, rayColorValueNormed.getRGB());
                    computedRGBValues[x][y] = rayColorValueNormed.getRGB();
                }
                else { /// otherwise perform antialiasing by jittered super-sampling
                    Random rnd = new Random();
                    RTColor finalColorValue = RTColor.blank;
                    for(int i = 0; i < Camera.this.samplesPerPixelSide; i++) {
                        for(int j = 0; j < Camera.this.samplesPerPixelSide; j++) {
                            /// x,y coordinates of the point in this sub-pixel
                            // which we'll shoot the ray through, from image origin (top left)
                            double subpixelSampleX = x * pixelWidth + j * subPixelWidth + rnd.nextDouble() * subPixelWidth;
                            double subpixelSampleY = y * pixelHeight + i * subPixelHeight + rnd.nextDouble() * subPixelHeight;

                            /// transform to x,y coordinates where both x,y axes are in
                            /// opposite directions from the standard image axes
                            subpixelSampleX = Camera.this.getScreenPlaneWidth() / 2 - subpixelSampleX;
                            subpixelSampleY = Camera.this.screenPlaneHeight / 2 - subpixelSampleY;

                            /// create a ray to be cast from the camera through the selected sample point
                            Ray r = new Ray(new Vector3D(0, 0, 0), new Vector3D(subpixelSampleX, subpixelSampleY, Camera.this.screenPlaneDepth));
                            RTColor rayColorValue = r.traceWithReflections(scene, shader, Camera.this.reflectionTracingLimit);

                            /// add this ray's contribution
                            finalColorValue = finalColorValue.added(rayColorValue);
                        }
                    }

                    /// take the average of samples' contributions
                    finalColorValue = finalColorValue.scaled(1 / (double) (Camera.this.samplesPerPixelSide * Camera.this.samplesPerPixelSide));

                    /// clip the color values to 0.0 to 1.0 range
                    RTColor finalColorValueNormed = finalColorValue.normalised();

                     /// now store the computed 32 bit RGB value for later instead of writing to BufferedImage immediately
                    //digitalImage.setRGB(x, y, finalColorValueNormed.getRGB());
                    computedRGBValues[x][y] = finalColorValueNormed.getRGB();
                }

                /// update progress
                double done = (double) progress.incrementAndGet() / (double) (Camera.this.screenPlaneHeightInPixels * screenPlaneWidthInPixels);
                if(Math.abs((int)(done*100) - (done*100)) < 1e-9 && (int)(done*100) % 5 == 0) {
                    double eta = (double) (System.currentTimeMillis() - startTime) * ((1 - done) / done);
                    System.out.println((int)(done * 100) + "% done. ETA: " + Double.toString(Math.round(eta/ 1000)) + " seconds");
                }
                /// if a progress updater is given, update progress
                if(progressUpdaterConsumer != null) {
                    progressUpdaterConsumer.accept(done);
                }
            }
        };

        /// Execute the Kernel on the GPU by converting Java bytecode to OpenCL.
        /// The Kernel's 'run' method will be executed once for each value of
        /// global id (getGlobalId(), i.e. codedPixelPosition) in the given
        /// Range, i.e. from 0 to (#pixels - 1), where the #pixels is as shown
        /// before, this.screenPlaneHeightInPixels * screenPlaneWidthInPixels.
        kernel.execute(Range.create(this.screenPlaneHeightInPixels * screenPlaneWidthInPixels));
        /// dispose resources
        kernel.dispose();

        System.out.println("Total time: " + (double) (System.currentTimeMillis() - startTime) / 1000 + " seconds");

        /// finally write the 32 bit RGB values to the BufferedImage
        for(int x = 0; x < screenPlaneWidthInPixels; x++) {
            for(int y = 0; y < this.screenPlaneHeightInPixels; y++) {
                digitalImage.setRGB(x, y, computedRGBValues[x][y]);
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
       output image in the 'rendered images' folder
       inside project 'resources' folder.
     */
    public static void saveImage(BufferedImage image) {
        try{
            File f = new File("./src/main/resources/rendered images/result.png");
            ImageIO.write(image, "png", f);
        }
        catch(IOException e) {
            throw new RuntimeException();
        }
    }

    /// run this to render a .xml description
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, IncorrectSceneDescriptionXMLStructureException {
        Camera c = new Camera();
        /// don't need a progress bar here, so set progress updater to null
        BufferedImage b = c.renderWithCPUCoreParallelization("src/main/resources/scene descriptions/scene4.xml", null);
        Camera.saveImage(b);
    }
}
