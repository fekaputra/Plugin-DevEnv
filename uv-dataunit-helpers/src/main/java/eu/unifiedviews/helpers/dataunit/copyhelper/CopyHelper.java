package eu.unifiedviews.helpers.dataunit.copyhelper;

import eu.unifiedviews.dataunit.DataUnitException;


public interface CopyHelper extends AutoCloseable {
    void copyMetadata(String symbolicName) throws DataUnitException;
    @Override
    public void close() throws DataUnitException;
}
