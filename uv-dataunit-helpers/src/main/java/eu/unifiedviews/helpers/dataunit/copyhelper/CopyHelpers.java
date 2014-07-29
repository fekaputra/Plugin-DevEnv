package eu.unifiedviews.helpers.dataunit.copyhelper;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;

public class CopyHelpers {
    private static final CopyHelpers selfie = new CopyHelpers();

    private CopyHelpers() {

    }

    public static CopyHelper create(MetadataDataUnit source, WritableMetadataDataUnit destination) {
        return selfie.new CopyHelperImpl(source, destination);
    }

    public static void copyMetadataAndContents(String symbolicName, MetadataDataUnit source, WritableMetadataDataUnit destination) throws DataUnitException {
        CopyHelper helper = null;
        try {
            helper = create(source, destination);
            helper.copyMetadataAndContents(symbolicName);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
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
                Update update = connection.prepareUpdate(QueryLanguage.SPARQL, "");
                update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException ex) {
                throw new DataUnitException("", ex);
            } finally {

            }
        }

        @Override
        public void copyMetadataAndContents(String symbolicName) {

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
