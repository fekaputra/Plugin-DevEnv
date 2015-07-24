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
package eu.unifiedviews.dpu.config;

/**
 * Interface describes object that can be configured by using configuration string.
 */
public interface DPUConfigurable {

    /**
     * Use given configuration string to
     * configure this object. If the invalid configuration is given then {@link DPUConfigException} is thrown. For null the configuration
     * is left unchanged.
     *
     * @param config configuration.
     * @throws DPUConfigException to indicate invalid configuration
     */
    void configure(String config) throws DPUConfigException;

    /**
     * Return default configuration string.
     *
     * @return configuration.
     * @throws DPUConfigException to indicate that default configuration cannot be obtained
     */
    String getDefaultConfiguration() throws DPUConfigException;

}
