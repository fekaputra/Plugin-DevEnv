package eu.unifiedviews.helpers.dataunit.copyhelper;

import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.helpers.dataunit.rdfhelper.RDFHelper;

public class AddAllHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AddAllHelper.class);
    
    /**
     * Add all data from source DataUnit into destination DataUnit. The method must not
     * modify the current parameter (unit).
     *
     * @param dataunit {@link eu.unifiedviews.dataunit.rdf.RDFDataUnit} to add
     * from
     * @throws eu.unifiedviews.dataunit.DataUnitException
     * @throws IllegalArgumentException if some property of an element of the
     * specified dataunit prevents it from being added to this dataunit
     */
    public static void addAll(RDFDataUnit source, WritableRDFDataUnit destination) throws DataUnitException {
        if (!destination.getClass().equals(source.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class "
                    + destination.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + source.getClass().getCanonicalName() + ".");
        }

        RepositoryConnection connection = null;
        try {
            connection = destination.getConnection();

            String targetGraphName = destination.addNewDataGraph("all").stringValue();
            for (URI sourceGraph : RDFHelper.getGraphs(source)) {
                String sourceGraphName = sourceGraph.stringValue();

                LOG.info("Trying to merge {} triples from <{}> to <{}>.",
                        connection.size(sourceGraph), sourceGraphName,
                        targetGraphName);

                String mergeQuery = String.format("ADD <%s> TO <%s>", sourceGraphName,
                        targetGraphName);

                Update update = connection.prepareUpdate(
                        QueryLanguage.SPARQL, mergeQuery);

                update.execute();

                LOG.info("Merged {} triples from <{}> to <{}>.",
                        connection.size(sourceGraph), sourceGraphName,
                        targetGraphName);
            }
        } catch (MalformedQueryException ex) {
            LOG.error("NOT VALID QUERY: {}", ex);
        } catch (RepositoryException | DataUnitException | UpdateExecutionException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }
}
