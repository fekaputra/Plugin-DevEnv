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

import eu.unifiedviews.helpers.dpu.context.UserContext;

/**
 * User version of dialog context {@link DialogContext}.
 * DPU developer has access to this context from the DPU's configuration dialog by calling {@code this.ctx}
 * 
 * @author Škoda Petr
 */
public class UserDialogContext extends UserContext {

    protected final DialogContext<?> dialogMasterContext;

    public UserDialogContext(DialogContext<?> dialogContext) {
        super(dialogContext);
        this.dialogMasterContext = dialogContext;
    }

    /**
     * @return True if dialog is used as a template.
     */
    public boolean isTemplate() {
        return this.dialogMasterContext.getDialogContext().isTemplate();
    }

    public DialogContext<?> getDialogMasterContext() {
        return dialogMasterContext;
    }

}
