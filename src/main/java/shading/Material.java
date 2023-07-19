package shading;

import utility.IncorrectSceneDescriptionXMLStructureException;
import utility.SceneDescriptionParser;
import utility.Vector3D;

/**
 * A class to encapsulate material properties
 * of a certain point on a RTShape that is to
 * be rendered. Each RTShape can have a
 * different Material.
 *
 * The material properties consists of the
 * ambient, diffuse, specular coefficients,
 * Phong's roughness coefficient (for specular
 * component of the shading model), and the
 * reflection coefficient (how much of the
 * light is reflected).
 *
 * Materials are immutable.
 *
 * Ambient, diffuse, and specular coefficients
 * can be different for different color channels,
 * so they are represented as Vector3D objects,
 * i.e. each has one decimal value for each color
 * channel.
 */

public class Material {
    private final Vector3D ambientCoefficient;
    private final Vector3D diffuseCoefficient;
    private final Vector3D specularCoefficient;
    private final double phongRoughnessCoefficient;

    private final double reflectionCoefficient;

    /**
     * Predefined materials
     * (some from http://devernay.free.fr/cours/opengl/materials.html)
     */
    public static final Material defaultReflectiveMaterial = new Material(new Vector3D(0.4), new Vector3D(0.9), new Vector3D(0.6), 0.3125, 0.4);
    public static final Material defaultNonReflectiveMaterial = new Material(new Vector3D(0.4), new Vector3D(0.9), new Vector3D(0.6), 0.3125, 0);

    public static final Material metal        = new Material(new Vector3D(0.35), new Vector3D(0.3), new Vector3D(0.8), 0.05, 0.1);
    public static final Material glass        = new Material(new Vector3D(0), new Vector3D(0), new Vector3D(0.7), 0.01, 0.2);
    public static final Material mirror       = new Material(new Vector3D(0), new Vector3D(0), new Vector3D(0), 0.01, 0.9);
    public static final Material plastic      = new Material(new Vector3D(0.1), new Vector3D(0.25), new Vector3D(0.25), 0.1, 0.05);

    public static final Material obsidian     = new Material(new Vector3D(0.05375, 0.05, 0.06625), new Vector3D(0.18275, 0.17, 0.22525), new Vector3D(0.332741, 0.328634, 0.346435), 0.3, 0.01);
    public static final Material ruby         = new Material(new Vector3D(0.1745, 0.01175, 0.01175), new Vector3D(0.61424, 0.04136, 0.04136), new Vector3D(0.727811, 0.626959, 0.626959), 0.6, 0.01);

    public static final Material brass        = new Material(new Vector3D(0.329412, 0.223529, 0.027451), new Vector3D(0.780392, 0.568627, 0.113725), new Vector3D(0.992157, 0.941176, 0.807843), 0.21794872, 0.05);
    public static final Material gold         = new Material(new Vector3D(0.24725, 0.1995, 0.0745), new Vector3D(0.75164, 0.60648, 0.22648), new Vector3D(0.628281, 0.555802, 0.366065), 0.4, 0.05);
    public static final Material silver       = new Material(new Vector3D(0.19225, 0.19225, 0.19225), new Vector3D(0.50754, 0.50754, 0.50754), new Vector3D(0.508273, 0.508273, 0.508273), 0.4, 0.05);
    public static final Material copper       = new Material(new Vector3D(0.19125, 0.0735, 0.0225), new Vector3D(0.7038, 0.27048, 0.0828), new Vector3D(0.256777, 0.137622, 0.086014), 0.1, 0.05);

    public static final Material blackPlastic = new Material(new Vector3D(0.0, 0.0, 0.0), new Vector3D(0.01, 0.01, 0.01), new Vector3D(0.5, 0.5, 0.5), 0.25, 0.05);
    public static final Material redPlastic   = new Material(new Vector3D(0.0, 0.0, 0.0), new Vector3D(0.5, 0.0, 0.0), new Vector3D(0.7, 0.6, 0.6), 0.25, 0.05);
    public static final Material cyanPlastic  = new Material(new Vector3D(0.0, 0.1, 0.06), new Vector3D(0.0, 0.50980392, 0.50980392), new Vector3D(0.50196078, 0.50196078, 0.50196078), 0.25, 0.05);

    public static final Material greenRubber  = new Material(new Vector3D(0.0, 0.05, 0.0), new Vector3D(0.4, 0.5, 0.4), new Vector3D(0.04, 0.7, 0.04), 0.078125, 0.01);
    public static final Material redRubber    = new Material(new Vector3D(0.05, 0.0, 0.0), new Vector3D(0.5, 0.4, 0.4), new Vector3D(0.7, 0.04, 0.04), 0.078125, 0.01);
    public static final Material yellowRubber = new Material(new Vector3D(0.05, 0.05, 0.0), new Vector3D(0.5, 0.5, 0.4), new Vector3D(0.7, 0.7, 0.04), 0.078125, 0.01);

    public static final Material concrete = new Material(new Vector3D(0.07, 0.07, 0.07), new Vector3D(0.95, 0.95, 0.95), new Vector3D(0.01,0.01,0.01), 0.0000001, 0.001);

    /**
     * Constructors
     */
    public Material(Vector3D ambient, Vector3D diffuse, Vector3D specular, double roughness, double reflection) {
        this.ambientCoefficient = ambient;
        this.diffuseCoefficient = diffuse;
        this.specularCoefficient = specular;

        this.phongRoughnessCoefficient = roughness;
        this.reflectionCoefficient = reflection;
    }

    /**
     * Static Utility Methods
     */
    /*
       Method to parse a Material object from a String
       of form (ambient, diffuse, specular, roughness, reflection),
       where the first three coefficients are represented as (x,y,z)
       (one coefficient for each color channel) and the last two are
       simply real numbers.
     */
    public static Material parseMaterial(String s) throws IncorrectSceneDescriptionXMLStructureException {
        String[] components = s.substring(1, s.length()-1).split(",");
        if(components.length != 5) {
            throw new IncorrectSceneDescriptionXMLStructureException();
        }
        return new Material(SceneDescriptionParser.parseVector3D(components[0]),
                SceneDescriptionParser.parseVector3D(components[1]),
                SceneDescriptionParser.parseVector3D(components[2]),
                Double.parseDouble(components[3]),
                Double.parseDouble(components[4]));
    }
    /*
       Method to parse a Material object from a String that only
       states the name of one of the predefined Materials in this
       class.
     */
    public static Material parseMaterialFromName(String s) throws IncorrectSceneDescriptionXMLStructureException {
        switch (s) {
            case "default-reflective" -> {
                return Material.defaultReflectiveMaterial;
            }
            case "default-nonreflective" -> {
                return Material.defaultNonReflectiveMaterial;
            }

            case "metal" -> {
                return Material.metal;
            }
            case "glass" -> {
                return Material.glass;
            }
            case "mirror" -> {
                return Material.mirror;
            }
            case "plastic" -> {
                return Material.plastic;
            }

            case "obsidian" -> {
                return Material.obsidian;
            }
            case "ruby" -> {
                return Material.ruby;
            }
            case "brass" -> {
                return Material.brass;
            }

            case "gold" -> {
                return Material.gold;
            }
            case "silver" -> {
                return Material.silver;
            }
            case "copper" -> {
                return Material.copper;
            }

            case "black-plastic" -> {
                return Material.blackPlastic;
            }
            case "red-plastic" -> {
                return Material.redPlastic;
            }
            case "cyan-plastic" -> {
                return Material.cyanPlastic;
            }

            case "green-rubber" -> {
                return Material.greenRubber;
            }
            case "red-rubber" -> {
                return Material.redRubber;
            }
            case "yellow-rubber" -> {
                return Material.yellowRubber;
            }

            case "concrete" -> {
                return Material.concrete;
            }

            default -> throw new IncorrectSceneDescriptionXMLStructureException();
        }
    }

    /**
     * Getters
     */
    public Vector3D getAmbientCoefficient() {
        return this.ambientCoefficient;
    }
    public Vector3D getDiffuseCoefficient() {
        return this.diffuseCoefficient;
    }
    public Vector3D getSpecularCoefficient() {
        return this.specularCoefficient;
    }
    /*
       Getter for the Phong roughness coefficient.
       Within the class, it is kept in 0 to 1 range,
       so it needs to be multiplied by 128 before shading.
     */
    public double getPhongRoughnessCoefficient() {
        return this.phongRoughnessCoefficient * 128;
    }
    public double getReflectionCoefficient() {
        return this.reflectionCoefficient;
    }
}
