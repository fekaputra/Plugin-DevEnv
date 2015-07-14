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
package eu.unifiedviews.helpers.dpu.context;

import java.text.MessageFormat;

/**
 * Context for the DPU developer.
 *
 * @author Škoda Petr
 */
public class UserContext {

    /**
     * Wrapped master context.
     */
    private final Context<?> context;

    public UserContext(Context<?> context) {
        this.context = context;
    }

    public boolean isDialog() {
        return context.isDialog();
    }

    /**
     *
     * @param <T>
     * @param clazz
     * @return Class of given instance if such class is initialized in context of current DPU/dialog.
     */
    public <T> T getInstance(Class<T> clazz) {
        return (T)context.getInstance(clazz);
    }

    /**
     *
     * @return Instance of master context.
     */
    public Context getMasterContext() {
        return context;
    }

    /**
     * Get the resource bundle string stored under key, formatted using {@link MessageFormat}. Should be used
     * to translate all text in DPU.
     *
     * @param key resource bundle key
     * @param args parameters to formatting routine
     * @return formatted string, returns "!key!" when the value is not found in bundle
     */
    public String tr(final String key, final Object... args) {
        return context.getLocalization().getString(key, args);
    }

}
