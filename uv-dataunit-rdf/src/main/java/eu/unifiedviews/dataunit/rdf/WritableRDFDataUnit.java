package eu.unifiedviews.dataunit.rdf;

import eu.unifiedviews.dataunit.DataUnitException;

import org.openrdf.model.URI;

public interface WritableRDFDataUnit extends RDFDataUnit {

    /**
     * Get name of the context which is considered to be owned by data unit and
     * is the only one into which write operations are performed.
     *
     * @return
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    URI getBaseDataGraphURI() throws DataUnitException;

    /**
     * Adds an existing graph with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * The graph should be URI-rooted under the getBaseDataGraphURI() but it is not enforced (and that is useful for advance usage).
     * If you don't know if use this function or addNewGraph, use the latter one.
     * 
     * @param symbolicName symbolic name under which the graph will be stored (must be unique in scope of this data unit)
     * @param existingDataGraphURI real graph name
     * @throws DataUnitException
     */
    void addExistingDataGraph(String symbolicName, URI existingDataGraphURI) throws DataUnitException;

    /**
     * Generates unique graph name under the getBaseDataGraphURI().
     * Returns the newly generated full graph URI to work with.
     * It does create the graph in triplestore, and it does add the file into the dataunit under provided symbolicName.
     * 
     * @return URI of newly created data graph
     * @throws DataUnitException
     */
    URI addNewDataGraph(String symbolicName) throws DataUnitException;
    
    /**
     * Add all data from given DataUnit into this DataUnit. The method must not
     * modify the current parameter (unit).
     *
     * @param dataunit {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} to add
     * from
     * @throws eu.unifiedviews.dataunit.DataUnitException
     * @throws IllegalArgumentException if some property of an element of the
     * specified dataunit prevents it from being added to this dataunit
     */
    @Deprecated
    void addAll(RDFDataUnit dataunit) throws DataUnitException;

}
