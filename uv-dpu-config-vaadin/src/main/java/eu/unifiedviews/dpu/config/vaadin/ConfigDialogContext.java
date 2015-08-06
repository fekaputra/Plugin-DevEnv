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
     * Return the execution environment variables
     * 
     * @return
     */
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
