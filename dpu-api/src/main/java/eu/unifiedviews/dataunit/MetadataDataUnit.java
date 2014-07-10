package eu.unifiedviews.dataunit;

import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;

/**
 * Basic data unit interface. The data unit should be passed in context between
 * modules and should carry the main information. Each DataUnit has URI, this
 * can can't be changed by DPU directly. It's assigned once when DataUnit is
 * created. The URI can be obtained using {@link #getDataUnitName()}
 *
 * @author Petyr
 */
public interface MetadataDataUnit extends DataUnit {

    public static final String PREDICATE_SYMBOLIC_NAME = "http://linked.opendata.cz/ontology/odcs/dataunit/files/symbolicName";

    /**
     * Returns connection to repository.
     *
     * @return Connection to repository.
     * @throws DataUnitException If something went wrong during the creation
     * of the Connection.
     */
    public RepositoryConnection getConnection() throws DataUnitException;

    /**
     * Returns URI representation of graphs where Metadata data are stored.
     *
     * @return URI representation of graph where meta data are stored.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    public Set<URI> getMetadataGraphnames() throws DataUnitException;
}
