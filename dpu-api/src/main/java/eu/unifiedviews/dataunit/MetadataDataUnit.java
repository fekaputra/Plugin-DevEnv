package eu.unifiedviews.dataunit;

import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;

/**
 * Basic metadata data unit interface.
 *
 */
public interface MetadataDataUnit extends DataUnit {

    static final String PREDICATE_SYMBOLIC_NAME = "http://linked.opendata.cz/ontology/odcs/dataunit/files/symbolicName";

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
}
