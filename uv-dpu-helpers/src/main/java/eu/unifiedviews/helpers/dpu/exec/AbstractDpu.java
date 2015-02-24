package eu.unifiedviews.helpers.dpu.exec;

import eu.unifiedviews.helpers.dpu.extension.Extension;
import eu.unifiedviews.helpers.dpu.extension.ExtensionException;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUContext.MessageType;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.dpu.config.DPUConfigurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.config.vaadin.AbstractConfigDialog;
import eu.unifiedviews.dpu.config.vaadin.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.MasterConfigObject;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;
import eu.unifiedviews.helpers.dpu.serialization.SerializationUtils;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFailure;

/**
 * Base class for DPUs.
 *
 * @author Škoda Petr
 * @param <CONFIG> Type of DPU's configuration object.
 */
public abstract class AbstractDpu<CONFIG> implements DPU, DPUConfigurable,
        ConfigDialogProvider<MasterConfigObject> {

    /**
     * Name used for DPU's configuration class.
     */
    public static final String DPU_CONFIG_NAME = "dpu_config";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDpu.class);

    /**
     * Holds all variables of this class.
     */
    private ExecContext<CONFIG> masterContext = null;

    /**
     * History of configuration.
     *
     * As holder is used to store information until it's used by context.
     */
    private final ConfigHistory<CONFIG> configHistoryHolder;

    /**
     * Configuration class visible for the DPU.
     */
    protected CONFIG config;

    /**
     * User visible context.
     */
    protected UserExecContext ctx;

    /**
     * Class of user dialog.
     */
    private final Class<AbstractDialog<CONFIG>> dialogClass;

    /**
     * Used to hold configuration between {@link #configure(java.lang.String)} and
     * {@link #execute(eu.unifiedviews.dpu.DPUContext)} where context is created.
     */
    private String configAsString;

    public <DIALOG extends AbstractDialog<CONFIG>> AbstractDpu(Class<DIALOG> dialogClass,
            ConfigHistory<CONFIG> configHistory) {
        this.dialogClass = (Class<AbstractDialog<CONFIG>>) dialogClass;
        this.configHistoryHolder = configHistory;
        // Initialize master context.
        this.masterContext = new ExecContext(this);
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        // Set master configuration and initialize ConfigTransformer -> initialize addons.
        this.masterContext.init(configAsString, context);
        // ConfigTransformer are ready from setConfiguration method -> get DPU configuration.
        try {
            this.masterContext.setDpuConfig((CONFIG) this.masterContext.getConfigManager().get(
                    DPU_CONFIG_NAME, this.masterContext.getConfigHistory()));
        } catch (ConfigException ex) {
            throw new DPUException("Configuration preparation failed.", ex);
        }
        // Set variables for DPU.
        this.config = this.masterContext.getDpuConfig();
        this.ctx = new UserExecContext(this.masterContext);
        // Execute DPU's code - innerInit.
        try {
            LOG.info("innerInit:start");
            innerInit();
            LOG.info("innerInit:end");
        } catch (DataUnitException ex) {
            throw new DPUException("DPU.innerInit fail for problem with data unit.", ex);
        } catch (DPUException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new DPUException("DPU.innerInit throws throwable.", ex);
        }
        // {@link Addon}'s execution point.
        boolean executeDpu = true;
        executeDpu = executeAddons(Extension.ExecutionPoint.PRE_EXECUTE);
        // Main execution for user code.
        DPUException exception = null;
        try {
            if (executeDpu) {
                LOG.info("innerExecute:start");
                innerExecute();
                LOG.info("innerExecute:end");
            }
        } catch (DPUException ex) {
            exception = ex;
        } catch (RuntimeException ex) {
            exception = new DPUException("DPU.innerExecute throws runtime exception.", ex);
        } catch (Throwable ex) {
            exception = new DPUException("DPU.innerExecute throws throwable.", ex);
        }
        if (exception != null) {
            LOG.error("DPU exeution failed!", exception);
        }

        // Execute DPU's code - innerCleanUp.
        try {
            LOG.info("innerCleanUp:start");
            innerCleanUp();
            LOG.info("innerCleanUp:stop");
        } catch (Throwable ex) {
            if (exception == null) {
                exception = new DPUException("DPU.innerCleanUp throws throwable.", ex);
            } else {
                context.sendMessage(MessageType.ERROR, "DPU Failed",
                        "DPU throws Throwable in innerCleanUp method. See logs for more details.");
                LOG.error("Throwable has ben thrown from innerCleanUp!", ex);
            }
        }
        // {@link Addon}'s execution point.
        executeAddons(Extension.ExecutionPoint.POST_EXECUTE);
        // And throw an exception.
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void configure(String config) throws DPUConfigException {
        this.configAsString = config;
    }

    @Override
    public String getDefaultConfiguration() throws DPUConfigException {
        try {
            // Get default configuraiton.
            final MasterConfigObject defaultConfig = createDefaultMasterConfig();
            // Serialize into string and return.
            return this.masterContext.getSerializationXml().convert(defaultConfig);
        } catch (SerializationFailure | SerializationXmlFailure ex) {
            throw new DPUConfigException("Config serialization failed.", ex);
        }
    }

    @Override
    public AbstractConfigDialog<MasterConfigObject> getConfigurationDialog() {
        final AbstractConfigDialog<MasterConfigObject> dialog;
        try {
            dialog = dialogClass.newInstance();
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException("Can't create dialog.", ex);
        }
        return dialog;

    }

    /**
     * Create default configuration for DPU.
     *
     * @return {@link MasterConfigObject} with default DPU configuration.
     * @throws SerializationXmlFailure
     */
    private MasterConfigObject createDefaultMasterConfig()
            throws SerializationXmlFailure, SerializationFailure {
        // Get string representation of DPU's config class.
        final CONFIG dpuConfig = (CONFIG) SerializationUtils.createInstance(
                this.masterContext.getConfigHistory().getFinalClass());
        final String dpuConfigStr = this.masterContext.getSerializationXml().convert(dpuConfig);
        // Prepare master config.
        final MasterConfigObject newConfigObject = new MasterConfigObject();
        newConfigObject.getConfigurations().put(DPU_CONFIG_NAME, dpuConfigStr);
        return newConfigObject;
    }

    /**
     * Execute all {@link ExecutableAddon}s of this DPU.
     *
     * @param execPoint
     * @return If exception is thrown then return false.
     */
    private boolean executeAddons(Extension.ExecutionPoint execPoint) {
        boolean result = true;
        for (Extension item : this.masterContext.geInitializables()) {
            if (item instanceof Extension.Executable) {
                final Extension.Executable executable = (Extension.Executable) item;
                try {
                    LOG.debug("Executing '{}' with on '{}' point", executable.getClass().getSimpleName(),
                            execPoint.toString());
                    executable.execute(execPoint);
                } catch (ExtensionException ex) {
                    ContextUtils.sendError(this.ctx, "Addon execution failed",
                            ex, "Addon: s", item.getClass().getSimpleName());
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     *
     * @return Current instance of used {@link ConfigHistory} class.
     */
    public ConfigHistory<CONFIG> getConfigHistoryHolder() {
        return this.configHistoryHolder;
    }

    /**
     * Is called before {@link #innerExecute()}. If this method throws DPU execution is immediately stopped.
     * No other function (not Add-on) is executed.
     *
     * DPU's configuration is already accessible.
     *
     * @throws eu.unifiedviews.dpu.DPUException
     * @throws DataUnitException
     */
    protected void innerInit() throws DPUException, DataUnitException {
        // Do nothing implementation.
    }

    /**
     * Execute user DPU code.
     *
     * @throws DPUException
     */
    protected abstract void innerExecute() throws DPUException;

    /**
     * Is called after the {@link #innerExecute()} ends.
     * {@link Addon#postAction(eu.unifiedviews.dpu.DPUContext, cz.cuni.mff.xrg.uv.boost.dpu.config.ConfigManager)}
     * methods are called after this method.
     */
    protected void innerCleanUp() {
        // Do nothing implementation.
    }

}
