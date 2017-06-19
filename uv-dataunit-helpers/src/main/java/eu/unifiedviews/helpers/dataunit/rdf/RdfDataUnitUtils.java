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
package eu.unifiedviews.helpers.dataunit.rdf;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;

/**
 * Utils for working with {@link RDFDataUnit}. DPU developer should NOT use this class directly - he should use {@link RDFHelper}.
 * 
 * @author Škoda Petr
 */
public class RdfDataUnitUtils {

    /**
     * InMemory representation of RDF entry.
     */
    public static class InMemoryEntry implements RDFDataUnit.Entry {

        private final IRI graphUri;

        private final String symbolicName;

        public InMemoryEntry(IRI graphUri, String symbolicName) {
            this.graphUri = graphUri;
            this.symbolicName = symbolicName;
        }

        @Override
        public IRI getDataGraphURI() throws DataUnitException {
            return graphUri;
        }

        @Override
        public String getSymbolicName() throws DataUnitException {
            return symbolicName;
        }

    }

    private RdfDataUnitUtils() {

    }

    /**
     * Add entry with generated graph name.
     * 
     * @param dataUnit
     * @param symbolicName
     * @return Wrap of a new entry.
     * @throws DataUnitException
     */
    public static InMemoryEntry addGraph(WritableRDFDataUnit dataUnit, String symbolicName)
            throws DataUnitException {
        final IRI uri = dataUnit.addNewDataGraph(symbolicName);
        return new InMemoryEntry(uri, symbolicName);
    }

    /**
     * Add given graph under given symbolic name.
     * 
     * @param dataUnit
     * @param symbolicName
     * @param uri
     * @return Wrap of a new entry.
     * @throws DataUnitException
     */
    public static InMemoryEntry addGraph(WritableRDFDataUnit dataUnit, String symbolicName, IRI uri)
            throws DataUnitException {
        dataUnit.addExistingDataGraph(symbolicName, uri);
        return new InMemoryEntry(uri, symbolicName);
    }

    /**
     * @param entry
     * @return IRI of graph represented by this entry.
     * @throws DataUnitException
     */
    public static IRI asGraph(RDFDataUnit.Entry entry) throws DataUnitException {
        return entry.getDataGraphURI();
    }

    /**
     * Convert RDF graph entries into their respective URIs.
     * 
     * @param entries
     * @return
     * @throws DataUnitException
     */
    public static IRI[] asGraphs(List<RDFDataUnit.Entry> entries) throws DataUnitException {
        final List<IRI> result = new ArrayList<>(entries.size());
        for (RDFDataUnit.Entry entry : entries) {
            result.add(asGraph(entry));
        }
        return result.toArray(new IRI[0]);
    }

}
