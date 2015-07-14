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
package eu.unifiedviews.helpers.dpu.config;

/**
 * Can be used to transform configuration before it's loaded by DPU/add-on.
 *
 * @author Škoda Petr
 */
public interface ConfigTransformer {

    /**
     * Configure add-on before any other non {@link ConfigTransformerAddon} or DPU is configured.
     *
     * @param configManager
     * @throws ConfigException 
     */
    void configure(ConfigManager configManager) throws ConfigException;

    /**
     * Transform configuration on string level, before it's serialized as
     * an object.
     *
     * @param configName
     * @param config
     * @return
     * @throws cz.cuni.mff.xrg.uv.boost.dpu.config.ConfigException
     */
    String transformString(String configName, String config) throws ConfigException;

    /**
     * Can transform configuration object.
     *
     * @param <TYPE>
     * @param configName
     * @param config
     * @throws cz.cuni.mff.xrg.uv.boost.dpu.config.ConfigException
     */
    <TYPE> void transformObject(String configName, TYPE config) throws ConfigException;

}
