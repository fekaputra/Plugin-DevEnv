package eu.unifiedviews.helpers.dataunit.resourcehelper;

import java.util.Map;

import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Helper providing easy way to attach Resource describing metadata to
 * particular {@link eu.unifiedviews.dataunit.MetadataDataUnit.Entry}.
 * <p>
 * Each entry can have exactly one Resource entity attached to it.
 * <p>
 * For example usage see {@link ResourceHelpers}
 * <p>
 * Each instance has to be closed after using it.
 * <p>
 * Internal storage format of the resource:
 * <p><blockquote><pre>
 * &lt;subject&gt; {@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME} <>
 * &lt;subject&gt; &lt;{@value #PREDICATE_HAS_MAP}&gt; &lt;generatedUniqueUriOrBlankNode1&gt;
 * &lt;generatedUniqueUriOrBlankNode1&gt; &lt;{@value #PREDICATE_MAP_TITLE}&gt; "literal mapName"
 * &lt;generatedUniqueUriOrBlankNode1&gt; &lt;{@value #PREDICATE_MAP_CONTAINS}&gt; &lt;generatedUniqueUriOrBlankNode2&gt;
 * &lt;generatedUniqueUriOrBlankNode2&gt; &lt;{@value #PREDICATE_MAP_ENTRY_KEY}&gt; "key literal"
 * &lt;generatedUniqueUriOrBlankNode2&gt; &lt;{@value #PREDICATE_MAP_ENTRY_VALUE}&gt; "value literal"
 * ...
 * </pre></blockquote></p>
 */
public interface ResourceHelper extends AutoCloseable {

    /**
     * Value: {@value #STORAGE_MAP_NAME}, value used to specify mapName property of underlying storage map.
     */
    public static final String STORAGE_MAP_NAME = "http://unifiedviews.eu/ResourceHelper/mapName";

    /**
     * Obtain Resource entity attached to metadata entry named symbolicName.
     *
     * @param symbolicName entry's symbolic name
     * @return metadata map or null if no such map exists
     * @throws DataUnitException
     */
    Map<String, String> getResource(String symbolicName) throws DataUnitException;

    /**
     * Attach Resource entity to metadata entry named symbolicName. Any previously attached Resource entity will be replaced with new one.
     * @param symbolicName entry's symbolic name
     * @param resource the key-value map representing resource
     * @throws DataUnitException
     */
    void setResource(String symbolicName, Map<String, String> resource) throws DataUnitException;

    @Override
    public void close() throws DataUnitException;
}
