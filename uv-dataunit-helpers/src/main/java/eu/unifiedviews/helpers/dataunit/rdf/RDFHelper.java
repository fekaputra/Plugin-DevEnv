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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;
import java.util.List;

/**
 * Helper to simplify fetching RDF graphs entries from input {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit},
 * operating with RDF graphs, and writing RDF graphs to output {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}.
 * This class should be used as the main class by DPU developers who needs helpers to operate with {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}.
 */
public class RDFHelper {
    /**
     * Gets entries from the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}.
     * This method internally iterates over entries in the {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} and stores the entries into one {@link Map} of
     * entries.
     * Only suitable for ~10000 of entries in the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}, because all entries are stored into a {@link Map}. For
     * bigger amounts of entries, it is better to directly use {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit.Iteration} to iterate over the
     * entries and directly process them.
     * 
     * @param rdfDataUnit
     *            data unit from which the entries are fetched
     * @return {@link Map} containing all entries, symbolic names are used as keys
     * @throws DataUnitException
     */
    public static Map<String, RDFDataUnit.Entry> getGraphsMap(RDFDataUnit rdfDataUnit) throws DataUnitException {
        if (rdfDataUnit == null) {
            return new LinkedHashMap<>();
        }
        RDFDataUnit.Iteration iteration = rdfDataUnit.getIteration();
        Map<String, RDFDataUnit.Entry> resultMap = new LinkedHashMap<>();
        try {
            while (iteration.hasNext()) {
                RDFDataUnit.Entry entry = iteration.next();
                resultMap.put(entry.getSymbolicName(), entry);
            }
        } finally {
            iteration.close();
        }
        return resultMap;
    }

    /**
     * Gets entries from the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}.
     * This method internally iterates over entries in the {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} and stores the entries into one {@link Set} of
     * entries.
     * Only suitable for ~10000 of entries in the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}, because all entries are stored into a {@link Set}. For
     * bigger amounts of entries, it is better to directly use {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit.Iteration} to iterate over the
     * entries and directly process them.
     * 
     * @param rdfDataUnit
     *            data unit from which the entries are fetched
     * @return {@link Set} containing all entries
     * @throws DataUnitException
     */
    public static Set<RDFDataUnit.Entry> getGraphs(RDFDataUnit rdfDataUnit) throws DataUnitException {
        if (rdfDataUnit == null) {
            return new LinkedHashSet<>();
        }
        RDFDataUnit.Iteration iteration = rdfDataUnit.getIteration();
        Set<RDFDataUnit.Entry> resultSet = new LinkedHashSet<>();
        try {
            while (iteration.hasNext()) {
                RDFDataUnit.Entry entry = iteration.next();
                resultSet.add(entry);
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }

    /**
     * Gets entries from the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}.
     * This method internally iterates over entries in the {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} and stores the entries into one {@link Set} of
     * of data graph URIs.
     * Only suitable for ~10000 of entries in the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}, because all entries are stored into a {@link Set}. For
     * bigger amounts of entries, it is better to directly use {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit.Iteration} to iterate over the
     * entries and directly process them.
     * <p>
     * Useful for feeding {@link Dataset} class (together with {@link DatasetBuilder}):
     * <p>
     * <blockquote>
     * 
     * <pre>
     * query.setDataset(new DatasetBuilder().withNamedGraphs(RDFHelper.getGraphsURISet(inputDataUnit)).build())
     * </pre>
     * 
     * </blockquote>
     * </p>
     * 
     * @param rdfDataUnit
     *            data unit from which the entries are fetched
     * @return {@link Set} containing all graphs from the rdfDataUnit
     * @throws DataUnitException
     */
    public static Set<IRI> getGraphsURISet(RDFDataUnit rdfDataUnit) throws DataUnitException {
        if (rdfDataUnit == null) {
            return new LinkedHashSet<>();
        }
        RDFDataUnit.Iteration iteration = rdfDataUnit.getIteration();
        Set<IRI> resultSet = new LinkedHashSet<>();
        try {
            while (iteration.hasNext()) {
                RDFDataUnit.Entry entry = iteration.next();
                resultSet.add(entry.getDataGraphURI());
            }
        } finally {
            iteration.close();
        }
        return resultSet;
    }

    /**
     * Gets entries from the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}.
     * This method internally iterates over entries in the {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} and stores the entries into one array of
     * of data graph URIs.
     * Only suitable for ~10000 of entries in the given {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit}, because all entries are stored into an array. For
     * bigger amounts of entries, it is better to directly use {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit.Iteration} to iterate over the
     * entries and directly process them.
     * <p>
     * Useful for methods from {@link RepositoryConnection} which are varargs. Such as
     * <p>
     * <blockquote>
     * 
     * <pre>
     * connection.add(statement, RDFHelper.getGraphsURIArray(outputDataUnit));
     * </pre>
     * 
     * </blockquote>
     * </p>
     * 
     * @param rdfDataUnit
     *            data unit from which the entries are fetched
     * @return array of URIs containing all graphs from the rdfDataUnit
     * @throws DataUnitException
     */
    public static IRI[] getGraphsURIArray(RDFDataUnit rdfDataUnit) throws DataUnitException {
        return getGraphsURISet(rdfDataUnit).toArray(new IRI[0]);
    }

    /**
     * To create instance of Dataset class (from OpenRDF API) for which all RDF data graphs from {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} are set as
     * default graphs.
     * Suitable for further querying.
     * <p>
     * Used to shorten this (more verbose) way of creating equivalent dataset:
     * <p>
     * <blockquote>
     * 
     * <pre>
     * query.setDataset(new DatasetBuilder().withDefaultGraphs(RDFHelper.getGraphsURISet(inputDataUnit)).build())
     * </pre>
     * 
     * </blockquote>
     * </p>
     * into shortened form using this method:
     * <p>
     * <blockquote>
     * 
     * <pre>
     * query.setDataset(RDFHelper.getDatasetWithDefaultGraphs(inputDataUnit)))
     * </pre>
     * 
     * </blockquote>
     * </p>
     * Beware that this method refuses to create dataset with empty defaultGraphs parameter. This is to prevent
     * bugs and errors, as with different storages the empty defaultGraphs may be interpreted in different ways.
     * 
     * @param rdfDataUnit
     *            data unit from which all RDF data graphs are obtained
     * @return {@link Dataset} with defaultGraphs set to all RDF data graphs from the rdfDataUnit
     * @throws DataUnitException
     *             when rdfDataUnit does contain any graph or connection errors occur
     */
    public static Dataset getDatasetWithDefaultGraphs(RDFDataUnit rdfDataUnit) throws DataUnitException {
        Set<IRI> graphsUriSet = RDFHelper.getGraphsURISet(rdfDataUnit);
        if (graphsUriSet.isEmpty()) {
            throw new DataUnitException("Trying to build dataset from dataunit, which contains no data graphs");
        }
        return new DatasetBuilder().withDefaultGraphs(graphsUriSet).build();
    }

    /**
     * Creates new unique RDF data unit entry under the symbolic name being equal to graphName.
     * 
     * @param rdfDataUnit
     * @param graphName
     * @return new entry
     * @throws DataUnitException
     */
    public static RDFDataUnit.Entry createGraph(WritableRDFDataUnit rdfDataUnit, final String graphName)
            throws DataUnitException {
        return RdfDataUnitUtils.addGraph(rdfDataUnit, graphName);
    }

    /**
     * Add existing graph with the given graphURI to the rdfDataUnit under the symbolic name equal to graphURI.
     * 
     * @param rdfDataUnit
     * @param graphURI
     * @return new entry
     * @throws DataUnitException
     */
    public static RDFDataUnit.Entry addGraph(WritableRDFDataUnit rdfDataUnit, IRI graphURI)
            throws DataUnitException {
        String graphName = graphURI.toString();
        return RdfDataUnitUtils.addGraph(rdfDataUnit, graphName, graphURI);
    }

    /**
     * Add existing graph with the given graphURI to the rdfDataUnit under the symbolic name equal to graphName
     * 
     * @param rdfDataUnit
     * @param graphURI
     * @param graphName
     * @return
     * @throws DataUnitException
     */
    public static RDFDataUnit.Entry addGraph(WritableRDFDataUnit rdfDataUnit, IRI graphURI, final String graphName)
            throws DataUnitException {
        return RdfDataUnitUtils.addGraph(rdfDataUnit, graphName, graphURI);
    }

    /**
     * Gets data graph IRI of the RDF data unit entry
     * 
     * @param entry
     *            RDF data unit entry
     * @return data graph IRI of the entry
     * @throws DataUnitException
     */
    public static IRI asGraph(RDFDataUnit.Entry entry) throws DataUnitException {
        return RdfDataUnitUtils.asGraph(entry);
    }

    /**
     * Gets list of data graph URIs from list of RDF data unit entries
     * 
     * @param entries
     *            List of RDF data unit entries
     * @return List of data graph URIs of the given entries
     * @throws DataUnitException
     */
    public static IRI[] asGraphs(List<RDFDataUnit.Entry> entries) throws DataUnitException {
        return RdfDataUnitUtils.asGraphs(entries);
    }

}
