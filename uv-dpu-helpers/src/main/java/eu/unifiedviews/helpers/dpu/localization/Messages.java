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
package eu.unifiedviews.helpers.dpu.localization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Škoda Petr
 */
public class Messages {

    /**
     * File name for DPU's localization.
     */
    private final String BUNDLE_NAME_DPU = "resources";

    /**
     * File name for library localization.
     */
    private final String BUNDLE_NAME_LIB = "resources_lib";

    /**
     * Used resource bundle.
     */
    private ResourceBundle resourceBundleDpu = null;

    private ResourceBundle resourceBundleLib = null;

    public Messages(Locale locale, ClassLoader classLoader) {
        resourceBundleDpu = ResourceBundle.getBundle(BUNDLE_NAME_DPU, locale, classLoader);
        resourceBundleLib = ResourceBundle.getBundle(BUNDLE_NAME_LIB, locale,
                Messages.class.getClassLoader());
    }

    /**
     * Get the resource bundle string stored under key, formatted using {@link MessageFormat}.
     *
     * @param key
     *            resource bundle key
     * @param args
     *            parameters to formatting routine
     * @return formatted string, returns "!key!" when the value is not found in bundle
     */
    public String getString(final String key, final Object... args) {
        if (resourceBundleDpu == null || resourceBundleLib == null) {
            throw new RuntimeException("Localization module has not been initialized!");
        }

        if (resourceBundleDpu.containsKey(key)) {
            return MessageFormat.format(resourceBundleDpu.getString(key), args);
        } else if (resourceBundleLib.containsKey(key)) {
            return MessageFormat.format(resourceBundleLib.getString(key), args);
        } else {
            // Fallback for missing values. We may
            // add option for debugging values?
            return MessageFormat.format(key, args);
        }
    }

}
