package eu.unifiedviews.helpers.dpu.vaadin.dialog;

import java.util.LinkedList;
import java.util.List;

import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.config.vaadin.ConfigDialogContext;

/**
 *
 * @author Škoda Petr
 */
public class DialogContext<CONFIG> extends Context<CONFIG> {

    /**
     * Owner dialog.
     */
    protected final AbstractDialog dialog;

    /**
     * Core context.
     */
    protected final ConfigDialogContext dialogContext;

    /**
     * List of add-on dialogs.
     */
    protected final List<AbstractExtensionDialog> addonDialogs = new LinkedList<>();

    public DialogContext(AbstractDialog dialog, ConfigDialogContext dialogContext,
            Class<AbstractDpu<CONFIG>> dpuClass, AbstractDpu<CONFIG> dpuInstance)
            throws DPUException {
        super(dpuClass, dpuInstance);
        this.dialog = dialog;
        this.dialogContext = dialogContext;
        // And call init as we hawe all we need. Configuration is going to be set later.
        init(null, dialogContext.getLocale(), dpuClass.getClassLoader());
    }

    public AbstractDialog getDialog() {
        return dialog;
    }

    public ConfigDialogContext getDialogContext() {
        return dialogContext;
    }

    public List<AbstractExtensionDialog> getAddonDialogs() {
        return addonDialogs;
    }

}
