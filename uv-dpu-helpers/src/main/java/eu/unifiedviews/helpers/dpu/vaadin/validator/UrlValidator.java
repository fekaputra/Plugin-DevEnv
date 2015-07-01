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
    private boolean emptyAllowed = true;

    private Messages localization = new Messages(Locale.US, this
            .getClass().getClassLoader());

    /**
     * Constructor
     */
    public UrlValidator() {
    }

    /**
     * Constructor
     * 
     * @param emptyAllowed
     *            If true then empty value is considered to be a valid URL.
     */
    public UrlValidator(boolean emptyAllowed) {
        this(emptyAllowed, Locale.getDefault());
    }

    /**
     * Constructor
     * 
     * @param emptyAllowed
     *            If true then empty value is considered to be a valid URL.
     */
    public UrlValidator(boolean emptyAllowed, Locale locale) {
        this.emptyAllowed = emptyAllowed;
        this.localization = new Messages(locale, this.getClass()
                .getClassLoader());
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
                throw new InvalidValueException(localization.getString(
                        "urlvalidator.invaliduri", valueStr));
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
                throw new InvalidValueException(localization.getString(
                        "urlvalidator.invaliduri", valueStr));
            }
        }
    }
}
