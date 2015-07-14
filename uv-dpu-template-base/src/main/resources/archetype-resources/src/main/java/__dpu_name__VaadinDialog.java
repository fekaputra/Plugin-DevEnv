/*******************************************************************************
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
 *******************************************************************************/
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for ${dpu_name}.
 *
 * @author ${author}
 */
public class ${dpu_name}VaadinDialog extends AbstractDialog<${dpu_name}Config_V1> {

    public ${dpu_name}VaadinDialog() {
        super(${dpu_name}.class);
    }

    @Override
    public void setConfiguration(${dpu_name}Config_V1 c) throws DPUConfigException {

    }

    @Override
    public ${dpu_name}Config_V1 getConfiguration() throws DPUConfigException {
        final ${dpu_name}Config_V1 c = new ${dpu_name}Config_V1();

        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        mainLayout.addComponent(new Label(ctx.tr("${dpu_name}.dialog.label")));

        setCompositionRoot(mainLayout);
    }
}
