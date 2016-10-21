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
package eu.unifiedviews.helpers.dpu.extension.rdf.validation;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.extension.Extension;
import eu.unifiedviews.helpers.dpu.extension.ExtensionException;
import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlProblemException;
import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlUtils;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractExtensionDialog;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.Configurable;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * RdfValidation extension, see: https://grips.semantic-web.at/display/UDDOC/RDF+Validation
 */
public class RdfValidation implements Extension, Extension.Executable, Configurable<RdfValidation.Configuration_V1> {

    public static final String USED_CONFIG_NAME = "addon/rdfValidationWrap";

    public static final String ADDON_NAME = "dialog.dpu.tab.rdfvalidation";

    private static final Logger log = LoggerFactory.getLogger(RdfValidation.class);

    private List<String> outputDataUnitNames = new ArrayList<>();

    private RDFDataUnit validatedDataUnit;

    @Override
    public void execute(ExecutionPoint execPoint) throws ExtensionException {
        if (execPoint == Extension.ExecutionPoint.POST_EXECUTE) {
            validate();

        }

    }

    private void validate() {
        log.info("Validation launched");

        if (config.enabled) {

            if (config.askQuery != null && (!config.askQuery.isEmpty())) {

                //get the data unit to be validated
                if (config.validatedDataUnitName == null) {
                    ContextUtils.sendShortWarn(context.asUserContext(), "rdfvalidation.started.missingdataunit");
                    return;
                }

                //get connection to the validated output data unit
                RepositoryConnection connection = null;
                try {
                    connection = validatedDataUnit.getConnection();

                    //get entries (graphs to be validated)
                    List<RDFDataUnit.Entry> entries;
                    try {
                        entries = DataUnitUtils.getEntries(validatedDataUnit, RDFDataUnit.Entry.class);
                        if (entries.isEmpty()) {
                            ContextUtils.sendShortWarn(context.asUserContext(), "rdfvalidation.started.nodata", config.validatedDataUnitName);
                            return;
                        }
                        ContextUtils.sendShortInfo(context.asUserContext(), "rdfvalidation.started", config.validatedDataUnitName);

                    } catch (DataUnitException ex) {
                        ContextUtils.sendError(context.asUserContext(),"rdfvalidation.finished.error", "Cannot obtain list of graphs within the validated output data unit", ex);
                        return;
                    }

                    //execute the given sparql query
                    SparqlUtils.SparqlAskObject ask;
                    try {
                        ask = SparqlUtils.createAsk(config.getAskQuery(), entries);
                    } catch (SparqlProblemException ex) {
                        log.error("Problem with the SPARQL ASK query", ex);
                        return;
                    } catch (DataUnitException ex) {
                        log.error("Cannot execute the SPARQL ASK query", ex);
                        return;
                    }
                    try {
                        SparqlUtils.execute(connection, ask);
                    } catch (RepositoryException | MalformedQueryException | UpdateExecutionException | QueryEvaluationException ex) {
                        ContextUtils.sendError(context.asUserContext(),"rdfvalidation.finished.error", ex.getLocalizedMessage(), ex);
                        return;
                    }

                    //report failure
                    if (!ask.result) {
                        ContextUtils.sendMessage(context.asUserContext(), DPUContext.MessageType.ERROR, "rdfvalidation.finished.error", "rdfvalidation.constraintfailed", config.getAskQuery().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
                    }
                    else {
                        ContextUtils.sendShortInfo(context.asUserContext(), "rdfvalidation.finished.ok", config.validatedDataUnitName);
                    }

                } catch (DataUnitException ex) {
                    log.error("Cannot obtain connection to the validated output data unit", ex);
                    return;
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (RepositoryException ex) {
                        log.warn("Can't close connection", ex);
                    }
                }

            }
            else {
                log.error("NO SPARQL ASK query defined");
            }
        }
        else {
            log.info("Validation disabled");

        }

    }

    public static class Configuration_V1 {

        /**
         * If false no rdf validation is provided by this add-on.
         */
        private boolean enabled = false;

        private String askQuery = "ASK { ?s ?p ?o }";

        private String validatedDataUnitName;

        public String getValidatedDataUnitName() {
            return validatedDataUnitName;
        }

        public void setValidatedDataUnitName(String dataUnitValidated) {
            this.validatedDataUnitName = dataUnitValidated;
        }

        public Configuration_V1() {
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getAskQuery() {
            return askQuery;
        }

        public void setAskQuery(String askQuery) {
            this.askQuery = askQuery;
        }

    }

    public class VaadinDialog extends AbstractExtensionDialog<Configuration_V1> {

        private CheckBox checkEnabled;

        private TextArea txtAskQuery;

        private ComboBox cbDataUnitValidated;

        public VaadinDialog() {
            super(configHistory);
        }

        @Override
        public void buildLayout() {
            final VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(true);
            layout.setMargin(true);

            checkEnabled = new CheckBox(RdfValidation.this.context.asUserContext().tr("dialog.dpu.rdfvalidation.enabled"));
            checkEnabled.addListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    txtAskQuery.setEnabled(checkEnabled.getValue());
                    cbDataUnitValidated.setEnabled(checkEnabled.getValue());
                }
            });

            layout.addComponent(checkEnabled);

            cbDataUnitValidated = new ComboBox(RdfValidation.this.context.asUserContext().tr("dialog.dpu.rdfvalidation.dataunitsvalidated"));
            cbDataUnitValidated.removeAllItems();
            for (int i = 0; i < outputDataUnitNames.size(); i++) {
                String dataUnitNameAvailable = outputDataUnitNames.get(i);
                cbDataUnitValidated.addItem(dataUnitNameAvailable);
            }

            layout.addComponent(cbDataUnitValidated);

            txtAskQuery = new TextArea(RdfValidation.this.context.asUserContext().tr("dialog.dpu.rdfvalidation.sparqlaskquery"));
            txtAskQuery.setSizeFull();
            txtAskQuery.setNullRepresentation("");
            txtAskQuery.setNullSettingAllowed(true);
            layout.addComponent(txtAskQuery);
            layout.setExpandRatio(txtAskQuery, 1.0f);

            final Panel panel = new Panel();
            panel.setSizeFull();
            panel.setContent(layout);

            setCompositionRoot(panel);
        }

        @Override
        protected String getConfigClassName() {
            return USED_CONFIG_NAME;
        }

        @Override
        protected void setConfiguration(Configuration_V1 conf) throws DPUConfigException {
            checkEnabled.setValue(conf.isEnabled());
            txtAskQuery.setValue(conf.getAskQuery());
            txtAskQuery.setEnabled(checkEnabled.getValue());

            if (conf.getValidatedDataUnitName() != null && !conf.getValidatedDataUnitName().isEmpty())
                cbDataUnitValidated.setValue(conf.getValidatedDataUnitName());
            else if (cbDataUnitValidated.size() > 0) {
                //select the first one otherwise 
                cbDataUnitValidated.setValue(outputDataUnitNames.get(0));
            } else {
                //do not select any (no item!) 
            }
            cbDataUnitValidated.setEnabled(checkEnabled.getValue());
        }

        @Override
        protected Configuration_V1 getConfiguration() throws DPUConfigException {
            final Configuration_V1 c = new Configuration_V1();
            c.setEnabled(checkEnabled.getValue());
            c.setAskQuery(txtAskQuery.getValue());

            if (cbDataUnitValidated.getValue() != null) {
                c.setValidatedDataUnitName(cbDataUnitValidated.getValue().toString());
            }
            return c;
        }
    }

    private Configuration_V1 config;

    private final ConfigHistory<Configuration_V1> configHistory = ConfigHistory.noHistory(Configuration_V1.class);

    /**
     * Context used by DPU, used to find out cancellation.
     */
    private DPUContext dpuContext;

    /**
     * Root context.
     */
    private Context context;

    @Override
    public Class<Configuration_V1> getConfigClass() {
        return Configuration_V1.class;
    }

    @Override
    public String getDialogCaption() {
        return ADDON_NAME;
    }

    @Override
    public AbstractExtensionDialog<Configuration_V1> getDialog() {
        return new VaadinDialog();
    }

    @Override
    public void preInit(String param) throws DPUException {
        // No-op/
    }

    @Override
    public void afterInit(Context context) throws DPUException {
        this.context = context;

        // Load configuration.
        try {
            this.config = context.getConfigManager().get(USED_CONFIG_NAME, configHistory);
        } catch (ConfigException ex) {
            log.warn("Can't load configuration.", ex);
            ContextUtils.sendInfo(context.asUserContext(), "Addon failed to load configuration",
                    "Failed to load configuration for: {0} default configuration is used.", ADDON_NAME);
            this.config = new Configuration_V1();
        }
        if (this.config == null) {
            ContextUtils.sendInfo(context.asUserContext(), "Addon failed to load configuration",
                    "Failed to load configuration for: {0} default configuration is used.", ADDON_NAME);
            this.config = new Configuration_V1();
        }

        if (context instanceof ExecContext) {
            //we are executing the extension

            //continue only when the extension is enabled
            if (config.enabled) {

                //get the particular instance of the selected output data unit to be validated (this is available only during execution)
                this.dpuContext = ((ExecContext) context).getDpuContext();
                log.info("\tcontext set to: {}", this.dpuContext);

                final ExecContext execContext = (ExecContext) context;
                final AbstractDpu dpu = execContext.getDpu();

                validatedDataUnit = null;

                //get output data unit which should be validated
                for (Field field : dpu.getClass().getFields()) {
                    if (field.getAnnotation(DataUnit.AsOutput.class) != null) {
                        // We got annotated field which is output data unit. Get it's value.

                        try {
                            final Object value = field.get(dpu);
                            if (value == null) {
                                // We are in scope of dialog, or input is not set.
                                return;
                            }
                            if (value instanceof RDFDataUnit) {
                                //we got output RDF data unit - add it to the list !
                                log.info("Data unit to be validated: {}", config.validatedDataUnitName);
                                log.info("Examined data unit name: {}", field.getAnnotation(DataUnit.AsOutput.class).name());
                                if (field.getAnnotation(DataUnit.AsOutput.class).name().equals(config.validatedDataUnitName)) {
                                    validatedDataUnit = (RDFDataUnit) value;
                                    return;
                                }
                            }

                        } catch (IllegalAccessException | IllegalArgumentException ex) {
                            throw new DPUException("Can't get value for annotated field: " + field.getName(), ex);
                        }
                    }
                }
                //if we get here, something strange happened
                log.error("Something strange happens, RDF validation was disabled. Data unit with the given name {} cannot be fetched. This is strange as the name was selected from the list of available data units! ", config.validatedDataUnitName);
                config.enabled = false;
            }

        }
        else {
            //it is dialog context
            //get output data unit names to be depicted in the dialog
            outputDataUnitNames = new ArrayList<>();
            for (Field field : context.asUserContext().getMasterContext().getDpuClass().getFields()) {
                if (field.getAnnotation(DataUnit.AsOutput.class) != null) {
                    // We got annotated field which is output data unit. G

                    if (field.getType().getName().contains("RDFDataUnit")) {

                        //we got only output data units, which are RDF data units
                        String outputDataUnitName = field.getAnnotation(DataUnit.AsOutput.class).name();
                        outputDataUnitNames.add(outputDataUnitName);
                    }

                }

            }
        }

    }
}
