package eu.unifiedviews.dataunit;

import java.util.NoSuchElementException;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import eu.unifiedviews.dpu.DPU;

/**
 * Represents data which is decorated by RDF metadata. Contains many data entries, each entry is decorated at least by single metadata
 * triple
 * <p><blockquote><pre>
 * &lt;subject&gt; {@value eu.unifiedviews.dataunit.MetadataDataUnit#PREDICATE_SYMBOLIC_NAME} "name literal"
 * </pre></blockquote></p>
 * where &lt;subject&gt; is unknown to the {@link MetadataDataUnit}'s user (may also be stored as blank node by application) and is only used to group metadata
 * triples related to a single data entry.
 * <p>
 * Symbolic name predicate (see {@value #PREDICATE_SYMBOLIC_NAME}) is used to give an arbitrary (user
 * chosen) name to data entry. Symbolic name can be used to present data entry to user, to let the user configure {@link DPU} to process specifically named data
 * entry (filter, selector). Symbolic name is meant as ID or primary key to the data entry in scope of {@link MetadataDataUnit}.
 * <p>
 * The triples which represent the
 * metadata itself, are stored inside {@link Repository}, but for the user of this dataunit only {@link RepositoryConnection} can be obtained to work with raw
 * metadata triples. Triples can be spread in one or more Graphs inside {@link Repository}, so to query them one has to obtain all the graphnames to work with
 * by using {@link MetadataDataUnit#getMetadataGraphnames()}.
 * <p>
 * <b>Example 1: typical work pattern with metadata entries</b>
 * <p><blockquote><pre>
 * // Setup our iteration variable
 * MetadataDataUnit.Iteration iteration = null;
 * try {
 *   // Obtain iteration
 *   iteration = dataUnit.getIteration();
 *   // Iterate the entries
 *   while (iteration.hasNext()) {
 *       MetadataDataUnit.Entry entry = iteration.next();
 *       LOG.info("I contain piece of data with this symbolic name " + entry.getSymbolicName());
 *       // do something more with entry
 *       // ...
 *     }
 *   } catch (DataUnitException ex) {
 *     // There may be a problem iterating entries
 *     throw new SomeException("Some meaningful message", ex);
 *   } finally {
 *     // closing iteration is obligatory
 *     if (iteration != null) {
 *       try {
 *         iteration.close();
 *       } catch (DataUnitException ex) {
 *         LOG.warn("Error in close", ex);
 *       }
 *     }
 *   }
 * </pre></blockquote></p>
 * <p>
 * <b>Example 2: typical work pattern with raw connection and graphs</b>
 * </pre></blockquote></p>
 * // Setup our connection variable
 * RepositoryConnection connection = null;
 * try {
 *   // Obtain connection from dataUnit
 *   connection = dataUnit.getConnection();
 *   // Build a query
 *   TupleQuery query = connection.prepareTupleQuery(...);
 *   // Start building dataset for query
 *   DatasetImpl dataset = new DatasetImpl();
 *   // Use all graph URIs the dataunit provides to feed the dataset - we want the query to run over all graphs in this dataunit
 *   for (URI graph : dataUnit.getMetadataGraphnames()) {
 *     dataset.addDefaultGraph(graph);
 *   }
 *   // Bind the dataset to the query
 *   query.setDataset(dataset);
 *   // Prepare result variable
 *   TupleQueryResult statements = null;
 *   try {
 *     // Run query
 *     statements = query.evaluate();
 *     // Iterate over results
 *     while (statements.hasNext()) {
 *       BindingSet result = statements.next();
 *       // do something with result binding
 *       // ...
 *     }
 *   } catch (QueryEvaluationException ex1) {
 *     // There may be a problem evaluating query
 *     throw new SomeException("Some meaningful message", ex1);
 *   } finally {
 *     // closing tuple result is obligatory
 *     if (statements != null) {
 *       try {
 *         statements.close();
 *       } catch (QueryEvaluationException ex1) {
 *         LOG.warn("Error in close", ex1);
 *       }
 *     }
 *   }
 * } catch (RepositoryException ex) {
 *   throw new SomeException("Some meaningful message", ex);
 * } finally {
 *   // closing connection is obligatory
 *   if (connection != null) {
 *     try {
 *       connectin.close();
 *     } catch (RepositoryException ex) {
 *       LOG.warn("Error in close", ex);
 *     }
 *   }
 * }
 * </pre></blockquote></p>
 */
public interface MetadataDataUnit extends DataUnit {

    /**
     * Symbolic name given to the data entry is stored in {@link Repository} as a single triple
     * <p><blockquote><pre>
     * &lt;subject&gt; &lt;{@value #PREDICATE_SYMBOLIC_NAME}&gt; "name"
     * </pre></blockquote></p>
     * so this predicate is here (and public) to enable users of this
     * class to access entries by using raw connection and SPARQL querying the database.
     */
    final String PREDICATE_SYMBOLIC_NAME = "http://unifiedviews.eu/DataUnit/MetadataDataUnit/symbolicName";

    /**
     * Returns connection to the {@link Repository} where the metadata entries are stored. Connection is returned initialized and open, user is obliged to close
     * the connection after he has done work with it. See {@link MetadataDataUnit}:Example 2 for example usage.
     *
     * @return Connection to repository.
     * @throws DataUnitException If something went wrong during the creation of the connection.
     */
    RepositoryConnection getConnection() throws DataUnitException;

    /**
     * Returns set of URIs of graphs where metadata are stored. See {@link MetadataDataUnit}:Example 2 for example usage.
     *
     * @return URI representation of graph where meta data are stored.
     * @throws eu.unifiedviews.dataunit.DataUnitException
     */
    Set<URI> getMetadataGraphnames() throws DataUnitException;

    /**
     * Interface describing one piece of data which can be decorated by metadata. See {@link MetadataDataUnit}:Example 1 for example usage.
     */
    interface Entry {

        /**
         * Symbolic name is meant as ID or primary key to the data entry in scope of {@link MetadataDataUnit}.
         *
         * @return Symbolic name under which the data is stored inside this data unit.
         * @throws eu.unifiedviews.dataunit.DataUnitException
         */
        String getSymbolicName() throws DataUnitException;
    }

    /**
     * Iteration over data pieces. Has to be closed in finally blocks. See {@link MetadataDataUnit}:Example 1 for example usage.
     */
    interface Iteration extends AutoCloseable {
        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        boolean hasNext() throws DataUnitException;

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        MetadataDataUnit.Entry next() throws DataUnitException;

        @Override
        void close() throws DataUnitException;
    }

    /**
     * Get the iteration over metadata entries. See {@link MetadataDataUnit}:Example 1 for example usage.
     *
     * @return iteration over metadata entries
     * @throws DataUnitException
     */
    MetadataDataUnit.Iteration getIteration() throws DataUnitException;

}
