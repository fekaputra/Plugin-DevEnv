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
package eu.unifiedviews.dpu.config.vaadin;

import java.util.Locale;
import java.util.Map;

/**
 * Context for {@link AbstractConfigDialog}.
 *
 * @author Petr Škoda
 */
public interface ConfigDialogContext {

    /**
     * @return True if the dialog belongs to template false otherwise
     */
    boolean isTemplate();

    /**
     * Get the current locale
     */
    Locale getLocale();

    /**
     * Return the execution environment variables.
     *
     * For security reasons, it does not provides any configurations from config.properties!
     * 
     * @return
     */
    @Deprecated
    Map<String, String> getEnvironment();

    /**
     * Returns logged user external Id
     * 
     * @return Users' external Id
     */
    String getUserExternalId();

    /**
     * Returns logged user Id
     * 
     * @return Users' Id
     */
    Long getUserId();
}
