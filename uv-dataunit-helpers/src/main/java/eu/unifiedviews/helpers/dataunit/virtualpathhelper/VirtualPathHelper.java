package eu.unifiedviews.helpers.dataunit.virtualpathhelper;

import eu.unifiedviews.dataunit.DataUnitException;

public interface VirtualPathHelper extends AutoCloseable {
    public static final String PREDICATE_VIRTUAL_PATH = "http://linked.opendata.cz/ontology/odcs/dataunit/files/virtualPath";

    String getVirtualPath(String symbolicName) throws DataUnitException;

    void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException;

    @Override
    public void close() throws DataUnitException;
}
