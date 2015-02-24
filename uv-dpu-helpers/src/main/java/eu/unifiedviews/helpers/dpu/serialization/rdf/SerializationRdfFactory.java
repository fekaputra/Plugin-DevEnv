package eu.unifiedviews.helpers.dpu.serialization.rdf;

/**
 * Factory to create instances of {@link SerializationRdf}.
 *
 * @author Škoda Petr
 */
public class SerializationRdfFactory {

    private SerializationRdfFactory() {
    }

    /**
     *
     * @return 
     */
    public static SerializationRdf rdfSimple() {
        return new SerializationRdfSimple();
    }

}
