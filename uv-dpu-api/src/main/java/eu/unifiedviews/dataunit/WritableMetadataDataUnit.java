package eu.unifiedviews.dataunit;

import org.openrdf.model.URI;

/**
 * Basic metadata writable data unit interface.
 *
 */
public interface WritableMetadataDataUnit extends MetadataDataUnit {

    /**
     * Adds an symbolic name with supplied symbolic name to the data unit. The
     * symbolic name must be unique in scope of this data unit. 
     *
     * @param symbolicName symbolic name under which the data will be stored
     * (must be unique in scope of this data unit)
     * @throws DataUnitException
     */
    void addEntry(String symbolicName) throws DataUnitException;

    /**
     * Return graph name where all new metadata created by DPU should be written.
     * 
     * @return URI of the graph
     * @throws DataUnitException
     */
    URI getMetadataWriteGraphname() throws DataUnitException;
}
