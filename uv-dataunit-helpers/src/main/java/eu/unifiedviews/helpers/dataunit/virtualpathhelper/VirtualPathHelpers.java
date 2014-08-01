package eu.unifiedviews.helpers.dataunit.virtualpathhelper;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.helpers.dataunit.internal.metadata.MetadataHelper;
import eu.unifiedviews.helpers.dataunit.internal.metadata.MetadataHelpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualPathHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualPathHelpers.class);

    private static final VirtualPathHelpers selfie = new VirtualPathHelpers();

    private VirtualPathHelpers() {
    }

    public static VirtualPathHelper create(MetadataDataUnit filesDataUnit) {
        return selfie.new VirtualPathHelperImpl(filesDataUnit);
    }

    public static VirtualPathHelper create(WritableMetadataDataUnit writableFilesDataUnit) {
        return selfie.new VirtualPathHelperImpl(writableFilesDataUnit);
    }

    public static String getVirtualPath(MetadataDataUnit filesDataUnit, String symbolicName) throws DataUnitException {
        String result = null;
        VirtualPathHelper helper = null;
        try {
            helper = create(filesDataUnit);
            result = helper.getVirtualPath(symbolicName);
        } finally {
            if (helper != null) {
                try {
                    helper.close();
                } catch (DataUnitException ex) {
                    LOG.warn("Error in close.", ex);
                }
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
                try {
                    helper.close();
                } catch (DataUnitException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
    }

    private class VirtualPathHelperImpl implements VirtualPathHelper {
        private final Logger LOG = LoggerFactory.getLogger(VirtualPathHelperImpl.class);

        protected MetadataHelper metadataHelper;

        public VirtualPathHelperImpl(MetadataDataUnit dataUnit) {
            this.metadataHelper = MetadataHelpers.create(dataUnit);
        }

        public VirtualPathHelperImpl(WritableMetadataDataUnit dataUnit) {
            this.metadataHelper = MetadataHelpers.create(dataUnit);
        }

        @Override
        public String getVirtualPath(String symbolicName) throws DataUnitException {
            return metadataHelper.get(symbolicName, VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
        }

        @Override
        public void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException {
            metadataHelper.set(symbolicName, VirtualPathHelper.PREDICATE_VIRTUAL_PATH, virtualPath);
        }

        @Override
        public void close() throws DataUnitException {
            if (metadataHelper != null) {
                try {
                    metadataHelper.close();
                } catch (DataUnitException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
    }
}
