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
package eu.unifiedviews.helpers.dpu.vaadin.validator;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Locale;

import com.vaadin.data.Validator;

import eu.unifiedviews.helpers.dpu.localization.Messages;

/**
 * Validate given value to be full URL.
 * 
 * @author Škoda Petr
 */
public class UrlValidator implements Validator {

    private static final long serialVersionUID = 1L;

    /**
     * If true them empty value is considered to be valid URL.
     */
    private boolean emptyAllowed;

    private Messages localization;

    /**
     * Constructor
     */
    public UrlValidator() {
        this(true, Locale.US);
    }

    /**
     * Constructor
     * 
     * @param emptyAllowed
     *            If true then empty value is considered to be a valid URL.
     */
    public UrlValidator(boolean emptyAllowed) {
        this(emptyAllowed, Locale.US);
    }

    /**
     * Constructor
     * 
     * @param emptyAllowed
     *            If true then empty value is considered to be a valid URL.
     */
    public UrlValidator(boolean emptyAllowed, Locale locale) {
        this.emptyAllowed = emptyAllowed;
        this.localization = new Messages(locale, this.getClass().getClassLoader());
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            final String valueStr = (String) value;
            // null instance does not pass 'instanceof' test.
            if (emptyAllowed && valueStr.isEmpty()) {
                return;
            }

            try {
                new java.net.URL(valueStr);
            } catch (MalformedURLException ex) {
                throw new InvalidValueException(localization.getString("urlvalidator.invaliduri", valueStr));
            }

        } else if (value instanceof URI) {
            final String valueStr = ((URI) value).toString();
            // null instance does not pass 'instanceof' test.
            if (emptyAllowed && valueStr.isEmpty()) {
                return;
            }

            try {
                new java.net.URL(valueStr);
            } catch (MalformedURLException ex) {
                throw new InvalidValueException(localization.getString("urlvalidator.invaliduri", valueStr));
            }
        }
    }
}
