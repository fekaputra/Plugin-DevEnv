package eu.unifiedviews.dataunit.rdf;

import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Interface provides methods for working with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public interface RDFDataUnit extends MetadataDataUnit {

    /**
     * Returns URI representation of graph where RDF data are stored.
     *
     * @return URI representation of graph where RDF data are stored.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public Set<URI> getDataGraphnames() throws DataUnitException;

}
