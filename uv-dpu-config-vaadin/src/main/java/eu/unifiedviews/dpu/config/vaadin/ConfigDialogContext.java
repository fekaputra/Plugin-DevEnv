package eu.unifiedviews.dpu.config.vaadin;

import java.util.Locale;

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
}
