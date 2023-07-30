package shapes;

import de.javagl.obj.ObjData;
import shading.Material;
import tracing.Intersection;
import tracing.Ray;
import utility.*;

import java.io.FileInputStream;
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
 * The vertex coordinates of every mesh are given in object coordinates.
 * To position the triangle mesh in the scene, we define scaling,
 * rotation, and translation transformations in the XML description of
 * the mesh (to get world coordinates).
 *
 * Every triangle mesh has a modelling transformation (Matrix4D) defined
 * in its XML node, as a "model-transform" attribute. This XML sub-node
 * can contain various "scale" (give Vector3D for coefficients),
 * "rotateX" (give a real number for angle in degrees),
 * "rotateY" (give a real number for angle in degrees),
 * "rotateZ" (give a real number for angle in degrees), and
 * "translate" (give a Vector3D by which to translate) attributes. The
 * XML parser combines all these transformations into a single Matrix4D,
 * which is passed to the TriangleMesh constructor as the final modelling
 * transformation. Basic transformations (translate, rotate, ...) are
 * performed in the order in which they are listed in the XML sub-node
 * (of the mesh's XML node) named "model-transform".
 *
 * Every polygonal mesh's class variable shapeID must end in "mesh".
 *
 * TODO - implement a bounding box for the TriangleMesh, and first check
 *        if ray hits the bounding box to optimise tracing
 */

public class TriangleMesh implements RTShape {
    public static final String shapeID = "triangle-mesh";

    /// the vertices of the mesh, in object coordinates
    private final ArrayList<Vector3D> vertices;
    /// the normals to the mesh at each vertex, found by averaging adjacent face (triangle) normals, triangleFaces
    /// arraylist references objects from this vertexNormals arraylist (for triangle vertex normals)
    private final ArrayList<Vector3D> vertexNormals;
    /// the triangles that the vertices form
    private final ArrayList<Triangle> triangleFaces;

    private final RTColor diffuseColor;
    private final Material material;

    /**
     * Constructors
     */
    /*
       Constructor from mesh description as a Wavefront OBJ file, modelling transform
       to be performed to obtain triangle mesh in world coordinates, its color, and
       its material.

       The transformations ("scale", "rotateX", "rotateY", "rotateZ", "translate")
       are performed in the order in which they are listed in the XML description
       of the mesh, in the "model-transform" attribute.

       An open source Wavefront OBJ parser (https://github.com/javagl/Obj/tree/master)
       is used to extract mesh info.

       This constructor creates triangles which do not necessarily have to be flat
       shaded - if vertex normals indices use different normals for different vertices
       in the same triangle, normals will be linearly interpolated.
     */
    public TriangleMesh(String pathToObjFile, Matrix4D modelTransformation, RTColor color, Material material) throws IOException {
        InputStream objInputStream = new FileInputStream(pathToObjFile);
        Obj obj = ObjReader.read(objInputStream);

        float[] vertexCoordinates = ObjData.getVerticesArray(obj);
        float[] vertexNormalsCoordinates = ObjData.getNormalsArray(obj);
        int[] faceVertexIndices = ObjData.getFaceVertexIndicesArray(obj);
        int[] faceVertexNormalsIndices = ObjData.getFaceNormalIndicesArray(obj);

        /// extract list of vertices by grouping coordinates three by three
        this.vertices = new ArrayList<>();
        for(int i = 0; i < vertexCoordinates.length / 3; i++) {
            /// extract vertex in object coordinates from .obj file
            Vector3D v = new Vector3D(vertexCoordinates[3*i], vertexCoordinates[3*i+1], vertexCoordinates[3*i+2]);
            /// apply modelling transformation to transform the vertex to world coordinates (in scene), and add it to list
            this.vertices.add(modelTransformation.multiplyFromRight(v));
        }

        /// extract list of vertex normals by grouping coordinates three by three
        /// TODO - transform normals using (M^inverse)^transpose (for non-orthogonal matrices)
        this.vertexNormals = new ArrayList<>();
        for(int i = 0; i < vertexNormalsCoordinates.length / 3; i++) {
            this.vertexNormals.add(new Vector3D(vertexNormalsCoordinates[3*i], vertexNormalsCoordinates[3*i+1], vertexNormalsCoordinates[3*i+2]));
        }

        /// extract list of triangles
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
    /*
       Constructor from all attributes (and modelling transform to be performed to obtain triangle
       mesh in world coordinates) except triangleFaces, which is here extracted from the given
       indices of vertices and normals.

       The constructor takes the vertex indices as an argument, but not vertex normals indices
       because this constructor creates flat shaded triangles, i.e. vertex normals are all set
       to be equal to the unit normal to the surface of the triangle.
     */
    public TriangleMesh(ArrayList<Vector3D> vertices, ArrayList<Vector3D> vertexNormals, int[] faceVertexIndices, Matrix4D modelTransformation, RTColor color, Material material) {
        this.vertices = new ArrayList<>();
        for(Vector3D vertex : vertices) {
            /// apply modelling transformation to transform the vertex to world coordinates (in scene), and add it to list
            this.vertices.add(modelTransformation.multiplyFromRight(vertex));
        }

        /// TODO - transform normals using (M^inverse)^transpose (for non-orthogonal matrices)
        this.vertexNormals = vertexNormals;

        /// extract list of triangles
        this.triangleFaces = new ArrayList<>();
        for(int i = 0; i < faceVertexIndices.length / 3; i++) {
            this.triangleFaces.add(
                    new Triangle(
                            this.vertices.get(faceVertexIndices[3*i]), this.vertices.get(faceVertexIndices[3*i+1]), this.vertices.get(faceVertexIndices[3*i+2]),
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

       Each triangle mesh is defined by a "path-to-obj-file",
       "model-transform", "color", and a "material" attribute.
       "path-to-obj-file", "color", and "material" are leaf
       attributes (only contain a String value).

       The "model-transform" attribute is an XML sub-node that
       contains a list of transformations in arbitrary order
       (when creating XML description file, you should do scaling, then rotation,
       then translation):

            "scale" - scale by 3 given coefficients for 3 axis (as Vector3D)
            "rotateX" - rotate about x-axis by given angle in degrees
            "rotateY" - rotate about y-axis by given angle in degrees
            "rotateZ" - rotate about z-axis by given angle in degrees
            "translate" - translate by given Vector3D

       In the SceneDescriptionParser, each of these transformations is
       parsed into a Matrix4D, they are multiplied in correct order
       (so that they are performed in the order in which they are written
       in the XML), and the final Matrix4D is passed as the value of the
       "model-transform" attribute. This final matrix is passed as an
       argument to the parseShape method below. Only polygonal meshes
       are allowed to include the 'Matrix4D modelTransformation' argument in
       the signature of their parseShape method, because they are the
       only RTShape's that are allowed to have a "model-transform" attribute.
       If the "model-transform" attribute is missing from the XML description
       of the triangle mesh, the modelling transformation is set to identity
       when passed to the method below.

       If the material attribute is missing from the XML
       description of the RTShape, Material.defaultNonReflectiveMaterial
       is set.
     */
    public static TriangleMesh parseShape(Map<String, String> leafAttributes, Matrix4D modelTransformation) throws IncorrectSceneDescriptionXMLStructureException, IOException {
        String path = null;
        /// model transformation has already been parsed by SceneDescriptionParser
        RTColor color = null;
        Material material = null;

        for (Map.Entry<String, String> entry : leafAttributes.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            switch (attributeName) {
                case "path-to-obj-file" -> path = attributeValue;

                /// model transformation has already been parsed by SceneDescriptionParser

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
                default -> throw new IncorrectSceneDescriptionXMLStructureException("Undefined attribute in TriangleMesh description.");
            }
        }

        if(path == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'path-to-obj-file' attribute in TriangleMesh description.");
        }
        else if(color == null) {
            throw new IncorrectSceneDescriptionXMLStructureException("Missing 'color' attribute in TriangleMesh description.");
        }

        /// if missing material in XML, set default
        if(material == null) {
            material = Material.defaultNonReflectiveMaterial;
        }

        return new TriangleMesh(path, modelTransformation, color, material);
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
