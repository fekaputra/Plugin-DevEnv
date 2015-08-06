package eu.unifiedviews.helpers.dpu.vaadin.dialog;

import eu.unifiedviews.helpers.dpu.extension.Extension;

/**
 * Interface for configurable extension.
 *
 * <strong>Configuration class must be static and with nonparametric constructor!</strong>
 * 
 * @author Škoda Petr
 * @param <CONFIG>
 */
public interface Configurable<CONFIG> extends Extension {

    /**
     * 
     * @return Class of used configuration class.
     */
    Class<CONFIG> getConfigClass();

    /**
     * 
     * @return Caption that is used for {@link AbstractExtensionDialog}, ie. name of respective Tab.
     */
    String getDialogCaption();

    /**
     * 
     * @return Respective configuration dialog.
     */
    AbstractExtensionDialog<CONFIG> getDialog();

}
