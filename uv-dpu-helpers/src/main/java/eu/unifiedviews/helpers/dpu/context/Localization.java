package eu.unifiedviews.helpers.dpu.context;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Škoda Petr
 */
public class Localization {

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

    /**
     * Set current locale.
     *
     * @param locale
     */
    public void setLocale(Locale locale, ClassLoader classLoader) {
        resourceBundleDpu = ResourceBundle.getBundle(BUNDLE_NAME_DPU, locale, classLoader);
        resourceBundleLib = ResourceBundle.getBundle(BUNDLE_NAME_LIB, locale,
            Localization.class.getClassLoader());
    }

    /**
     * Get the resource bundle string stored under key, formatted using {@link MessageFormat}.
     *
     * @param key resource bundle key
     * @param args parameters to formatting routine
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
