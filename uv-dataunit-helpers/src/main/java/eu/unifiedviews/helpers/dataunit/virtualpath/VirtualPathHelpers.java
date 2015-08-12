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
package eu.unifiedviews.helpers.dataunit.virtualpath;

import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesVocabulary;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtilsInstance;
import eu.unifiedviews.helpers.dataunit.metadata.WritableMetadataUtilsInstance;

/**
 * Static helper nutshell for {@link VirtualPathHelper}
 * <p>
 * The helper can be used in two ways:
 * <ul>
 * <li>static (and ineffective), quick and dirty way {@code VirtualPathHelpers.getVirtualPath(dataUnit, "symbolicName")}.
 * This does the job, but every call opens new connection to the underlying storage and then closes the connection adding a little overhead.</li>
 * <li>dynamic way,
 * <p><blockquote><pre>
 * //first create helper over dataunit
 * VirtualPathHelper helper = VirtualPathHelpers.create(dataUnit);
 * try {
 *   // use many times (helper holds its connections open)
 *   String virtualPath = helper.getVirtualPath("symbolicName");
 *   helper.setVirtualPath("symbolicName", "new/book/pages.csv");
 * } finally {
 *   helper.close();
 * }
 * </pre></blockquote></p>
 * </ul>
 */
public class VirtualPathHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualPathHelpers.class);

    private static final VirtualPathHelpers selfie = new VirtualPathHelpers();

    private VirtualPathHelpers() {
    }

    /**
     * Create read-only {@link VirtualPathHelper} using {@link MetadataDataUnit},
     * returned helper instance method {@link VirtualPathHelper#setVirtualPath(String, String)} is unsupported (throws {@link DataUnitException}).
     * @param metadataDataUnit data unit to work with
     * @return helper, do not forget to close it after using it
     */
    public static VirtualPathHelper create(MetadataDataUnit metadataDataUnit) {
        return selfie.new VirtualPathHelperImpl(metadataDataUnit);
    }

    /**
     * Create read-write {@link VirtualPathHelper} using {@link WritableMetadataDataUnit}
     * @param writableMetadataDataUnit data unit to work with
     * @return helper, do not forget to close it after using it
     */
    public static VirtualPathHelper create(WritableMetadataDataUnit writableMetadataDataUnit) {
        return selfie.new VirtualPathHelperImpl(writableMetadataDataUnit);
    }

    /**
     * Just do the job, get virtualPath from given symbolicName.
     * Opens and closes connection to storage each time it is called.
     * @param metadataDataUnit data unit to work with
     * @param symbolicName
     * @return virtual path
     * @throws DataUnitException
     */
    public static String getVirtualPath(MetadataDataUnit metadataDataUnit, String symbolicName) throws DataUnitException {
        String result = null;
        VirtualPathHelper helper = null;
        try {
            helper = create(metadataDataUnit);
            result = helper.getVirtualPath(symbolicName);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
        return result;
    }

    /**
     * Just do the job, set virtualPath for given symbolicName.
     * Opens and closes connection to storage each time it is called.
     * @param metadataDataUnit data unit to work with
     * @param symbolicName
     * @param virtualPath virtual path
     * @throws DataUnitException
     */
    public static void setVirtualPath(WritableMetadataDataUnit metadataDataUnit, String symbolicName, String virtualPath) throws DataUnitException {
        VirtualPathHelper helper = null;
        try {
            helper = create(metadataDataUnit);
            helper.setVirtualPath(symbolicName, virtualPath);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
    }

    private class VirtualPathHelperImpl implements VirtualPathHelper {

        protected MetadataUtilsInstance metadataUtils = null;

        protected WritableMetadataUtilsInstance writableMetadataUtils = null;

        protected MetadataDataUnit dataUnit;

        protected WritableMetadataDataUnit writableDataUnit;

        public VirtualPathHelperImpl(MetadataDataUnit dataUnit) {
            this.dataUnit = dataUnit;
            this.writableDataUnit = null;
        }

        public VirtualPathHelperImpl(WritableMetadataDataUnit dataUnit) {
            this.dataUnit = null;
            this.writableDataUnit = dataUnit;
        }

        private void init() throws DataUnitException {
            if (metadataUtils == null) {
                if (writableDataUnit == null) {
                    // Read only.
                    this.metadataUtils = MetadataUtils.create(dataUnit);
                } else {
                    this.writableMetadataUtils = MetadataUtils.create(writableDataUnit);
                    this.metadataUtils = this.writableMetadataUtils;
                }
            }
        }

        @Override
        public String getVirtualPath(String symbolicName) throws DataUnitException {
            init();
            metadataUtils.setEntry(symbolicName);
            final Value value;
            try {
                value = metadataUtils.get(FilesVocabulary.UV_VIRTUAL_PATH);
            } catch (DPUException ex) {
                throw new DataUnitException(ex);
            }
            if (value == null) {
                return null;
            } else {
                return value.stringValue();
            }
        }

        @Override
        public void setVirtualPath(String symbolicName, String virtualGraph) throws DataUnitException {
            init();
            writableMetadataUtils.setEntry(symbolicName);
            writableMetadataUtils.set(FilesVocabulary.UV_VIRTUAL_PATH, virtualGraph);
        }

        @Override
        public void close() {
            if (metadataUtils != null) {
                try {
                    // If writableDataUnit != null, then as writableDataUnit == metadataUtils
                    // this also close writableDataUnit.
                    metadataUtils.close();
                } catch (DataUnitException ex) {
                    LOG.warn("Error in close.", ex);
                }
            }
        }
    }
}
