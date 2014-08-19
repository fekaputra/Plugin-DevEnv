package eu.unifiedviews.dataunit.rdf;

import org.openrdf.model.URI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Interface provides methods for working with RDF data and metadata.
 */
public interface RDFDataUnit extends MetadataDataUnit {

    /**
     * Predicate used to store URI or data graph which holds the data itself. Format of RDF metadata (see {@link MetadataDataUnit}) is then extended to at least two triples:
     * {@code
     * <subject> p:symbolicName "name literal"
     * <subject> p:datagraphURI <http://some.uri/and/it/is/unique
     * }
     */
    static final String PREDICATE_DATAGRAPH_URI = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/RDFDataUnit/dataGraphURI";

    /**
     * Extend entry to hold also dataGraphURI, the location of RDF data contained inside one metadata entry.
     */
    interface Entry extends MetadataDataUnit.Entry {
        /**
         * Get the URI of graph inside storage where data are located.
         *
         * @return URI of graph inside storage where data are located.
         * @throws DataUnitException when something fails
         */
        URI getDataGraphURI() throws DataUnitException;
    }

    interface Iteration extends MetadataDataUnit.Iteration {
        @Override
        public RDFDataUnit.Entry next() throws DataUnitException;
    }

    @Override
    RDFDataUnit.Iteration getIteration() throws DataUnitException;
}
