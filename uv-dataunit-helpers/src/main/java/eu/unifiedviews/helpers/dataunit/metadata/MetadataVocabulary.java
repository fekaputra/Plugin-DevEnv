package eu.unifiedviews.helpers.dataunit.metadata;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Common vocabulary definition.
 *
 * @author Škoda Petr
 */
public class MetadataVocabulary {

    public static final String STR_UV_SYMBOLIC_NAME = MetadataDataUnit.PREDICATE_SYMBOLIC_NAME;

    public static final URI UV_SYMBOLIC_NAME;

    static {
        final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

        UV_SYMBOLIC_NAME = valueFactory.createURI(STR_UV_SYMBOLIC_NAME);
    }

}
