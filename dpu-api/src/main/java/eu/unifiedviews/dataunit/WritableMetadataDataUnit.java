package eu.unifiedviews.dataunit;

/**
 * Basic metadata writable data unit interface.
 *
 */
public interface WritableMetadataDataUnit extends MetadataDataUnit {

    /**
     * Adds an symbolic name with supplied symbolic name to the data unit. The
     * symbolic name must be unique in scope of this data unit. The file should
     * be located under the getBasePath(). It is not allowed to create new files
     * in different locations.
     *
     * @param symbolicName symbolic name under which the file will be stored
     * (must be unique in scope of this data unit)
     * @throws DataUnitException
     */
    void addEntry(String symbolicName) throws DataUnitException;

}
