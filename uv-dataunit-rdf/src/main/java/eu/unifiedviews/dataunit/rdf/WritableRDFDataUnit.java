package eu.unifiedviews.dataunit.rdf;

import org.openrdf.model.URI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;

/**
 * Writable version of {@link RDFDataUnit}, supports adding of new data graphs or existing data graphs which were materialized inside storage externally.
 */
public interface WritableRDFDataUnit extends RDFDataUnit, WritableMetadataDataUnit {

    /**
     * Get URI prefix which is unique for this data unit. For generating graph names use
     * {@code
     * getBaseDataGraphURI() + "/" + "myName"
     * }
     * to avoid any graph name conflicts (as the prefix is unique already).
     *
     * @return URI-prefix to be used when generating graph names manually (and then adding them by {@link WritableRDFDataUnit#addExistingDataGraph(String, URI)}
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    URI getBaseDataGraphURI() throws DataUnitException;

    /**
     * Adds an existing graph with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * The graph should be URI-rooted under the {@link WritableRDFDataUnit#getBaseDataGraphURI()} but it is not enforced
     * (and that is useful for advanced usage - when graphs are loaded externally).
     * If you don't know if to use this function or {@link WritableRDFDataUnit#addNewDataGraph(String)}, use the latter one.
     *
     * @param symbolicName symbolic name under which the graph will be stored (must be unique in scope of this data unit)
     * @param existingDataGraphURI real graph name
     * @throws DataUnitException when symbolicName is not unique or something goes wrong
     */
    void addExistingDataGraph(String symbolicName, URI existingDataGraphURI) throws DataUnitException;

    /**
     * Generates unique graph name prefixed by the {@link WritableRDFDataUnit#getBaseDataGraphURI()}.
     * Returns the newly generated full graph URI to work with.
     * It does create the graph in storage, and it does add the graph metadata entry into the dataunit under provided symbolicName.
     *
     * @return URI of newly created data graph
     * @throws DataUnitException when symbolicName is not unique or something goes wrong
     */
    URI addNewDataGraph(String symbolicName) throws DataUnitException;
}
