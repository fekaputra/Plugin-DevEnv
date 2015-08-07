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

import java.util.LinkedList;
import java.util.List;

/**
 * Used to chain history of configuration. Can be used in DPU's constructor, sample usage:
 * <pre>
 * {@code
 *  public MyDpu() {
 * super(ConfigHistory.create(MyDpuConfig_V1.class).addCurrent(MyDpuConfig_V2.class),
 * AddonInitializer.noAddons());
 * }
 * }
 * </pre>
 *
 * Alternatives can be also added into configuration history. Let's demonstrate meaning of alternative
 * on a example:
 * <pre>
 * C1 -> C2 -> C3 -> C4
 *        ^
 *        |
 *      A1, A2
 * </pre>
 * In this case we have main chain of configuration C?. But there is also configuration A1 and A2.
 * These configuration can be converted to C2. We may add them to configuration chain, but A1 and A2 may
 * have different properties and so we would lose configuration in case of linearization. 
 * 
 * In this case we can set A1 and A2 as alternatives to C2. In that case if configuration A1 is set 
 * then is converted to C2 and then to C3, C4 and we have current configuration.
 * 
 * This functionality can be also used if DPU should read multiple configuration that differ too much 
 * to be put into linear path, for example if DPU should replace more then one other DPU.
 *
 * @author Škoda Petr
 * @param <SOURCE> Our source configuration we need as input.
 * @param <DEST>   Configuration we can update to.
 * @see ConfigHistory
 */
public class ConfigHistoryEntry<SOURCE, DEST> {

    /**
     * Instance of class for configuration class.
     */
    final Class<SOURCE> configClass;

    /**
     * Pointer to previous entry in configuration history.
     */
    final ConfigHistoryEntry<?, SOURCE> previous;

    /**
     * Used to store list of alternatives that can convert into current class.
     */
    final List<Class<VersionedConfig<?>>> alternatives = new LinkedList<>();

    ConfigHistoryEntry(Class<SOURCE> configClass, ConfigHistoryEntry<?, SOURCE> previous) {
        this.configClass = configClass;
        this.previous = previous;
    }

    ConfigHistoryEntry(Class<SOURCE> configClass, ConfigHistoryEntry<?, SOURCE> previous,
            Class<VersionedConfig<SOURCE>>... alternatives) {
        this.configClass = configClass;
        this.previous = previous;
    }

    /**
     *
     * @param <TARGET> Target class we can be updated to.
     * @param <THIS>   Class type for this instance.
     * @param clazz    Configuration class.
     * @return
     */
    public <TARGET, THIS extends VersionedConfig<TARGET>> ConfigHistoryEntry<THIS, TARGET> add(
            Class<THIS> clazz) {
        return new ConfigHistoryEntry<>(clazz, (ConfigHistoryEntry<?, THIS>) this);
    }

    /**
     * Add alternative class to the configuration history. Alternative class can be converted to current
     * history class.
     *
     * @param <THIS>
     * @param alternative
     * @return
     */
    public <THIS extends VersionedConfig<SOURCE>> ConfigHistoryEntry<SOURCE, DEST>
            alternative(Class<THIS> alternative) {
        this.alternatives.add((Class<VersionedConfig<?>>) alternative);
        return this;
    }

    /**
     * End the configuration history with current class.
     *
     * @param clazz Current configuration class.
     * @return
     */
    public ConfigHistory<DEST> addCurrent(Class<DEST> clazz) {
        // We need class that we can convert to, that is DEST.
        return new ConfigHistory<>(this, clazz);
    }

}
