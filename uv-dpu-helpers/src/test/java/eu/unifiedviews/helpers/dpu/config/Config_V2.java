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

import eu.unifiedviews.helpers.dpu.config.VersionedConfig;

/**
 *
 * @author Škoda Petr
 */
public class Config_V2 implements VersionedConfig<Config_V3>  {

    private String value = "10";

    public Config_V2() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Config_V3 toNextVersion() {
        Config_V3 conf = new Config_V3();
        conf.setStr1(value);
        conf.setStr2("<a>" + value + "</a>");
        return conf;
    }

}
