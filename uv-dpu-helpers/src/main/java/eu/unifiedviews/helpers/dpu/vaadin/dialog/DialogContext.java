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
package eu.unifiedviews.helpers.dpu.vaadin.dialog;

import java.util.LinkedList;
import java.util.List;

import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.config.vaadin.ConfigDialogContext;

/**
 * A context of the DPU's dialog. Such context is available for the DPU during its execution.
 * DPU developer has access, during DPU execution, to {@link UserExecContext}
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
