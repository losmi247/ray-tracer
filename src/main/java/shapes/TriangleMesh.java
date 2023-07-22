package shapes;

import de.javagl.obj.ObjData;
import shading.Material;
import tracing.Intersection;
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
 * Class for a mesh of triangles, defined by the vertices it contains,
 * normals to surface at each vertex, color and material.
 *
 * To create meshes, use Blender, then export it as an .obj file, then
 * place it into project folder, and insert the path to it into the
 * XML node of the triangle mesh in the scene description file.
 *
 * TODO - scaling, rotation, translation for each triangle mesh to be
 *          positioned in the scene, test by rendering
 *
 * TODO - implement a bounding box for the TriangleMesh, and first check
 *        if ray hits the bounding box to optimise tracing
 */

public class TriangleMesh implements RTShape {
    public static final String shapeID = "triangle-mesh";

    /// the vertices of the mesh, in object coordinates
    private final ArrayList<Vector3D> vertices;
    /// the normals to the mesh at each vertex, found by averaging adjacent face (triangle) normals
    private final ArrayList<Vector3D> vertexNormals;
    /// the triangles that the vertices form
    private final ArrayList<Triangle> triangleFaces;

    private final RTColor diffuseColor;
    private final Material material;

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
        for(int i = 0; i < vertexCoordinates.length / 3; i++) {
            this.vertices.add(new Vector3D(vertexCoordinates[3*i], vertexCoordinates[3*i+1], vertexCoordinates[3*i+2]));
        }

        /// initialise list of vertex normals by grouping coordinates three by three
        this.vertexNormals = new ArrayList<>();
        for(int i = 0; i < vertexNormalsCoordinates.length / 3; i++) {
            this.vertexNormals.add(new Vector3D(vertexNormalsCoordinates[3*i], vertexNormalsCoordinates[3*i+1], vertexNormalsCoordinates[3*i+2]));
        }

        /// initialise list of triangles
        this.triangleFaces = new ArrayList<>();
        for(int i = 0; i < faceVertexIndices.length / 3; i++) {
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

       The point of intersection is bundled together with the
       intersected shape, into an Intersection object.

       Unlike most other primitives, a triangle mesh here returns
       the intersected triangle in the returned Intersection object,
       not the mesh itself (we need to know which exact triangle is
       intersected later, so that was the motivation for making
       the 'intersect' method return an Intersection object rather
       than a simple Vector3D in the first place).
     */
    public Intersection intersect(Ray ray) {
        Intersection closestIntersection = null;
        double minDistanceSoFar = -1;

        for(Triangle triangle : this.triangleFaces) {
            Intersection intersection = triangle.intersect(ray);
            if(intersection != null) {
                if(closestIntersection == null || ray.distance(intersection.getIntersectionPoint()) < minDistanceSoFar) {
                    closestIntersection = intersection;
                    minDistanceSoFar = ray.distance(intersection.getIntersectionPoint());
                }
            }
        }

        return closestIntersection;
    }
    /*
       Method that returns the unit normal at a given point on
       the surface of the mesh, given as an Intersection object,
       rather than just a Vector3D, so that we have information
       which exact triangle is intersected (computed in an earlier
       call to 'intersect' which returns an Intersection for the
       same reason), so that we can pass the method invocation
       to the appropriate triangle.

       Hence, this method must be used only with an Intersection
       object that contains a Triangle as an intersected shape.

       The unit normal must point outwards by convention, which
       for a mesh is the direction in which the unit normal is
       defined when passed to the constructor of the corresponding
       triangle.

       If the precondition (point must be on the surface of
       the shape) is violated, behaviour is undefined.
     */
    public Vector3D getUnitNormalAt(Intersection intersection) {
        /// find the unit normal to intersected triangle
        return intersection.getIntersectedShape().getUnitNormalAt(intersection);
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
    public ArrayList<Vector3D> getVertices() {
        return this.vertices;
    }
    public ArrayList<Vector3D> getNormals() {
        return this.vertexNormals;
    }
    public ArrayList<Triangle> getTriangles() {
        return this.triangleFaces;
    }
}
