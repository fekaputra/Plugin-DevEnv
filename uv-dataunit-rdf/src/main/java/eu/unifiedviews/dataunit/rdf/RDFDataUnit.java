package eu.unifiedviews.dataunit.rdf;

import java.util.Set;

import org.openrdf.model.URI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Interface provides methods for working with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public interface RDFDataUnit extends MetadataDataUnit {

    static final String PREDICATE_DATAGRAPH_URI = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/RDFDataUnit/dataGraphURI";

    interface Entry extends MetadataDataUnit.Entry {
        URI getDataGraphURI() throws DataUnitException;
    }
    
    interface Iteration extends MetadataDataUnit.Iteration {
        @Override
        public RDFDataUnit.Entry next() throws DataUnitException;
    }
    
    RDFDataUnit.Iteration getIteration() throws DataUnitException;
}
