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
package eu.unifiedviews.dpu.config.vaadin;

import com.vaadin.ui.CustomComponent;

import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * Base abstract class for a configuration dialog.
 *
 * @author Petr Škoda
 * @param <C>
 */
public abstract class AbstractConfigDialog<C> extends CustomComponent {

    /**
     * Set context to the dialog. This method is called only once
     * before any other method.
     *
     * @param newContext
     */
    public abstract void setContext(ConfigDialogContext newContext);

    /**
     * Configure dialog with given serialized configuration.
     *
     * @param conf
     *            Serialized configuration object.
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    public abstract void setConfig(String conf) throws DPUConfigException;

    /**
     * Return current configuration from dialog in serialized form. If the
     * configuration is invalid then throws.
     *
     * @return Serialized configuration object.
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    public abstract String getConfig() throws DPUConfigException;

    /**
     * Return text that should be used as a DPU tool tip. The text should
     * contains information about configuration. If the configuration is not
     * valid, or this functionality is not supported can return null.
     *
     * @return Can be null.
     *         <b>This functionality is not currently supported by ODCS, but the
     *         returned value may be user in further.</b>
     */
    public abstract String getToolTip();

    /**
     * Return configuration summary that can be used as DPU description. The
     * summary should be short and as much informative as possible. Return null
     * in case of invalid configuration or it this functionality is not
     * supported. The returned string should be reasonably short.
     *
     * @return Can be null.
     */
    @Override
    public abstract String getDescription();

    /**
     * Compare last configuration and current dialog's configuration. If any
     * exception is thrown it is {@link DPUConfigException} indicating invalid configuration
     * entered by user.
     * The last configuration is updated in calls {@link #getConfig()} and {@link #setConfig(java.lang.String) }.
     *
     * @return True if configurations are valid and are different. False if configurations are valid and equal. Exception when new configuration is invalid.
     */
    public abstract boolean hasConfigChanged() throws DPUConfigException;

}
