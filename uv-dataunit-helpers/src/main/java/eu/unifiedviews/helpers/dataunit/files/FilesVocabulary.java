package eu.unifiedviews.helpers.dataunit.files;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper;

/**
 * Vocabulary definition for files.
 *
 * @author Škoda Petr
 */
public class FilesVocabulary {

    public static final String STR_UV_VIRTUAL_PATH = VirtualPathHelper.PREDICATE_VIRTUAL_PATH;

    public static final URI UV_VIRTUAL_PATH;

    static {
        final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

        UV_VIRTUAL_PATH = valueFactory.createURI(STR_UV_VIRTUAL_PATH);
    }

}
