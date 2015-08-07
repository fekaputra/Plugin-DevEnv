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

import java.util.HashMap;
import java.util.Map;

/**
 * Used to store multiple string values (configurations) under string keys.
 *
 * @author Škoda Petr
 */
public class MasterConfigObject {

    /**
     * Name of master configuration. Used as a master configuration name for
     * {@link cz.cuni.mff.xrg.uv.boost.dpu.addon.ConfigTransformerAddon}.
     */
    public static final String CONFIG_NAME = "master_config_object";

    /**
     * Type name used during serialisation.
     */
    public static final String TYPE_NAME = "MasterConfigObject";

    /**
     * Storage for configurations.
     */
    private Map<String, String> configurations = new HashMap<>();
    
    public Map<String, String> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Map<String, String> configurations) {
        this.configurations = configurations;
    }

}
