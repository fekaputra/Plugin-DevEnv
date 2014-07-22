package eu.unifiedviews.helpers.dataunit.virtualpathhelper;

import eu.unifiedviews.dataunit.DataUnitException;

public interface WritableVirtualPathHelper extends VirtualPathHelper {
    void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException;
}
