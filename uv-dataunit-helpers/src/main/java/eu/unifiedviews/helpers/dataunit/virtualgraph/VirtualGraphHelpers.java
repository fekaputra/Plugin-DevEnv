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
package eu.unifiedviews.helpers.dataunit.virtualgraph;

import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtilsInstance;
import eu.unifiedviews.helpers.dataunit.metadata.WritableMetadataUtilsInstance;
import eu.unifiedviews.helpers.dataunit.rdf.RdfVocabulary;

/**
 * Static helper nutshell for {@link VirtualGraphHelper}
 * <p>
 * The helper can be used in two ways:
 * <ul>
 * <li>static (and ineffective), quick and dirty way {@code VirtualGraphHelpers.getVirtualGraph(dataUnit, "symbolicName")}.
 * This does the job, but every call opens new connection to the underlying storage and then closes the connection adding a little overhead.</li>
 * <li>dynamic way,
 * <p><blockquote><pre>
 * //first create helper over dataunit
 * VirtualGraphHelper helper = VirtualGraphHelpers.create(dataUnit);
 * try {
 *   // use many times (helper holds its connections open)
 *   String virtualGraph = helper.getVirtualGraph("symbolicName");
 *   helper.setVirtualGraph("symbolicName", "http://myNewGraphName");
 * } finally {
 *   helper.close();
 * }
 * </pre></blockquote></p>
 * </ul>
 */
public class VirtualGraphHelpers {
    private static final VirtualGraphHelpers selfie = new VirtualGraphHelpers();

    private VirtualGraphHelpers() {
    }

    /**
     * Create read-only {@link VirtualGraphHelper} using {@link MetadataDataUnit},
     * returned helper instance method {@link VirtualGraphHelper#setVirtualGraph(String, String)} is unsupported (throws {@link DataUnitException}).
     * @param metadataDataUnit data unit to work with
     * @return helper, do not forget to close it after using it
     */
    public static VirtualGraphHelper create(MetadataDataUnit metadataDataUnit) {
        return selfie.new VirtualGraphHelperImpl(metadataDataUnit);
    }

    /**
     * Create read-write {@link VirtualGraphHelper} using {@link WritableMetadataDataUnit}
     * @param writableMetadataDataUnit data unit to work with
     * @return helper, do not forget to close it after using it
     */
    public static VirtualGraphHelper create(WritableMetadataDataUnit writableMetadataDataUnit) {
        return selfie.new VirtualGraphHelperImpl(writableMetadataDataUnit);
    }

    /**
     * Just do the job, get virtualGraph from given symbolicName.
     * Opens and closes connection to storage each time it is called.
     * @param metadataDataUnit data unit to work with
     * @param symbolicName
     * @return virtual graph
     * @throws DataUnitException
     */
    public static String getVirtualGraph(MetadataDataUnit metadataDataUnit, String symbolicName) throws DataUnitException {
        String result = null;
        VirtualGraphHelper helper = null;
        try {
            helper = create(metadataDataUnit);
            result = helper.getVirtualGraph(symbolicName);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
        return result;
    }

    /**
     * Just do the job, set virtualGraph for given symbolicName.
     * Opens and closes connection to storage each time it is called.
     * @param metadataDataUnit data unit to work with
     * @param symbolicName
     * @param virtualGraph virtual graph
     * @throws DataUnitException
     */
    public static void setVirtualGraph(WritableMetadataDataUnit metadataDataUnit, String symbolicName, String virtualGraph) throws DataUnitException {
        VirtualGraphHelper helper = null;
        try {
            helper = create(metadataDataUnit);
            helper.setVirtualGraph(symbolicName, virtualGraph);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
    }

    private class VirtualGraphHelperImpl implements VirtualGraphHelper {

        private final Logger LOG = LoggerFactory.getLogger(VirtualGraphHelperImpl.class);

        protected MetadataUtilsInstance metadataUtils = null;

        protected WritableMetadataUtilsInstance writableMetadataUtils = null;

        protected MetadataDataUnit dataUnit;
        
        protected WritableMetadataDataUnit writableDataUnit;

        public VirtualGraphHelperImpl(MetadataDataUnit dataUnit) {
            this.dataUnit = dataUnit;
            this.writableDataUnit = null;
        }

        public VirtualGraphHelperImpl(WritableMetadataDataUnit dataUnit) {
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
        public String getVirtualGraph(String symbolicName) throws DataUnitException {
            init();
            metadataUtils.setEntry(symbolicName);
            final Value value;
            try {
                value = metadataUtils.get(RdfVocabulary.UV_VIRTUAL_URI);
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
        public void setVirtualGraph(String symbolicName, String virtualGraph) throws DataUnitException {
            init();
            writableMetadataUtils.setEntry(symbolicName);
            writableMetadataUtils.set(RdfVocabulary.UV_VIRTUAL_URI, virtualGraph);
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
