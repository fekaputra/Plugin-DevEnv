/**
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
 */
package eu.unifiedviews.dataunit.rdf;

import org.eclipse.rdf4j.model.IRI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;

/**
 * Writable version of {@link RDFDataUnit}, supports adding of new data graphs or existing data graphs which were materialized inside storage externally.
 */
public interface WritableRDFDataUnit extends RDFDataUnit, WritableMetadataDataUnit {

    /**
     * Get IRI prefix which is unique for this data unit. For generating graph names use
     * <p><blockquote><pre>
     * getBaseDataGraphURI() + "/" + "myName"
     * </pre></blockquote></p>
     * to avoid any graph name conflicts (as the prefix is unique already).
     *
     * @return URI-prefix to be used when generating graph names manually (and then adding them by {@link WritableRDFDataUnit#addExistingDataGraph(String, IRI)}
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    IRI getBaseDataGraphURI() throws DataUnitException;

    /**
     * Adds an existing graph with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * The graph should be URI-rooted under the {@link WritableRDFDataUnit#getBaseDataGraphURI()} but it is not enforced
     * (and that is useful for advanced usage - when graphs are loaded externally).
     * If you don't know if to use this function or {@link WritableRDFDataUnit#addNewDataGraph(String)}, use the latter one.
     *
     * @param symbolicName symbolic name under which the graph will be stored (must be unique in scope of this data unit)
     * @param existingDataGraphURI real graph name
     * @throws DataUnitException when symbolicName is not unique or something goes wrong
     */
    void addExistingDataGraph(String symbolicName, IRI existingDataGraphURI) throws DataUnitException;

    /**
     * Generates unique graph name prefixed by the {@link WritableRDFDataUnit#getBaseDataGraphURI()}.
     * Returns the newly generated full graph URI to work with.
     * It does create the graph in storage, and it does add the graph metadata entry into the dataunit under provided symbolicName.
     *
     * @param symbolicName
     * @return URI of newly created data graph
     * @throws DataUnitException when symbolicName is not unique or something goes wrong
     */
    IRI addNewDataGraph(String symbolicName) throws DataUnitException;

    /**
     * Update an existing graph symbolic name with new existingDataGraphURI
     * The symbolic name must be unique in scope of this data unit.
     * The symbolic name must exist in data unit prior the execution.
     *
     * The graph should be URI-rooted under the {@link WritableRDFDataUnit#getBaseDataGraphURI()} but it is not enforced
     * (and that is useful for advanced usage - when graphs are loaded externally).
     *
     * @param symbolicName symbolic name under which the graph will be stored (must be unique in scope of this data unit)
     * @param newDataGraphURI new real graph name
     * @throws DataUnitException when symbolicName is not unique or something goes wrong
     * @deprecated Do not use, may be removed soon and replaced by proper helper.
     */
    @Deprecated
    void updateExistingDataGraph(String symbolicName, IRI newDataGraphURI) throws DataUnitException;
}

