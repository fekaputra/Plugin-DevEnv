package eu.unifiedviews.helpers.dataunit.copyhelper;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import org.openrdf.model.URI;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyHelpers {
    private static final CopyHelpers selfie = new CopyHelpers();

    private CopyHelpers() {

    }

    public static CopyHelper create(MetadataDataUnit source, WritableMetadataDataUnit destination) {
        return selfie.new CopyHelperImpl(source, destination);
    }

    public static void copyMetadata(String symbolicName, MetadataDataUnit source, WritableMetadataDataUnit destination) throws DataUnitException {
        CopyHelper helper = null;
        try {
            helper = create(source, destination);
            helper.copyMetadata(symbolicName);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
    }

    private class CopyHelperImpl implements CopyHelper {

        protected static final String SYMBOLIC_NAME_BINDING = "symbolicName";
        
        /**
         * Copy only first level.
         */
        protected static final String UPDATE = 
                "INSERT {?s ?p ?o} WHERE {"
                        + "?s ?p ?o."
                        + "?s <http://unifiedviews.eu/DataUnit/MetadataDataUnit/symbolicName> ?" + SYMBOLIC_NAME_BINDING + "."
                        + "}";

        private final Logger LOG = LoggerFactory.getLogger(CopyHelperImpl.class);

        private MetadataDataUnit source;

        private WritableMetadataDataUnit destination;

        private RepositoryConnection connection = null;

        public CopyHelperImpl(MetadataDataUnit source, WritableMetadataDataUnit destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public void copyMetadata(String symbolicName) throws DataUnitException {
            try {
                if (connection == null) {
                    connection = source.getConnection();
                }
                // Select all triples <bnode> symbolicName "symbolicName"
                // add all of them to destination data unit
                // (we use source connection - both run on same storage).

                final Update update = connection.prepareUpdate(
                        QueryLanguage.SPARQL, UPDATE);

                update.setBinding(SYMBOLIC_NAME_BINDING, 
                        connection.getValueFactory().createLiteral(symbolicName));

                final DatasetImpl dataset = new DatasetImpl();
                for (URI item : source.getMetadataGraphnames()) {
                    dataset.addDefaultGraph(item);
                }
                dataset.setDefaultInsertGraph(
                        destination.getMetadataWriteGraphname());

                update.setDataset(dataset);
                update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException ex) {
                throw new DataUnitException("", ex);
            }
        }

        @Override
        public void close() throws DataUnitException {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
    }
}
