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
package eu.unifiedviews.helpers.dpu.test.config;

import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigManager;
import eu.unifiedviews.helpers.dpu.config.MasterConfigObject;
import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXml;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFactory;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFailure;

/**
 * This class can be used to prepare configuration for DPU. Sample usage:
 * <pre>
 * {@code 
 * ZipperConfig_V1 config = new ZipperConfig_V1();
 * Zipper dpu = new Zipper();
 * // Set configuration to DPU.
 * dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());
 * }
 * </pre>
 *
 * @author Škoda Petr
 */
public class ConfigurationBuilder {

    private final SerializationXml serialization = SerializationXmlFactory.serializationXml();

    private final MasterConfigObject masterConfig = new MasterConfigObject();

    private final ConfigManager configManager = new ConfigManager(serialization);

    public ConfigurationBuilder() {
        try {
            configManager.setMasterConfig(masterConfig);
        } catch (ConfigException ex) {
            throw new RuntimeException("Can't set master configuration.", ex);
        }
    }

    /**
     *
     * @param configuration COnfiguration of DPU.
     * @return This instance for chaining.
     */
    public ConfigurationBuilder setDpuConfiguration(Object configuration) {
        configManager.set(configuration, AbstractDpu.DPU_CONFIG_NAME);
        return this;
    }

    @Override
    public String toString() {
        try {
            return serialization.convert(masterConfig);
        } catch (SerializationFailure | SerializationXmlFailure ex) {
            throw new RuntimeException("Can't serialize configuration.", ex);
        }
    }

}
