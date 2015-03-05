package eu.unifiedviews.helpers.dataunit.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import eu.unifiedviews.helpers.dataunit.virtualgraph.VirtualGraphHelper;

/**
 * Vocabulary definition for RDF.
 *
 * @author Škoda Petr
 */
public class RdfVocabulary {

    public static final String STR_UV_VIRTUAL_URI = VirtualGraphHelper.PREDICATE_VIRTUAL_GRAPH;

    public static final URI UV_VIRTUAL_URI;

    static {
        final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

        UV_VIRTUAL_URI = valueFactory.createURI(STR_UV_VIRTUAL_URI);
    }

}
