package eu.unifiedviews.dataunit.rdf;

import org.openrdf.model.URI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Interface provides methods for working with RDF data and metadata.
 */
public interface RDFDataUnit extends MetadataDataUnit {

    /**
     * Value: {@value #PREDICATE_DATAGRAPH_URI}, predicate used to store URI or data graph which holds the data itself.
     * Format of RDF metadata (see {@link MetadataDataUnit}) is then extended to at least two triples:
     * <p><blockquote><pre>
     * &lt;subject&gt; &lt;{@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME}&gt; "name literal"
     * &lt;subject&gt &lt;{@value #PREDICATE_DATAGRAPH_URI}&gt; &lt;http://some.uri/and/it/is/unique&gt;
     * </pre></blockquote></p>
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
