package eu.unifiedviews.helpers.dataunit.copyhelper;

import eu.unifiedviews.dataunit.DataUnitException;


public interface CopyHelper extends AutoCloseable {
    void copyMetadataAndContents(String symbolicName) throws DataUnitException;
    void copyMetadata(String symbolicName) throws DataUnitException;
    @Override
    public void close() throws DataUnitException;
}
