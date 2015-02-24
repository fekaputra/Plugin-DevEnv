package eu.unifiedviews.dpu.config.vaadin;

import eu.unifiedviews.dpu.config.DPUConfigException;

public interface InitializableConfigDialog {

    /**
     * Called once dialog context {@link ConfigDialogContext} is set. In this call dialog should
     * construct it's layout.
     *
     * @throws DPUConfigException
     */
    public void initialize() throws DPUConfigException;

}
