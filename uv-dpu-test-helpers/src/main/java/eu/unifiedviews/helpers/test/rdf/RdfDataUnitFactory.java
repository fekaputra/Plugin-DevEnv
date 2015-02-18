package eu.unifiedviews.helpers.test.rdf;

import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import org.openrdf.repository.RepositoryException;

/**
 * Factory for test RDF data units.
 *
 * @author Škoda Petr
 */
public class RdfDataUnitFactory {

    private RdfDataUnitFactory() {
        
    }

    public static WritableRDFDataUnit createInMemory() throws RepositoryException {
        return new InMemoryRdfDataUnit();
    }

}
