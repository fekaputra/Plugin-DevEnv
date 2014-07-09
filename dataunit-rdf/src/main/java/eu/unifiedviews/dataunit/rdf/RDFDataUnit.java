package eu.unifiedviews.dataunit.rdf;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Interface provides methods for working with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public interface RDFDataUnit extends DataUnit {

    interface GraphEntry {

        /**
         * Returns connection to repository.
         *
         * @return Connection to repository.
         * @throws RepositoryException If something went wrong during the
         * creation of the Connection.
         */
        public RepositoryConnection getConnection() throws RepositoryException;

        /**
         * Returns URI representation of graph where RDF data are stored.
         *
         * @return URI representation of graph where RDF data are stored.
         */
        public Set<URI> getGraphnames();
    }

    interface GraphIteration extends AutoCloseable {

        public boolean hasNext() throws DataUnitException;

        public RDFDataUnit.GraphEntry next() throws DataUnitException;

        @Override
        public void close() throws DataUnitException;
    }

    /**
     *
     * @return @throws DataUnitException
     */
    GraphEntry getFederationEntry() throws DataUnitException;

    /**
     * List the graphs.
     *
     * @return
     * @throws DataUnitException
     */
    RDFDataUnit.GraphIteration getGraphIteration() throws DataUnitException;

}
