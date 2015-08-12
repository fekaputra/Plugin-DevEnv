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
package eu.unifiedviews.helpers.dataunit.resource;

import java.text.ParseException;
import java.util.Map;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.WritableMetadataDataUnit;
import eu.unifiedviews.helpers.dataunit.map.MapHelper;
import eu.unifiedviews.helpers.dataunit.map.MapHelpers;

/**
 * Static helper nutshell for {@link ResourceHelper}
 * <p>
 * The helper can be used in two ways:
 * <ul>
 * <li>static (and ineffective), quick and dirty way {@code ResourceHelpers.getResrouce(dataUnit, "symbolicName")}.
 * This does the job, but every call opens new connection to the underlying storage and then closes the connection adding a little overhead.</li>
 * <li>dynamic way,
 * <p><blockquote><pre>
 * //first create helper over dataunit
 * ResourceHelper helper = ResourceHelpers.create(dataUnit);
 * try {
 *   // use many times (helper holds its connections open)
 *   Resource resource = helper.getResource("symbolicName");
 *   helper.setResource("symbolicName", resource);
 * } finally {
 *   helper.close();
 * }
 * </pre></blockquote></p>
 * </ul>
 */
public class ResourceHelpers {
    private static final ResourceHelpers selfie = new ResourceHelpers();

    private ResourceHelpers() {

    }

    /**
     * Create read-only {@link ResourceHelper} using {@link MetadataDataUnit}, method {@link ResourceHelper#setResource(String, String, Map)} is unsupported in this instance.
     * @param dataUnit data unit to work with
     * @return instance of {@link ResourceHelper}, don't forget to close it after using it
     */
    public static ResourceHelper create(MetadataDataUnit dataUnit) {
        return selfie.new ResourceHelperImpl(dataUnit);
    }

    /**
     * Create read-write{@link ResourceHelper} using {@link WritableMetadataDataUnit}.
     * @param dataUnit data unit to work with
     * @return instance of {@link ResourceHelper}, don't forget to close it after using it
     */
    public static ResourceHelper create(WritableMetadataDataUnit dataUnit) {
        return selfie.new WritableMapHelperImpl(dataUnit);
    }

    /**
     * Just do the job, get Resource from given symbolicName.
     * Opens and closes connection to storage each time it is called.
     *
     * @param dataUnit unit to work with
     * @param symbolicName to which the map is binded
     * @return map
     * @throws DataUnitException
     */
    public static Resource getResource(MetadataDataUnit dataUnit, String symbolicName) throws DataUnitException {
        ResourceHelper helper = null;
        Resource result = null;
        try {
            helper = create(dataUnit);
            result = helper.getResource(symbolicName);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
        return result;
    }

    /**
     * Just set the Resource to given symbolicName.
     * @param dataUnit unit to work with
     * @param symbolicName to which the resource is binded
     * @param resource map to save
     * @throws DataUnitException
     */
    public static void setResource(WritableMetadataDataUnit dataUnit, String symbolicName, Resource resource) throws DataUnitException {
        ResourceHelper helper = null;
        try {
            helper = create(dataUnit);
            helper.setResource(symbolicName, resource);
        } finally {
            if (helper != null) {
                helper.close();
            }
        }
    }

    private class ResourceHelperImpl implements ResourceHelper {
        private MapHelper mapHelper;

        public ResourceHelperImpl(MetadataDataUnit dataUnit) {
            this.mapHelper = MapHelpers.create(dataUnit);
        }

        @Override
        public Resource getResource(String symbolicName) throws DataUnitException {
            Resource resource;
            try {
                resource = ResourceConverter.resourceFromMap(mapHelper.getMap(symbolicName, ResourceHelper.RESOURCE_STORAGE_MAP_NAME));
                resource.setExtras(ResourceConverter.extrasFromMap(mapHelper.getMap(symbolicName, ResourceHelper.EXTRAS_STORAGE_MAP_NAME)));
            } catch (ParseException ex) {
                throw new DataUnitException("Invalid format", ex);
            }
            return resource;
        }

        @Override
        public void setResource(String symbolicName, Resource resource) throws DataUnitException {
            throw new UnsupportedOperationException("Cannot set resource into read only dataunit");
        }

        @Override
        public void close() {
            mapHelper.close();
        }
    }

    private class WritableMapHelperImpl implements ResourceHelper {
        private MapHelper mapHelper;

        public WritableMapHelperImpl(WritableMetadataDataUnit dataUnit) {
            this.mapHelper = MapHelpers.create(dataUnit);
        }

        @Override
        public Resource getResource(String symbolicName) throws DataUnitException {
            Resource resource;
            try {
                resource = ResourceConverter.resourceFromMap(mapHelper.getMap(symbolicName, ResourceHelper.RESOURCE_STORAGE_MAP_NAME));
                resource.setExtras(ResourceConverter.extrasFromMap(mapHelper.getMap(symbolicName, ResourceHelper.EXTRAS_STORAGE_MAP_NAME)));
            } catch (ParseException ex) {
                throw new DataUnitException("Invalid format", ex);
            }
            return resource;
        }

        @Override
        public void setResource(String symbolicName, Resource resource) throws DataUnitException {
            mapHelper.putMap(symbolicName, ResourceHelper.RESOURCE_STORAGE_MAP_NAME, ResourceConverter.resourceToMap(resource));
            mapHelper.putMap(symbolicName, ResourceHelper.EXTRAS_STORAGE_MAP_NAME, ResourceConverter.extrasToMap(resource.getExtras()));
        }

        @Override
        public void close() {
            mapHelper.close();
        }
    }
}
