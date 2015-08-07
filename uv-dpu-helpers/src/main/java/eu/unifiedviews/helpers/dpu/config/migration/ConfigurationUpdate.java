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
package eu.unifiedviews.helpers.dpu.config.migration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigManager;
import eu.unifiedviews.helpers.dpu.config.ConfigTransformer;
import eu.unifiedviews.helpers.dpu.config.MasterConfigObject;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.extension.Extension;

/**
 * Provide DPU with possibility of opening configuration from "core" DPUs.
 *
 * As initialization parameter accept name of configuration class that will be used if the configuration
 * class name is missing in stored configuration. This name must respect xStream naming conventions,
 * so it should be same as the one that would be presented in configuration.
 *
 * @author Škoda Petr
 */
public class ConfigurationUpdate implements ConfigTransformer, Extension {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationUpdate.class);

    private static final String CLASS_GROUP = "class";

    private static final String CONFIGURATION_GROUP = "config";

    /**
     * Contains pattern used to match old configurations.
     */
    private static final Pattern pattern = Pattern.compile("^<object-stream>\\s*<ConfigurationVersion>.*"
            + "<className>(?<" + CLASS_GROUP + ">[^<]*)</className>\\s*</ConfigurationVersion>\\s*"
            + "<Configuration>(?<" + CONFIGURATION_GROUP + ">.+)</Configuration>\\s*</object-stream>\\s*$"
            , Pattern.DOTALL);

    /**
     * Name of class used if configuration does not contains configuration name.
     */
    protected String defaultClassName = null;

    @Override
    public void configure(ConfigManager configManager) throws ConfigException {
        // No-op here.
    }

    @Override
    public String transformString(String configName, String config) throws ConfigException {
        if (!configName.equals(MasterConfigObject.CONFIG_NAME) || config == null) {
            return config;
        }
        // Fast initial check.
        if (!config.contains("<ConfigurationVersion>")) {
            return config;
        }

        final Matcher matcher = pattern.matcher(config);
        if (!matcher.matches()) {
            LOG.warn("Configuration does not match the pattern, but pass first test!");
            return config;
        }
        String className = matcher.group(CLASS_GROUP).replaceAll("_", "__");
        if (className.isEmpty()) {
            LOG.info("Missing class name, user given is used insted!");
            className = defaultClassName;
        }
        
        String originalConfiguration = matcher.group(CONFIGURATION_GROUP);
        originalConfiguration = originalConfiguration.
                replaceAll("&", "&amp;").
                replaceAll("<", "&lt;").
                replaceAll(">", "&gt;");

        // Create new configuration.
        final StringBuilder newConfiguration = new StringBuilder();
        newConfiguration.append(""
                + "<object-stream>\n"
                + "  <MasterConfigObject>\n"
                + "    <configurations>\n"
                + "      <entry>\n"
                + "        <string>dpu_config</string>\n"
                + "        <string>&lt;object-stream&gt;"
                + "&lt;");
        newConfiguration.append(className);
        newConfiguration.append("&gt;");

        newConfiguration.append(originalConfiguration);
        
        newConfiguration.append("&lt;/");
        newConfiguration.append(className);
        newConfiguration.append("&gt;"
                + "&lt;/object-stream&gt;</string>\n"
                + "      </entry>\n"
                + "    </configurations>\n"
                + "  </MasterConfigObject>\n"
                + "</object-stream>");

        return newConfiguration.toString();
    }

    @Override
    public <TYPE> void transformObject(String configName, TYPE config) throws ConfigException {
        // No-op here.
    }

    @Override
    public void preInit(String param) {
        this.defaultClassName = param;
    }

    @Override
    public void afterInit(Context context) {
        // No-op here.
    }

}
