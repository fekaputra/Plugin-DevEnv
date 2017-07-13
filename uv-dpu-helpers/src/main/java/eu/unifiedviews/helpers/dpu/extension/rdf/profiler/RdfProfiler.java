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
package eu.unifiedviews.helpers.dpu.extension.rdf.profiler;

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
import eu.unifiedviews.helpers.dataunit.rdf.RDFHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.extension.Extension;
import eu.unifiedviews.helpers.dpu.extension.ExtensionException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractExtensionDialog;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.Configurable;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * RdfValidation extension, see: https://grips.semantic-web.at/display/UDDOC/RDF+Validation
 */
public class RdfProfiler implements Extension, Extension.Executable, Configurable<RdfProfiler.Configuration_V1> {

    public static final String USED_CONFIG_NAME = "addon/rdfProfiler";

    public static final String ADDON_DIALOG_NAME = "dialog.dpu.tab.rdfprofiler";

    private static final Logger log = LoggerFactory.getLogger(RdfProfiler.class);

    private List<String> outputDataUnitNames = new ArrayList<>();

    private RDFDataUnit profiledDataUnit;

    @Override
    public void execute(ExecutionPoint execPoint) throws ExtensionException {
        if (execPoint == ExecutionPoint.POST_EXECUTE) {
            profile();
        }
    }

    private void profile() {
        if (config.enabled) {
            log.info("RDF Profiler extension enabled");



                //get the data unit to be profiled
                if (config.profiledDataUnitName == null) {
                    ContextUtils.sendShortWarn(context.asUserContext(), "rdfprofiler.started.missingdataunit");
                    return;
                }

                //get connection to the profiled output data unit
                RepositoryConnection connection = null;
                try {
                    connection = profiledDataUnit.getConnection();

                    //get entries (graphs to be profiled)
                    List<RDFDataUnit.Entry> entries;
                    try {
                        entries = DataUnitUtils.getEntries(profiledDataUnit, RDFDataUnit.Entry.class);
                        if (entries.isEmpty()) {
                            ContextUtils.sendShortWarn(context.asUserContext(), "rdfprofiler.started.nodata", config.profiledDataUnitName);
                            return;
                        }
                        ContextUtils.sendShortInfo(context.asUserContext(), "rdfprofiler.started", config.profiledDataUnitName);

                    } catch (DataUnitException ex) {
                        ContextUtils.sendError(context.asUserContext(),"rdfprofiler.finished.internalerror", ex, ex.getLocalizedMessage());
                        return;
                    }
                    //get dataset - set of graphs the queries should operate on
                    Dataset dataset = RDFHelper.getDatasetWithDefaultGraphs(profiledDataUnit);

//                    for (IRI graph : dataset.getDefaultGraphs()) {
//                        log.info("Dataset URI to be processed {}", graph.toString());
//                    }

                    StringBuilder report = new StringBuilder();
//
//                    //execute the given sparql query
//                    String allQueryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o }";
//
////                    TupleQuery queryX = connection.prepareTupleQuery(allQueryString);
//                    queryX.setDataset(dataset);
//
//                    // A QueryResult is also an AutoCloseable resource, so make sure it gets
//                    // closed when done.
//                    try (TupleQueryResult resultX = queryX.evaluate()) {
//                        // we just iterate over all solutions in the result...
//                        while (resultX.hasNext()) {
//                            BindingSet solution = resultX.next();
//                            // ... and print out the value of the variable bindings
//                            // show it in the dialog
//                           log.info("Triple {},{},{}", solution.getValue("s").stringValue(), solution.getValue("p").stringValue(), solution.getValue("o").stringValue() );
//                        }
//                    }


                    //execute the given sparql query
                    String countQueryString = "SELECT (COUNT(*) as ?count) WHERE { ?s ?p ?o }";
                    int  numOfTriples = 0;
                    TupleQuery query = connection.prepareTupleQuery(countQueryString);
                    query.setDataset(dataset);
                    // A QueryResult is also an AutoCloseable resource, so make sure it gets
                    // closed when done.
                    try (TupleQueryResult result = query.evaluate()) {
                        // we just iterate over all solutions in the result...
                        while (result.hasNext()) {
                            BindingSet solution = result.next();
                            // ... and print out the value of the variable bindings
                            // show it in the dialog
                            numOfTriples = Integer.valueOf(solution.getValue("count").stringValue());
                            break;
                           //log.info("Count: {}", numOfTriples);
                        }
                    }
                    report.append("<h3>Number of triples: ");
                    report.append(numOfTriples);
                    report.append("</h3>");


                    //info about classes:
                    //get top 100 mostly used classes, including number of instances
                    String classesQueryString = "select distinct ?class (COUNT(*) as ?count) where {[] a ?class} Group By ?class Order by DESC(?count) LIMIT 100";
                    query = connection.prepareTupleQuery(classesQueryString);
                    query.setDataset(dataset);
                    // A QueryResult is also an AutoCloseable resource, so make sure it gets
                    // closed when done.

                    report.append("<h3>Top 100 classes</h3>");
                    report.append("<table style='border-collapse:collapse;width:100%;'><tr><th>Class</th><th>Number of instances</th></tr>");
                    try (TupleQueryResult result = query.evaluate()) {
                        // we just iterate over all solutions in the result...
                        while (result.hasNext()) {
                            BindingSet solution = result.next();
                            // ... and print out the value of the variable bindings
                            // show it in the dialog
                            report.append("<tr><td>");
                            report.append(solution.getValue("class").stringValue());
                            report.append("</td><td>");
                            report.append(solution.getValue("count").stringValue());
                            report.append("</td></tr>");

                        }
                    }
                    report.append("</table>");

                    //info about properties
                    String predicatesQueryString = "select distinct ?predicate (COUNT(*) as ?count) where {[] ?predicate []} Group By ?predicate Order by DESC(?count) LIMIT 100";
                    query = connection.prepareTupleQuery(predicatesQueryString);
                    query.setDataset(dataset);
                    // A QueryResult is also an AutoCloseable resource, so make sure it gets
                    // closed when done.

                    report.append("<h3>Top 100 predicates</h3>");
                    report.append("<table style='border-collapse:collapse;width:100%;'><tr><th>Predicate</th><th>Number of occurrences</th></tr>");
                    try (TupleQueryResult result = query.evaluate()) {
                        // we just iterate over all solutions in the result...
                        while (result.hasNext()) {
                            BindingSet solution = result.next();
                            // ... and print out the value of the variable bindings
                            // show it in the dialog
                            report.append("<tr><td>");
                            report.append(solution.getValue("predicate").stringValue());
                            report.append("</td><td>");
                            report.append(solution.getValue("count").stringValue());
                            report.append("</td></tr>");

                        }
                    }
                    report.append("</table>");

                    ContextUtils.sendInfo(context.asUserContext(), "rdfprofiler.finished.ok", report.toString(), config.profiledDataUnitName, numOfTriples);

                } catch (DataUnitException ex) {
                    log.error("Cannot obtain connection to the profiled output data unit", ex);
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
            log.info("RDF Validation extension disabled");

        }

    }

    public static class Configuration_V1 {

        /**
         * If false no rdf validation is provided by this add-on.
         */
        private boolean enabled = false;

        private String profiledDataUnitName;

        public String getProfiledDataUnitName() {
            return profiledDataUnitName;
        }

        public void setProfiledDataUnitName(String dataUnitProfiled) {
            this.profiledDataUnitName = dataUnitProfiled;
        }

        public Configuration_V1() {
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }

    public class VaadinDialog extends AbstractExtensionDialog<Configuration_V1> {

        private CheckBox checkEnabled;

        private ComboBox cbDataUnitProfiled;

        private Label lInfo;

//        private TextArea txtAskQuery;

        public VaadinDialog() {
            super(configHistory);
        }

        @Override
        public void buildLayout() {
            final VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(true);
            layout.setMargin(true);

            checkEnabled = new CheckBox(RdfProfiler.this.context.asUserContext().tr("dialog.dpu.rdfprofiler.enabled"));
            checkEnabled.addListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
//                    txtAskQuery.setEnabled(checkEnabled.getValue());
                    cbDataUnitProfiled.setEnabled(checkEnabled.getValue());
                }
            });

            layout.addComponent(checkEnabled);

            cbDataUnitProfiled = new ComboBox(RdfProfiler.this.context.asUserContext().tr("dialog.dpu.rdfprofiler.dataunitsvalidated"));
            cbDataUnitProfiled.removeAllItems();
            for (int i = 0; i < outputDataUnitNames.size(); i++) {
                String dataUnitNameAvailable = outputDataUnitNames.get(i);
                cbDataUnitProfiled.addItem(dataUnitNameAvailable);
            }

            layout.addComponent(cbDataUnitProfiled);

            lInfo = new Label(RdfProfiler.this.context.asUserContext().tr("dialog.dpu.rdfprofiler.labelInfo"));
            layout.addComponent(lInfo);

//            txtAskQuery = new TextArea(RdfProfiler.this.context.asUserContext().tr("dialog.dpu.rdfprofiler.sparqlaskquery"));
//            txtAskQuery.setSizeFull();
//            txtAskQuery.setNullRepresentation("");
//            txtAskQuery.setNullSettingAllowed(true);
//            layout.addComponent(txtAskQuery);
//            layout.setExpandRatio(txtAskQuery, 1.0f);

          //  lNumOfTriples = new Label(RdfProfiler.this.context.asUserContext().tr("dialog.dpu.rdfprofiler.sparqlaskquery"));


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
//            txtAskQuery.setValue(conf.getAskQuery());
//            txtAskQuery.setEnabled(checkEnabled.getValue());

            if (conf.getProfiledDataUnitName() != null && !conf.getProfiledDataUnitName().isEmpty())
                cbDataUnitProfiled.setValue(conf.getProfiledDataUnitName());
            else if (cbDataUnitProfiled.size() > 0) {
                //select the first one otherwise 
                cbDataUnitProfiled.setValue(outputDataUnitNames.get(0));
            } else {
                //do not select any (no item!) 
            }
            cbDataUnitProfiled.setEnabled(checkEnabled.getValue());
        }

        @Override
        protected Configuration_V1 getConfiguration() throws DPUConfigException {
            final Configuration_V1 c = new Configuration_V1();
            c.setEnabled(checkEnabled.getValue());
//            c.setAskQuery(txtAskQuery.getValue());

            if (cbDataUnitProfiled.getValue() != null) {
                c.setProfiledDataUnitName(cbDataUnitProfiled.getValue().toString());
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
        return ADDON_DIALOG_NAME;
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
                    "Failed to load configuration for: {0} default configuration is used.", ADDON_DIALOG_NAME);
            this.config = new Configuration_V1();
        }
        if (this.config == null) {
            ContextUtils.sendInfo(context.asUserContext(), "Addon failed to load configuration",
                    "Failed to load configuration for: {0} default configuration is used.", ADDON_DIALOG_NAME);
            this.config = new Configuration_V1();
        }

        if (context instanceof ExecContext) {
            //we are executing the extension

            //continue only when the extension is enabled
            if (config.enabled) {

                //get the particular instance of the selected output data unit to be profiled (this is available only during execution)
                this.dpuContext = ((ExecContext) context).getDpuContext();
                log.info("\tcontext set to: {}", this.dpuContext);

                final ExecContext execContext = (ExecContext) context;
                final AbstractDpu dpu = execContext.getDpu();

                profiledDataUnit = null;

                //get output data unit which should be profiled
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
                                log.info("Data unit to be profiled: {}", config.profiledDataUnitName);
                                log.info("Examined data unit name: {}", field.getAnnotation(DataUnit.AsOutput.class).name());
                                if (field.getAnnotation(DataUnit.AsOutput.class).name().equals(config.profiledDataUnitName)) {
                                    profiledDataUnit = (RDFDataUnit) value;
                                    return;
                                }
                            }

                        } catch (IllegalAccessException | IllegalArgumentException ex) {
                            throw new DPUException("Can't get value for annotated field: " + field.getName(), ex);
                        }
                    }
                }
                //if we get here, something strange happened
                log.error("Something strange happens, RDF validation was disabled. Data unit with the given name {} cannot be fetched. This is strange as the name was selected from the list of available data units! ", config.profiledDataUnitName);
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
