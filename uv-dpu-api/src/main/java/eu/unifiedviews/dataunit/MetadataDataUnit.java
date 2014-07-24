package eu.unifiedviews.dataunit;

import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;

/**
 * Basic metadata data unit interface.
 *
 */
public interface MetadataDataUnit extends DataUnit {

    static final String PREDICATE_SYMBOLIC_NAME = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/symbolicName";

    /**
     * Returns connection to repository.
     *
     * @return Connection to repository.
     * @throws DataUnitException If something went wrong during the creation of
     * the Connection.
     */
    RepositoryConnection getConnection() throws DataUnitException;

    /**
     * Returns URI representation of graphs where Metadata data are stored.
     *
     * @return URI representation of graph where meta data are stored.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    Set<URI> getMetadataGraphnames() throws DataUnitException;

    /**
     * Interface describing one piece of data which can be decorated by metadata.
     * 
     */
    interface Entry {

        /**
         *
         * @return Symbolic name under which the data is stored inside this data
         * unit.
         * @throws eu.unifiedviews.dataunit.DataUnitException
         */
        String getSymbolicName() throws DataUnitException;
    }

    /**
     * To iterate over data pieces.
     * 
     */
    interface Iteration extends AutoCloseable {

        boolean hasNext() throws DataUnitException;

        MetadataDataUnit.Entry next() throws DataUnitException;

        @Override
        void close() throws DataUnitException;
    }

    /**
     * List the metadata.
     *
     * @return iteration over metadata entries
     * @throws DataUnitException
     */
    MetadataDataUnit.Iteration getIteration() throws DataUnitException;

}
