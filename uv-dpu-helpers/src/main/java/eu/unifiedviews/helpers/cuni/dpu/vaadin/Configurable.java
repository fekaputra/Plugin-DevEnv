package eu.unifiedviews.helpers.cuni.dpu.vaadin;

import eu.unifiedviews.helpers.cuni.dpu.addon.Addon;
import eu.unifiedviews.helpers.cuni.dpu.exec.AutoInitializer;

/**
 * Interface for configurable {@link Addon}.
 *
 * <strong>Configuration class must be static and with nonparametric constructor!</strong>
 * 
 * @author Škoda Petr
 * @param <CONFIG>
 */
public interface Configurable<CONFIG> extends AutoInitializer.Initializable {

    /**
     * 
     * @return Class of used configuration class.
     */
    Class<CONFIG> getConfigClass();

    /**
     * 
     * @return Caption that is used for {@link AbstractAddonDialog}, ie. name of respective Tab.
     */
    String getDialogCaption();

    /**
     * 
     * @return Respective configuration dialog.
     */
    AbstractAddonDialog<CONFIG> getDialog();

}
