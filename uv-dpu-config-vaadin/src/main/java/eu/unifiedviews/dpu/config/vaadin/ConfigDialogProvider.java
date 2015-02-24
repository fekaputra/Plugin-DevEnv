package eu.unifiedviews.dpu.config.vaadin;


/**
 * Interface which provides graphical configuration dialog associated with the
 * given DPU
 * 
 * @author Petr Škoda
 * @param <C>
 */
public interface ConfigDialogProvider<C> {

    /**
     * @return Configuration dialog.
     */
    public AbstractConfigDialog<C> getConfigurationDialog();

}
