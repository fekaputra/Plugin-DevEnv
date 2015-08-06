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
package eu.unifiedviews.helpers.dpu.vaadin.tabs;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.UserDialogContext;

/**
 * Create a Tab in configuration dialog. The tab provide user with possibility to copy and paste
 * DPUs configuration as a string.
 *
 * This offer a simple way how to copy DPU's configuration from one instance to another but also
 * provide user will full access to the configuration - this is a high security risk, use with caution!
 *
 * @author Škoda Petr
 */
public class ConfigCopyPaste {

    protected ConfigCopyPaste() {
        
    }

    /**
     *
     * @param ctx
     * @return Tab that should be added to the dialog.
     */
    public static CustomComponent create(UserDialogContext ctx) {
        final ConfigCopyPasteTab tab = new ConfigCopyPasteTab(ctx);
        tab.buildLayout();
        return tab;
    }

    protected static class ConfigCopyPasteTab extends CustomComponent {

        private TextArea txtConfiguration;

        private final UserDialogContext ctx;

        private final AbstractDialog dialog;

        public ConfigCopyPasteTab(UserDialogContext ctx) {
            this.ctx = ctx;
            this.dialog = ctx.getDialogMasterContext().getDialog();
            // Build layout.            
        }

        public void buildLayout() {
            setSizeFull();

            final HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setSpacing(true);
            
            final Button btnImport = new Button("Import configuration");
            buttonLayout.addComponent(btnImport);
            buttonLayout.setComponentAlignment(btnImport, Alignment.MIDDLE_LEFT);

            final Button btnExport = new Button("Export configuration");
            buttonLayout.addComponent(btnExport);
            buttonLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_RIGHT);

            final VerticalLayout mainLayout = new VerticalLayout();
            final Label label = new Label("Can be used to import export DPU's configuration as string. Use with caution! </br>"
                    + "In case of import configuration is not saved, only loaded into dialog.</br>"
                    + "Exports corrent configuration from dialog, current configuration msut be valid.", ContentMode.HTML);
            mainLayout.addComponent(label);

            mainLayout.setSizeFull();
            mainLayout.setMargin(true);
            mainLayout.setSpacing(true);
            mainLayout.addComponent(buttonLayout);

            txtConfiguration = new TextArea("Configuration");
            txtConfiguration.setSizeFull();

            mainLayout.addComponent(txtConfiguration);
            mainLayout.setExpandRatio(txtConfiguration, 1.0f);

            setCompositionRoot(mainLayout);

            // Bind functionality to the buttons.
            btnImport.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {

                        dialog.setConfig(txtConfiguration.getValue());
                        Notification.show("Configuration has been imported.", Notification.Type.HUMANIZED_MESSAGE);
                    } catch (DPUConfigException ex) {
                        Notification.show("Import failed", ex.fillInStackTrace().toString(), Notification.Type.ERROR_MESSAGE);
                    }
                }
            });

            btnExport.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        final String configuration = dialog.getConfig();
                        txtConfiguration.setValue(configuration);
                        Notification.show("Configuration has been exported.", Notification.Type.HUMANIZED_MESSAGE);
                    } catch (DPUConfigException ex) {
                        txtConfiguration.setValue("");
                        Notification.show("Export failed", ex.fillInStackTrace().toString(), Notification.Type.ERROR_MESSAGE);
                    }
                }
            });
        }
    }

}
