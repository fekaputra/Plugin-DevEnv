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
package eu.unifiedviews.helpers.dpu.vaadin.dialog;

import com.vaadin.ui.CustomComponent;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.ConfigManager;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.dpu.config.vaadin.ConfigDialogContext;

/**
 * Base dialog for extensions.
 * 
 * @author Škoda Petr
 * @param <CONFIG>
 */
public abstract class AbstractExtensionDialog<CONFIG> extends CustomComponent {

    /**
     * Dialog context.
     * TODO: Use some richer version like in AdvancedDPu.
     */
    protected ConfigDialogContext context;
    
    /**
     * History of configuration class, if set used instead of {@link #configClass}.
     */
    private final ConfigHistory<CONFIG> configHistory;

    public AbstractExtensionDialog(ConfigHistory<CONFIG> configHistory) {
        this.configHistory = configHistory;
    }

    /**
     *
     * @return Dialog context.
     */
    protected ConfigDialogContext getContext() {
        return context;
    }

    /**
     * Load configuration into dialog.
     *
     * @param configManager
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    public void loadConfig(ConfigManager configManager) throws DPUConfigException {
        CONFIG dpuConfig = configManager.get(getConfigClassName(), configHistory);
        // Config can be null, so we then use the default.
        if (dpuConfig == null) {
            // Create new class.
            dpuConfig = configManager.createNew(configHistory.getFinalClass());
        }
        setConfiguration(dpuConfig);
    }

    /**
     * Store configuration from dialog into given {@link ConfigManager}.
     *
     * @param configManager
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    public void storeConfig(ConfigManager configManager) throws DPUConfigException {
        configManager.set(getConfiguration(), getConfigClassName());
    }

    /**
     * Build and initialise layout.
     */
    public abstract void buildLayout();

    /**
     *
     * @return Key under which the configuration is saved.
     */
    protected abstract String getConfigClassName();

    /**
     * Set configuration for dialog.
     *
     * @param conf
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    protected abstract void setConfiguration(CONFIG conf) throws DPUConfigException;

    /**
     * Get dialog configuration.
     *
     * @return
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    protected abstract CONFIG getConfiguration() throws DPUConfigException;

}
