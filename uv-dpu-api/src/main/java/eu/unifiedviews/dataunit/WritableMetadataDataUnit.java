package eu.unifiedviews.dataunit;

import org.openrdf.model.URI;

/**
 * {@link MetadataDataUnit} which is writable (adding triples, removing triples).
 */
public interface WritableMetadataDataUnit extends MetadataDataUnit {

    /**
     * Adds a data piece with symbolic name to the data unit. The
     * symbolic name must be unique in scope of this data unit.
     *
     * Inserts at least one metadata triple in form
     * <p><blockquote><pre>
     * &lt;subject&gt; {@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME}  "name literal"
     * </pre></blockquote></p>
     * See {@link MetadataDataUnit} for RDF format description.
     *
     * @param symbolicName symbolic name under which the data will be stored
     * (must be unique in scope of this data unit)
     * @throws DataUnitException
     */
    void addEntry(String symbolicName) throws DataUnitException;

    /**
     * Return the graph name where all new metadata created must be written.
     *
     * @return URI of the graph
     * @throws DataUnitException
     */
    URI getMetadataWriteGraphname() throws DataUnitException;
}
