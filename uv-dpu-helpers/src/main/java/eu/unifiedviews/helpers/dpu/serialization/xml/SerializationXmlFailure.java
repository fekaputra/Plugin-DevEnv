package eu.unifiedviews.helpers.dpu.serialization.xml;

/**
 *
 * @author Škoda Petr
 */
public class SerializationXmlFailure extends Exception {

    public SerializationXmlFailure(String message) {
        super(message);
    }

    public SerializationXmlFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationXmlFailure(Throwable cause) {
        super(cause);
    }

}
