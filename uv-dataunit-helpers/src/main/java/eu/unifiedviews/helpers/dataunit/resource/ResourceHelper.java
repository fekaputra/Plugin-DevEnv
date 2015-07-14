/*******************************************************************************
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
 *******************************************************************************/
package eu.unifiedviews.helpers.dataunit.resource;

import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Helper providing easy way to attach Resource describing metadata to
 * particular {@link eu.unifiedviews.dataunit.MetadataDataUnit.Entry}.
 * <p>
 * Each entry can have exactly one Resource entity attached to it.
 * <p>
 * For example usage see {@link ResourceHelpers}
 * <p>
 * Each instance has to be closed after using it.
 * <p>
 */
public interface ResourceHelper extends AutoCloseable {

    /**
     * Value: {@value #RESOURCE_STORAGE_MAP_NAME}, value used to specify mapName property of underlying storage map.
     */
    public static final String RESOURCE_STORAGE_MAP_NAME = "http://unifiedviews.eu/ResourceHelper/resourceMap";

    /**
     * Value: {@value #EXTRAS_STORAGE_MAP_NAME}, value used to specify mapName property of underlying storage map.
     */
    public static final String EXTRAS_STORAGE_MAP_NAME = "http://unifiedviews.eu/ResourceHelper/resourceMapExtras";

    /**
     * Obtain Resource entity attached to metadata entry named symbolicName.
     *
     * @param symbolicName entry's symbolic name
     * @return resource metadata bean or null if no resource exists
     * @throws DataUnitException
     */
    Resource getResource(String symbolicName) throws DataUnitException;

    /**
     * Attach Resource entity to metadata entry named symbolicName. Any previously attached Resource entity will be replaced with new one.
     * Therefore, correct usage is to obtain the resource, make any changes and save it back.
     * @param symbolicName entry's symbolic name
     * @param resource entity representing resource
     * @throws DataUnitException
     */
    void setResource(String symbolicName, Resource resource) throws DataUnitException;

    @Override
    public void close();
}
