package shapes;

import de.javagl.obj.ObjData;
import shading.Material;
import tracing.Ray;
import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.RTColor;
import utility.SceneDescriptionParser;
import utility.Vector3D;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

/**
 * Class for a mesh of triangles.
 *
 * TODO - implement a bounding box for the TriangleMesh, and first check
 *        if ray hits the bounding box to optimise tracing
 */

public class TriangleMesh implements RTShape {
    public static String shapeID = "triangle-mesh";

    /// the vertices of the mesh, in object coordinates
    private ArrayList<Vector3D> vertices;
    /// the normals to the mesh at each vertex, found by averaging adjacent face (triangle) normals
    private ArrayList<Vector3D> vertexNormals;
    /// the triangles that the vertices form
    private ArrayList<Triangle> triangleFaces;

    private RTColor diffuseColor;
    private Material material;

    /**
     * Constructors
     */
    /*
       Constructor from mesh description as a Wavefront OBJ file, its color, and
       its material.

       An open source Wavefront OBJ parser (https://github.com/javagl/Obj/tree/master)
       is used to extract mesh info.
     */
    public TriangleMesh(String pathToObjFile, RTColor color, Material material) throws IOException {
        InputStream objInputStream = new FileInputStream(pathToObjFile);
        Obj obj = ObjReader.read(objInputStream);

        float[] vertexCoordinates = ObjData.getVerticesArray(obj);
        float[] vertexNormalsCoordinates = ObjData.getNormalsArray(obj);
        int[] faceVertexIndices = ObjData.getFaceVertexIndicesArray(obj);
        int[] faceVertexNormalsIndices = ObjData.getFaceNormalIndicesArray(obj);

        /// initialise list of vertices by grouping coordinates three by three
        this.vertices = new ArrayList<>();
        for(int i = 0; i < vertexCoordinates.length; i += 3) {
            this.vertices.add(new Vector3D(vertexCoordinates[3*i], vertexCoordinates[3*i+1], vertexCoordinates[3*i+2]));
        }

        /// initialise list of vertex normals by grouping coordinates three by three
        this.vertexNormals = new ArrayList<>();
        for(int i = 0; i < vertexNormalsCoordinates.length; i += 3) {
            this.vertexNormals.add(new Vector3D(vertexNormalsCoordinates[3*i], vertexNormalsCoordinates[3*i+1], vertexNormalsCoordinates[3*i+2]));
        }

        /// initialise list of triangles
        this.triangleFaces = new ArrayList<>();
        for(int i = 0; i < faceVertexIndices.length; i++) {
            this.triangleFaces.add(
                    new Triangle(
                            this.vertices.get(faceVertexIndices[3*i]), this.vertices.get(faceVertexIndices[3*i+1]), this.vertices.get(faceVertexIndices[3*i+2]),
                            this.vertexNormals.get(faceVertexNormalsIndices[3*i]), this.vertexNormals.get(faceVertexNormalsIndices[3*i+1]), this.vertexNormals.get(faceVertexNormalsIndices[3*i+2]),
                            color, material)
            );
        }

        this.diffuseColor = color;
        this.material = material;
    }

    /**
     * Methods
     */
    /*
       Method that intersects a ray with the mesh. It
       gives the closest intersection (minimum non-negative
       value of parameter s in P = O + s * D ray equation)
       if an intersection exists, or 'null' if no intersection
       exists (don't want to use exceptions for control
       flow when rendering).
     */
    public Vector3D intersect(Ray ray) {
        Vector3D closestIntersectionPoint = null;
        double minDistanceSoFar = -1;
        Triangle intersectedTriangle = null;

        for(Triangle triangle : this.triangleFaces) {
            Vector3D intersectionPoint = triangle.intersect(ray);
            if(intersectionPoint != null) {
                if(closestIntersectionPoint == null || ray.distance(intersectionPoint) < minDistanceSoFar) {
                    closestIntersectionPoint = intersectionPoint;
                    minDistanceSoFar = ray.distance(intersectionPoint);
                    intersectedTriangle = triangle;
                }
            }
        }

        /// TODO - finish intersection
        return null;
    }
    /*
       Method that returns the unit normal at a given point on
       the surface of the mesh.

       The unit normal must point outwards by convention, which
       for a mesh is the direction in which the unit normal is
       defined when passed to the constructor of the corresponding
       triangle.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Vector3D getUnitNormalAt(Vector3D point) {
        return null;
    }
    /*
       Method that returns the diffuse color of the mesh at
       a given point on its surface.

       This mesh has the same diffuse color everywhere.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public RTColor getColorAt(Vector3D point) {
        return this.diffuseColor;
    }
    /*
       Method that returns the material (shading
       coefficients) at a given point on the surface of
       the mesh.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Material getMaterialAt(Vector3D point) {
        return this.material;
    }

    /**
     * Static Utility Methods
     */
    /*
       Method that parses a triangle mesh from a Map<String,String>
       mapping attribute names to their values.

       Each triangle mesh is defined by a "path-to-obj-file", "color",
       and a "material" attribute.

       If the material attribute is missing from the XML
       description of the RTShape, Material.defaultNonReflectiveMaterial
       is set.
     */
    public static TriangleMesh parseShape(Map<String, String> attributes) throws IncorrectSceneDescriptionXMLStructureException, IOException {
        String path = null;
        RTColor color = null;
        Material material = null;

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            switch (attributeName) {
                case "path-to-obj-file" -> path = attributeValue;
                case "color" -> color = SceneDescriptionParser.parseColor(attributeValue);
                case "material" ->
                {
                    /// first try to parse the material from name, then try to parse from description
                    try {
                        material = Material.parseMaterialFromName(attributeValue);
                    }
                    catch (IncorrectSceneDescriptionXMLStructureException e) {
                        material = Material.parseMaterial(attributeValue);
                    }
                }
                default -> throw new IncorrectSceneDescriptionXMLStructureException();
            }
        }

        if(path == null || color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }

        /// if missing material in XML, set default
        if(material == null) {
            material = Material.defaultNonReflectiveMaterial;
        }

        return new TriangleMesh(path, color, material);
    }

    /**
     * Getters
     */
    public String getShapeID() {
        return TriangleMesh.shapeID;
    }
}
