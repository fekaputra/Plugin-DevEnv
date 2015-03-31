package eu.unifiedviews.helpers.dpu.vaadin.dialog;

import static eu.unifiedviews.helpers.dpu.exec.AbstractDpu.DPU_CONFIG_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.dpu.config.vaadin.AbstractConfigDialog;
import eu.unifiedviews.dpu.config.vaadin.ConfigDialogContext;
import eu.unifiedviews.dpu.config.vaadin.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.config.MasterConfigObject;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFailure;

/**
 * Base class for DPU's configuration dialogs.
 *
 * @author Škoda Petr
 * @param <CONFIG>
 */
public abstract class AbstractDialog<CONFIG> extends AbstractConfigDialog<MasterConfigObject>
        implements InitializableConfigDialog {

    private static final String ABOUT_DIALOG_PROPERTY = "frontend.dpu.dialog.about.disabled";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDialog.class);

    /**
     * Main tab sheet.
     */
    private final TabSheet tabSheet = new TabSheet();

    /**
     * Currently set main sheet.
     */
    private Tab mainTab = null;

    /**
     * Store value of last set configuration.
     */
    private String lastSetConfiguration = null;

    /**
     * Dialog's originalDialogContext.
     */
    private DialogContext<CONFIG> context = null;

    /**
     * Original dialog context.
     */
    private ConfigDialogContext dialogContextHolder = null;

    /**
     * Class of associated DPU.
     */
    private final Class<AbstractDpu<CONFIG>> dpuClass;

    /**
     * User visible dialog context.
     */
    protected UserDialogContext ctx;

    public <DPU extends AbstractDpu<CONFIG>> AbstractDialog(Class<DPU> dpuClass) {
        this.dpuClass = (Class<AbstractDpu<CONFIG>>) dpuClass;
    }

    @Override
    public void initialize() throws DPUConfigException {
        try {
            context = new DialogContext(this, dialogContextHolder, dpuClass, null);
        } catch (DPUException ex) {
            LOG.error("Can't create dialog context!", ex);
            throw new DPUConfigException("Dialog initialization failed!", ex);
        }
        // Set user context.
        this.ctx = new UserDialogContext(this.context);
        // Build main layout.
        buildMainLayout();

        //  Build user layout.
        buildDialogLayout();
    }

    /**
     * Build main layout and add dialogs for add-ons.
     *
     * @param addons
     */
    private void buildMainLayout() {
        setSizeFull();
        tabSheet.setSizeFull();
        // Prepare add-ons.
        for (Configurable addon : this.context.getConfigurableAddons()) {
            final AbstractExtensionDialog dialog = addon.getDialog();
            if (dialog == null) {
                LOG.error("Dialog is ignored as it's null: {}", addon.getDialogCaption());
            } else {
                dialog.buildLayout();
                // FIXME: should be localized
                addTab(dialog, addon.getDialogCaption());
                this.context.addonDialogs.add(dialog);
            }
        }
        // Add AboutTab if not disabled in properties
        String aboutDisabledProperty = this.context.getDialogContext().getEnvironment().get(ABOUT_DIALOG_PROPERTY);
        if (aboutDisabledProperty == null || !Boolean.parseBoolean(aboutDisabledProperty)) {
            final AboutTab aboutTab = new AboutTab();
            aboutTab.buildLayout(context);
            // FIXME: should be localized
            addTab(aboutTab, aboutTab.getCaption());
            // We do not register for this.ctx.addonDialogs.add(dialog); as this is static element.
        }

        // Set composition root.
        super.setCompositionRoot(tabSheet);
    }

    @Override
    protected void setCompositionRoot(Component compositionRoot) {
        if (mainTab != null && mainTab.getComponent().equals(compositionRoot)) {
            // Already set, just update selected tab index.
            tabSheet.setSelectedTab(0);
            return;
        }
        // FIXME: should be localized
        final Tab newTab = tabSheet.addTab(compositionRoot, "DPU configuration");
        // Remove old one if set, and set new as a master tab (tab with DPU's configuration).
        if (mainTab != null) {
            tabSheet.removeTab(mainTab);
        }
        mainTab = newTab;
        tabSheet.setTabPosition(newTab, 0);
        tabSheet.setSelectedTab(0);
    }

    /**
     *
     * @param component Tab to add.
     * @param caption   Tab name.
     */
    protected void addTab(Component component, String caption) {
        final Tab newTab = tabSheet.addTab(component, caption);
    }

    @Override
    public void setContext(ConfigDialogContext newContext) {
        this.dialogContextHolder = newContext;
    }

    /**
     *
     * @return Dialog originalDialogContext.
     */
    protected ConfigDialogContext getContext() {
        return this.context.getDialogContext();
    }

    @Override
    public void setConfig(String conf) throws DPUConfigException {
        context.getConfigManager().setMasterConfig(conf);
        // Configure DPU's dialog.
        final CONFIG dpuConfig = context.getConfigManager().get(DPU_CONFIG_NAME,
                this.context.getConfigHistory());
        setConfiguration(dpuConfig);
        // Configura add-ons.
        for (AbstractExtensionDialog dialogs : this.context.addonDialogs) {
            dialogs.loadConfig(context.getConfigManager());
        }
        // Update last configuration.
        this.lastSetConfiguration = conf;
    }

    @Override
    public String getConfig() throws DPUConfigException {
        // Clear config mamanger.
        context.getConfigManager().setMasterConfig(new MasterConfigObject());
        // Get configuration from DPU.
        CONFIG dpuConfig = getConfiguration();
        context.getConfigManager().set(dpuConfig, AbstractDpu.DPU_CONFIG_NAME);
        // Get configuration from addons.
        for (AbstractExtensionDialog dialogs : this.context.addonDialogs) {
            dialogs.storeConfig(context.getConfigManager());
        }
        // Convert all into a string.
        try {
            return context.getSerializationXml().convert(context.getConfigManager().getMasterConfig());
        } catch (SerializationFailure | SerializationXmlFailure ex) {
            throw new DPUConfigException("Conversion failed.", ex);
        }
    }

    @Override
    public String getToolTip() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean hasConfigChanged() {
        // We utilize string form of configuration to decide it the configuration has changed or not.
        // This could be done probably better, but not in general case.
        final String configString;
        try {
            configString = getConfig();
        } catch (DPUConfigException ex) {
            // Exception according to definition return false.
            LOG.warn("Dialog configuration is invalid. It's assumed unchanged: ", ex);
            return false;
        } catch (Throwable ex) {
            LOG.warn("Unexpected exception. Configuration is assumed to be unchanged.", ex);
            return false;
        }

        if (this.lastSetConfiguration == null) {
            return configString == null;
        } else {
            return this.lastSetConfiguration.compareTo(configString) != 0;
        }
    }

    /**
     * It's called before the dialog is shows and after the context is accessible. Should be used to
     * initialise dialog layout.
     */
    protected abstract void buildDialogLayout();

    /**
     * Set DPU's configuration for dialog.
     *
     * @param conf
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    protected abstract void setConfiguration(CONFIG conf) throws DPUConfigException;

    /**
     * Get DPU's dialog configuration.
     *
     * @return
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    protected abstract CONFIG getConfiguration() throws DPUConfigException;

}
