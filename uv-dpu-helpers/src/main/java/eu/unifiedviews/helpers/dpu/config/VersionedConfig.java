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
package eu.unifiedviews.helpers.dpu.config;

import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * Interface for versioned configuration.
 *
 * @author Škoda Petr
 * @param <CONFIG> Configuration to which this configuration can be converted (updated) to.
 */
public interface VersionedConfig<CONFIG> {

    /**
     *
     * @return Next version of configuration with "same" settings.
     * @throws DPUConfigException
     */
    CONFIG toNextVersion() throws DPUConfigException;

}
