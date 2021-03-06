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
package eu.unifiedviews.helpers.dpu.context;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.ConfigManager;
import eu.unifiedviews.helpers.dpu.config.ConfigTransformer;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.Extension;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.localization.Messages;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXml;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFactory;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.Configurable;

/**
 * Base class for a context.
 * There are two types of context:
 * - {@link ExecContext}, which is a context of the executed DPU
 * - {@link DialogContext}, which is a context of the DPU's dialog
 * 
 * @author Škoda Petr
 * @param <CONFIG>
 *            Last configuration class.
 * @param <ONTOLOGY>
 *            Ontology class.
 */
public class Context<CONFIG> implements ExtensionInitializer.FieldSetListener {

    /**
     * Respective DPU class.
     */
    private final Class<AbstractDpu<CONFIG>> dpuClass;

    /**
     * True if context is used for dialog.
     */
    private final boolean dialog;

    /**
     * List of used extensions.
     */
    private final List<Extension> extensions = new LinkedList<>();

    /**
     * List of used configuration transformers.
     */
    private final List<ConfigTransformer> configTransformers = new LinkedList<>();

    /**
     * List of configurable ad-dons. May contains same classes as {@link #extensions} and {@link #configTransformers}.
     */
    private final List<Configurable> configurable = new LinkedList<>();

    /**
     * History of configuration class, if set used instead of {@link #configClass}.
     */
    private ConfigHistory<CONFIG> configHistory = null;

    /**
     * Configuration manager.
     */
    private ConfigManager configManager = null;

    /**
     * Serialisation service for root configuration.
     */
    private final SerializationXml serializationXml;

    /**
     * Class used to initialize given DPU.
     */
    private final ExtensionInitializer initializer;

    /**
     * Module for localization support.
     */
    private Messages localization;

    /**
     * Set base fields and create
     * 
     * @param <T>
     * @param dpuClass
     * @param dpuInstance
     *            Can be null, in such case temporary instance is created.
     * @param ontology
     * @throws eu.unifiedviews.dpu.DPUException
     */
    public Context(Class<AbstractDpu<CONFIG>> dpuClass, AbstractDpu<CONFIG> dpuInstance) {
        // Prepare DPU instance, just to get some classes.
        try {
            if (dpuInstance == null) {
                dpuInstance = dpuClass.newInstance();
            }
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException("Can't create DPU instance for purpose of scanning.", ex);
        }
        // Set properties.
        this.dpuClass = dpuClass;
        this.dialog = true;
        this.configHistory = dpuInstance.getConfigHistoryHolder();
        this.serializationXml = SerializationXmlFactory.serializationXml();
        // Prepare initializer.
        this.initializer = new ExtensionInitializer(dpuInstance);
        // Prepare configuration manager - without addons, configuration transformers etc ..
        this.configManager = new ConfigManager(this.serializationXml);
    }

    /**
     * Initialize fields and set given configuration.
     * 
     * @param configAsString
     *            If null no configuration is set. Used by dialog as there the configuration is set
     *            later.
     * @throws DPUException
     */
    protected final void init(String configAsString, Locale locale, ClassLoader classLoader)
            throws DPUException {
        // Initialize localization.
        this.localization = new Messages(locale, classLoader);
        // Init DPU use callback to get info about Addon, ConfigTransformer, ConfigurableAddon.
        initializer.addCallback(this);
        initializer.preInit();
        // Add initialized config transformers.
        this.configManager.addTransformers(configTransformers);
        try {
            if (configAsString != null) {
                this.configManager.setMasterConfig(configAsString);
            }
        } catch (ConfigException ex) {
            throw new DPUException("Can't configure DPU.", ex);
        }
        // Finish addon initialization.
        initializer.afterInit(this);
    }

    /**
     * Callback to gather ad-dons.
     * 
     * @param field
     * @param value
     */
    @Override
    public void onField(Field field, Object value) {
        if (value instanceof Extension) {
            extensions.add((Extension) value);
        }
        if (value instanceof ConfigTransformer) {
            configTransformers.add((ConfigTransformer) value);
        }
        if (value instanceof Configurable) {
            configurable.add((Configurable) value);
        }
    }

    /**
     * Can be used before complete context initialization.
     * 
     * @return
     */
    public Class<AbstractDpu<CONFIG>> getDpuClass() {
        return dpuClass;
    }

    /**
     * Can be used before complete context initialization.
     * 
     * @return
     */
    public boolean isDialog() {
        return dialog;
    }

    /**
     * Can be used before complete context initialization.
     * 
     * @return
     */
    public SerializationXml getSerializationXml() {
        return serializationXml;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public List<ConfigTransformer> getConfigTransformers() {
        return configTransformers;
    }

    public List<Configurable> getConfigurableAddons() {
        return configurable;
    }

    public ConfigHistory<CONFIG> getConfigHistory() {
        return configHistory;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Return class with given type stored in context.
     * 
     * @param <T>
     * @param clazz
     * @return Null if no {@link Addon} of given type exists.
     */
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        for (Extension item : extensions) {
            if (item.getClass() == clazz) {
                return (T) item;
            }
        }
        for (ConfigTransformer item : configTransformers) {
            if (item.getClass() == clazz) {
                return (T) item;
            }
        }

        for (Configurable item : configurable) {
            if (item.getClass() == clazz) {
                return (T) item;
            }
        }
        return null;
    }

    public Messages getLocalization() {
        return localization;
    }

    /**
     * @return Return new instance of {@link UserContext} that wrap this context.
     */
    public UserContext asUserContext() {
        return new UserContext(this);
    }

}
