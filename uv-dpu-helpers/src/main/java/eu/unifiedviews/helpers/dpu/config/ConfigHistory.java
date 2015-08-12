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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.config.DPUConfigException;


/**
 *
 * @author Škoda Petr
 * @param <CONFIG> Final configuration type we can convert to.
 */
public class ConfigHistory<CONFIG> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigHistory.class);

    /**
     * End of history chain.
     */
    private final ConfigHistoryEntry<?, CONFIG> endOfHistory;

    private final Class<CONFIG> finalClass;

    /**
     *
     * @param endOfHistory
     * @param finalClass   If null then class from endOfHistory is used.
     * @param finalClassAlias
     */
    ConfigHistory(ConfigHistoryEntry<?, CONFIG> endOfHistory, Class<CONFIG> finalClass) {
        this.endOfHistory = endOfHistory;
        if (finalClass == null) {
            // We know this as the only option we can get here is by ConfigHistoryEntry.addCurrent
            // which adds the final version of config ie. CONFIG.
            this.finalClass = (Class<CONFIG>) endOfHistory.configClass;
        } else {
            this.finalClass = finalClass;
        }
    }

    CONFIG parse(String configAsString, List<ConfigSerializer> serializers) throws ConfigException {
        // Be positive, start with the last class = current class.
        CONFIG config = deserialize(configAsString, finalClass, serializers);
        if(config != null) {
            return config;
        }
        // Let's enter the past ..
        if (endOfHistory == null) {
            LOG.error("Can't parse config for ({}), there is no history, value is: '{}' config:\n{}",
                    finalClass.getName(), config, configAsString);
            throw new ConfigException("Can't parse configuration for: " + finalClass.getName());
        }
        Object object = null;
        ConfigHistoryEntry<?, ?> current = endOfHistory;
        // Search for mathicng class in history.
        do {
            object = deserialize(configAsString, current.configClass, serializers);
            if (object == null) {
                // We should try alternatives here. As alternative can be converted
                // to this class and this call to another, we can use alternative as value for object directly.
                for (Class<VersionedConfig<?>> alternativceClass : current.alternatives) {
                    object = deserialize(configAsString, alternativceClass, serializers);
                    if (object != null) {
                        // We have it, we can stop search.
                        break ;
                    }
                }
            }
            // Go to the next history record. It doesnt matter if we move to next record event when
            // object != null, as we do not use current any further.
            current = current.previous;
        } while (current != null && object == null);
        // Check that we have something.
        if (object == null) {
            throw new ConfigException("Can't parse given object, no history record has been found.");
        }
        // We have the configuration object and we will update it as we can - call toNextVersion.
        // The compile time check secure that the last conversion return CONFIG object.
        while (!object.getClass().equals(finalClass)) {
            if (object instanceof VersionedConfig) {
                try {
                    object = ((VersionedConfig) object).toNextVersion();
                } catch (DPUConfigException ex) {
                    throw new ConfigException("Configuration update fail!", ex);
                }
            } else {
                // We can convert anymore.
                throw new ConfigException("Can't update given configuration to current.");
            }
        }
        return (CONFIG) object;
    }

    /**
     *
     * @return Final class in configuration class ie. the current configuration.
     */
    public Class<CONFIG> getFinalClass() {
        return finalClass;
    }

    /**
     * Call {@link #create(java.lang.Class, java.lang.String)} with alias equals to given class name.
     *
     * @param <T>
     * @param <S>
     * @param clazz
     * @return
     */
    public static <T, S extends VersionedConfig<T>> ConfigHistoryEntry<S, T> history(Class<S> clazz) {
        return new ConfigHistoryEntry<>(clazz, null);
    }

    /**
     * Create representation for configuration class without history.
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> ConfigHistory<T> noHistory(Class<T> clazz) {
        return new ConfigHistory<>(null, clazz);
    }

    /**
     *
     * @param <TYPE>
     * @param configAsString
     * @param clazz
     * @param serializers
     * @return Null if object can't be deserialise.
     */
    private <TYPE> TYPE deserialize(String configAsString, Class<TYPE> clazz,
            List<ConfigSerializer> serializers) {
        for (ConfigSerializer serializer : serializers) {
            if (serializer.canDeserialize(configAsString, clazz)) {
                final TYPE object = serializer.deserialize(configAsString, clazz);
                if (object != null) {

                    return object;
                }
            }
        }
        return null;
    }
}
