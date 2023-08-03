package rendering.utility;

public class IncorrectSceneDescriptionXMLStructureException extends Exception {
    /// default constructor
    public IncorrectSceneDescriptionXMLStructureException() {

    }
    /// constructor for giving an error message
    public IncorrectSceneDescriptionXMLStructureException(String msg) {
        super(msg);
    }
}
