package eu.unifiedviews.helpers.dataunit.virtualpathhelper;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataHelper;

public class VirtualPathHelpers {
    private static final VirtualPathHelpers selfie = new VirtualPathHelpers();

    private VirtualPathHelpers() {
    }

    public static VirtualPathHelper create(MetadataDataUnit filesDataUnit) {
        return selfie.new VirtualPathHelperImpl(filesDataUnit);
    }

    public static VirtualPathHelper create(WritableMetadataDataUnit writableFilesDataUnit) {
        return selfie.new WritableVirtualPathHelperImpl(writableFilesDataUnit);
    }

    public static String getVirtualPath(MetadataDataUnit filesDataUnit, String symbolicName) throws DataUnitException {
        String result = null;
        VirtualPathHelper helper = null;
        try {
            helper = create(filesDataUnit);
            result = helper.getVirtualPath(symbolicName);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
        return result;
    }

    public static void setVirtualPath(WritableMetadataDataUnit writableFilesDataUnit, String symbolicName, String virtualPath) throws DataUnitException {
        VirtualPathHelper helper = null;
        try {
            helper = create(writableFilesDataUnit);
            helper.setVirtualPath(symbolicName, virtualPath);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
    }

    private class VirtualPathHelperImpl implements VirtualPathHelper {
        private final Logger LOG = LoggerFactory.getLogger(VirtualPathHelperImpl.class);

        private MetadataDataUnit dataUnit;

        private RepositoryConnection connection = null;

        public VirtualPathHelperImpl(MetadataDataUnit dataUnit) {
            this.dataUnit = dataUnit;
        }

        @Override
        public String getVirtualPath(String symbolicName) throws DataUnitException {
            if (connection == null) {
                connection = dataUnit.getConnection();
            }
            String result = null;
            try {
                result = MetadataHelper.get(connection, dataUnit.getMetadataGraphnames(), symbolicName, VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
            } catch (QueryEvaluationException | RepositoryException | MalformedQueryException ex) {
                throw new DataUnitException(ex);
            }
            return result;
        }

        @Override
        public void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException {
            throw new UnsupportedOperationException();
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

    private class WritableVirtualPathHelperImpl extends VirtualPathHelperImpl {
        private WritableMetadataDataUnit dataUnit;

        private RepositoryConnection connection = null;

        public WritableVirtualPathHelperImpl(WritableMetadataDataUnit dataUnit) {
            super(dataUnit);
            this.dataUnit = dataUnit;
        }

        @Override
        public void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException {
            if (connection == null) {
                connection = dataUnit.getConnection();
            }
            try {
                MetadataHelper.set(connection, dataUnit.getMetadataWriteGraphname(), symbolicName, VirtualPathHelper.PREDICATE_VIRTUAL_PATH, virtualPath);
            } catch (RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
                throw new DataUnitException(ex);
            }
        }
    }

}
