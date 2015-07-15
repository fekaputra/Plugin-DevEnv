/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
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
