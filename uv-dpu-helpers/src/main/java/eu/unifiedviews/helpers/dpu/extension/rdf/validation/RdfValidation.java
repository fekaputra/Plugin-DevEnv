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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
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
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Provide possibility to wrap user code. Wrapped code may be re-executed in case of failure and so this
 * add-on can be used a form of fault tolerant layer.
 * 
 * @author Škoda Petr
 */
public class RdfValidation implements Extension, Extension.Executable, Configurable<RdfValidation.Configuration_V1> {

    public static final String USED_CONFIG_NAME = "addon/rdfValidationWrap";

    public static final String ADDON_NAME = "dialog.dpu.tab.rdfvalidation";

    private static final Logger log = LoggerFactory.getLogger(RdfValidation.class);

    private Map<String, RDFDataUnit> outputDataUnits = new HashMap<>();

    private RDFDataUnit validatedDataUnit;

    @Override
    public void execute(ExecutionPoint execPoint) throws ExtensionException {
        if (execPoint == Extension.ExecutionPoint.POST_EXECUTE) {
            validate();

        }

    }

    private void validate() {
        log.info("Validation launched");

        if (config.askQuery != null && (!config.askQuery.isEmpty())) {

            //get connection to the validated output data unit
            RepositoryConnection connection = null;
            try {
                connection = validatedDataUnit.getConnection();

                //get entries (graphs to be validated)
                List<RDFDataUnit.Entry> entries;
                try {
                    entries = DataUnitUtils.getEntries(validatedDataUnit, RDFDataUnit.Entry.class);
                } catch (DataUnitException ex) {
                    log.error("Cannot obtain list of graphs withing the validated output data unit", ex);
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
                    java.util.logging.Logger.getLogger(RdfValidation.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }

                //report failure
                if (!ask.result) {
                    String msg = "RDF Validator: Outputted RDF data does not satisfy SPARQL ASK constraint";
                    String constrainedFailed = "Constraint failed: " + config.getAskQuery();
                    ContextUtils.sendMessage(context.asUserContext(), DPUContext.MessageType.ERROR, msg, constrainedFailed);
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
                    log.warn("Can't close connection!");
                }
            }

        }
        else {
            log.info("No validation defined");
        }

    }

    public static class Configuration_V1 {

        /**
         * If false no rdf validation is provided by this add-on.
         */
        //        private boolean enabled = true;

        private String askQuery = "ASK { ?s ?p ?o }";

        //        /**
        //         * Store names of exceptions that should be catch.
        //         */
        //        private List<String> exceptionNames = new LinkedList<>();

        //        /**
        //         * Number of retry before failure, use -1 as no limit.
        //         */
        //        private int maxRetryCount = -1;

        public Configuration_V1() {
        }

        //        public boolean isEnabled() {
        //            return enabled;
        //        }
        //
        //        public void setEnabled(boolean enabled) {
        //            this.enabled = enabled;
        //        }

        public String getAskQuery() {
            return askQuery;
        }

        public void setAskQuery(String askQuery) {
            this.askQuery = askQuery;
        }

    }

    public class VaadinDialog extends AbstractExtensionDialog<Configuration_V1> {

        //        private CheckBox checkEnabled;

        private TextArea txtAskQuery;

        public VaadinDialog() {
            super(configHistory);
        }

        @Override
        public void buildLayout() {
            final VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(true);
            layout.setMargin(true);

            //            checkEnabled = new CheckBox(RdfValidation.this.context.asUserContext().tr("dialog.dpu.rdfvalidation.enabled"));
            //            layout.addComponent(checkEnabled);

            txtAskQuery = new TextArea("SPARQL ASK query:");
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
            //            checkEnabled.setValue(conf.isEnabled());
            txtAskQuery.setValue(conf.getAskQuery());
        }

        @Override
        protected Configuration_V1 getConfiguration() throws DPUConfigException {
            final Configuration_V1 c = new Configuration_V1();
            //            c.setEnabled(checkEnabled.getValue());
            c.setAskQuery(txtAskQuery.getValue());
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
        if (context instanceof ExecContext) {
            this.dpuContext = ((ExecContext) context).getDpuContext();
            log.info("\tcontext set to: {}", this.dpuContext);

            final ExecContext execContext = (ExecContext) context;
            final AbstractDpu dpu = execContext.getDpu();

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

            //get output data unit which may be validated
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
                            //we got output RDF data unit !
                            validatedDataUnit = (RDFDataUnit) value;
                            return;
                        } else {
                            throw new DPUException("Field " + field.getName() + " does not have requested type "
                                    + "RDFDataUnit");
                        }
                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                        throw new DPUException("Can't get value for annotated field: " + field.getName(), ex);
                    }
                }
            }
            throw new DPUException("Missing configuration RDFDataUnit! "
                    + "Use RdfConfiguration.ContainsConfiguration annotation to mark it.");

        }

    }

    //    /**
    //     * Set configuration for test purpose.
    //     *    /**
    // 
    //     * @param config
    //     */
    //    void configure(Configuration_V1 config, DPUContext ctx) {
    //        this.config = config;
    //        this.dpuContext = ctx;
    //    }

}
