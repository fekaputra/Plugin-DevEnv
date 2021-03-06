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
package eu.unifiedviews.helpers.dpu.extension.faulttolerance;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.extension.Extension;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractExtensionDialog;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.Configurable;

/**
 * Provide possibility to wrap user code. Wrapped code may be re-executed in case of failure and so this
 * add-on can be used a form of fault tolerant layer.
 *
 * @author Škoda Petr
 */
public class FaultTolerance implements Extension, Configurable<FaultTolerance.Configuration_V1> {

    public static final String USED_CONFIG_NAME = "addon/faultToleranceWrap";

    public static final String ADDON_NAME = "dialog.dpu.tab.faulttolerance";

    private static final Logger LOG = LoggerFactory.getLogger(FaultTolerance.class);

    /**
     * Interface to wrap general user code.
     */
    public interface Action {

        /**
         * Put your code inside this method. Remember that this method can be executed more then once!
         *
         * @param connection
         */
        void action() throws Exception;

    }

    /**
     * Interface to wrap general user code that should return some value.
     */
    public interface ActionReturn<TYPE> {

        /**
         * Put your code inside this method. Remember that this method can be executed more then once!
         *
         * @param connection
         * @return
         */
        TYPE action() throws Exception;

    }

    /**
     * Interface to wrap user code that uses {@link RepositoryConnection}.
     */
    public interface ConnectionAction {

        /**
         * Put your code inside this method. Remember that this method can be executed more then once!
         *
         * @param connection
         */
        void action(RepositoryConnection connection) throws Exception;

    }

    public static class Configuration_V1 {

        /**
         * If false no fault tolerance is provided by this add-on.
         */
        private boolean enabled = false;

        /**
         * Store names of exceptions that should be catch.
         */
        private List<String> exceptionNames = new LinkedList<>();

        /**
         * Number of retry before failure, use -1 as no limit.
         */
        private int maxRetryCount = -1;

        public Configuration_V1() {
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getExceptionNames() {
            return exceptionNames;
        }

        public void setExceptionNames(List<String> exceptionNames) {
            this.exceptionNames = exceptionNames;
        }

        public int getMaxRetryCount() {
            return maxRetryCount;
        }

        public void setMaxRetryCount(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
        }

    }

    public class VaadinDialog extends AbstractExtensionDialog<Configuration_V1> {

        private CheckBox checkEnabled;

        public VaadinDialog() {
            super(configHistory);
        }

        @Override
        public void buildLayout() {
            final VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(true);
            layout.setMargin(true);

            checkEnabled = new CheckBox(FaultTolerance.this.context.asUserContext().tr("dialog.dpu.faulttolerance.enabled"));
            layout.addComponent(checkEnabled);

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
        }

        @Override
        protected Configuration_V1 getConfiguration() throws DPUConfigException {
            final Configuration_V1 c = new Configuration_V1();
            c.setEnabled(checkEnabled.getValue());
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
    public void afterInit(Context context) {
        this.context = context;
        if (context instanceof ExecContext) {
            this.dpuContext = ((ExecContext) context).getDpuContext();
            LOG.debug("\tcontext set to: {}", this.dpuContext);
        }
        // Load configuration.
        try {
            this.config = context.getConfigManager().get(USED_CONFIG_NAME, configHistory);
        } catch (ConfigException ex) {
            LOG.warn("Can't load configuration.", ex);
            ContextUtils.sendInfo(context.asUserContext(), "Addon failed to load configuration",
                    "Failed to load configuration for: {0} default configuration is used.", ADDON_NAME);
            this.config = new Configuration_V1();
        }
        if (this.config == null) {
            ContextUtils.sendInfo(context.asUserContext(), "Addon failed to load configuration",
                    "Failed to load configuration for: {0} default configuration is used.", ADDON_NAME);
            this.config = new Configuration_V1();
        }
        // Add exception for Virtuoso
        this.config.exceptionNames.add("java.sql.BatchUpdateException");
        // Virtuoso Communications Link Failure (timeout) : Connection to the server lost
        this.config.exceptionNames.add("virtuoso.jdbc4.VirtuosoException");
    }

    /**
     *
     * @param failCounter
     * @return new value for failCounter.
     * @throws DPUException If we run out of attempts.
     */
    private int checkFailState(Exception ex, int failCounter) throws DPUException {
        if (!shouldBeCatched(ex) || failCounter > config.maxRetryCount) {
            throw new DPUException("Operation failed.", ex);
        }
        LOG.warn("User operation failed {}/{}.", failCounter + 1, config.maxRetryCount, ex);
        return failCounter + (config.maxRetryCount == -1 ? 0 : 1);
    }

    public void execute(Action codeToExecute) throws DPUException {
        int failCounter = -1;
        while (!dpuContext.canceled()) {
            // Try to execute user code.
            try {
                codeToExecute.action();
                return;
            } catch (Exception ex) {
                failCounter = checkFailState(ex, failCounter);
            }
        }
        // If we get here we were interupter before we finished the operation.
        throw new DPUException("Interrupted before user operation could be completed.");
    }

    public <TYPE> TYPE execute(ActionReturn<TYPE> codeToExecute) throws DPUException {
        int failCounter = -1;
        while (!dpuContext.canceled()) {
            // Try to execute user code.
            try {
                return codeToExecute.action();
            } catch (Exception ex) {
                failCounter = checkFailState(ex, failCounter);
            }
        }
        // If we get here we were interupter before we finished the operation.
        throw new DPUException("Interrupted before user operation could be completed.");
    }

    public <T extends MetadataDataUnit> void execute(T dataUnit, ConnectionAction codeToExecute)
            throws DPUException {
        int failCounter = -1;
        while (!dpuContext.canceled()) {
            // Get connection.
            RepositoryConnection connection;
            try {
                connection = dataUnit.getConnection();
            } catch (DataUnitException ex) {
                // Test for max retry count.
                if (!shouldBeCatched(ex) || failCounter > config.maxRetryCount) {
                    throw new DPUException("Operation failed.", ex);
                } else {
                    LOG.warn("Can't get connection {}/{}.", failCounter, config.maxRetryCount, ex);
                    failCounter += (config.maxRetryCount == -1 ? 0 : 1);
                    continue;
                }
            }
            // Try to execute user code.
            try {
                codeToExecute.action(connection);
                return;
            } catch (Exception ex) {
                failCounter = checkFailState(ex, failCounter);
            } finally {
                // In every case close the connection.
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Can't close connection.", ex);
                }
            }
        }
        // If we get here we were interupter before we finished the operation.
        throw new DPUException("Interrupted before user operation could be completed.");
    }

    /**
     *
     * @param codeToExecute
     * @param failMessage   Text of exception that should be thrown in case of failure. Message is localized
     *                      before use.
     * @param args
     * @throws DPUException
     */
    public void execute(Action codeToExecute, String failMessage, Object... args) throws DPUException {
        try {
            execute(codeToExecute);
        } catch (DPUException ex) {
            // Rethrow with custom user message.
            throw ContextUtils.dpuException(context.asUserContext(), ex, failMessage, args);
        }
    }

    /**
     *
     * @param <TYPE>
     * @param codeToExecute
     * @param failMessage   Text of exception that should be thrown in case of failure. Message is localized
     *                      before use.
     * @param args
     * @return
     * @throws DPUException
     */
    public <TYPE> TYPE execute(ActionReturn<TYPE> codeToExecute, String failMessage, Object... args)
            throws DPUException {
        try {
            return execute(codeToExecute);
        } catch (DPUException ex) {
            // Rethrow with custom user message.
            throw ContextUtils.dpuException(context.asUserContext(), ex, failMessage, args);
        }
    }

    /**
     *
     * @param <T>
     * @param dataUnit
     * @param codeToExecute
     * @param failMessage   Text of exception that should be thrown in case of failure. Message is localized
     *                      before use.
     * @param args
     * @throws DPUException
     */
    public <T extends MetadataDataUnit> void execute(T dataUnit, ConnectionAction codeToExecute,
            String failMessage, Object... args) throws DPUException {
        try {
            execute(dataUnit, codeToExecute);
        } catch (DPUException ex) {
            // Rethrow with custom user message.
            throw ContextUtils.dpuException(context.asUserContext(), ex, failMessage, args);
        }
    }

    /**
     *
     * @param ex
     * @return True if this add-on is configured to catch this type of exception.
     */
    private boolean shouldBeCatched(Throwable exception) {
        do {
            // Check by name.
            final String exceptionClassName = exception.getClass().getCanonicalName();
            for (String item : config.exceptionNames) {
                LOG.trace("shouldBeCatched '{}' ? '{}'", exceptionClassName, item);
                if (item.compareToIgnoreCase(exceptionClassName) == 0) {
                    return true;
                }
            }
            exception = exception.getCause();
        } while (exception != null);
        return false;
    }

    /**
     * Set configuration for test purpose.
     *
     * @param config
     */
    void configure(Configuration_V1 config, DPUContext ctx) {
        this.config = config;
        this.dpuContext = ctx;
    }

}
