package eu.unifiedviews.helpers.dpu.serialization.xml;

/**
 *
 * @author Škoda Petr
 */
public class SerializationXmlFactory {
    
    private SerializationXmlFactory() {
    }

    /**
     * 
     * @return
     */
    public static SerializationXml serializationXml() {
        return new SerializationXmlImpl();
    }

}
