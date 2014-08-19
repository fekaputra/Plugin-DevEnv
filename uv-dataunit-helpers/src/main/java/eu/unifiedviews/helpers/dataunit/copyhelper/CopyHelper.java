package eu.unifiedviews.helpers.dataunit.copyhelper;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;

/**
 * Helper for copying all metadata related to single symbolicName, that is these triples:
 * <ul>
 *  <li>&ltsubject&gt p:symbolicName "name"</li>
 *  <li>&ltsubject&gt ?p ?v</li>
 * <ul>
 * <p>
 * In future versions it will be improved to copy whole triple-tree rooted at &lt;subject%gt;
 * <p>
 * User of the helper is obliged to close this helper after he finished work with it (closes underlying connections).
 * <p>
 * For example usage see {@link CopyHelpers}.
 */
public interface CopyHelper extends AutoCloseable {
    /**
     * Copy all metadata related to single symbolicName
     * @param symbolicName key to {@link MetadataDataUnit.Entry} which will be copied
     * @throws DataUnitException
     */
    void copyMetadata(String symbolicName) throws DataUnitException;

    @Override
    public void close() throws DataUnitException;
}
