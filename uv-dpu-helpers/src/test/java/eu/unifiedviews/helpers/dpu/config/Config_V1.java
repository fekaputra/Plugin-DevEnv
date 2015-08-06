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

import eu.unifiedviews.helpers.dpu.config.VersionedConfig;

/**
 *
 * @author Škoda Petr
 */
public class Config_V1 implements VersionedConfig<Config_V2> {

    private int value = 2;

    public Config_V1() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public Config_V2 toNextVersion() {
        Config_V2 conf = new Config_V2();
        conf.setValue(Integer.toString(value));
        return conf;
    }
 
}
