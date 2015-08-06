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

    private static final long serialVersionUID = 2482689171030846291L;

    private static final String DIALOG_TAB_DISABLED_PROPETY_PREFIX = "frontend.dpu.tab.disabled.";

    private static final String DPU_CONFIGURATION_TAB_NAME = "dialog.dpu.tab.config";

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
        this.tabSheet.setSizeFull();
        // Prepare configurable addons
        prepareConfigurableAddons();
        // Add AboutTab if not disabled in properties

        final AboutTab aboutTab = new AboutTab();
        aboutTab.buildLayout(this.context);
        addTab(aboutTab, aboutTab.getCaption());
        // We do not register for this.ctx.addonDialogs.add(dialog); as this is static element.

        // Set composition root.
        super.setCompositionRoot(this.tabSheet);
    }

    private void prepareConfigurableAddons() {
        for (Configurable<CONFIG> addon : this.context.getConfigurableAddons()) {
            final AbstractExtensionDialog<CONFIG> dialog = addon.getDialog();
            if (dialog == null) {
                LOG.error("Dialog is ignored as it's null: {}", addon.getDialogCaption());
            } else {
                String dialogPropertyName = DIALOG_TAB_DISABLED_PROPETY_PREFIX + addon.getClass().getSimpleName();
                String dialogDisabledProperty = this.context.getDialogContext().getEnvironment().get(dialogPropertyName);
                LOG.debug("Disable property name / value for tab: {}/{}", dialogPropertyName, dialogDisabledProperty);
                if (dialogDisabledProperty == null || !Boolean.parseBoolean(dialogDisabledProperty)) {
                    dialog.buildLayout();
                    addTab(dialog, addon.getDialogCaption(), false);
                    this.context.addonDialogs.add(dialog);
                }

            }
        }

    }

    @Override
    protected void setCompositionRoot(Component compositionRoot) {
        if (mainTab != null && mainTab.getComponent().equals(compositionRoot)) {
            // Already set, just update selected tab index.
            tabSheet.setSelectedTab(0);
            return;
        }
        final Tab newTab = tabSheet.addTab(compositionRoot, this.ctx.tr(DPU_CONFIGURATION_TAB_NAME));
        // Remove old one if set, and set new as a master tab (tab with DPU's configuration).
        if (mainTab != null) {
            tabSheet.removeTab(mainTab);
        }
        mainTab = newTab;
        tabSheet.setTabPosition(newTab, 0);
        tabSheet.setSelectedTab(0);
    }

    /**
     * @param component
     *            Tab to add.
     * @param caption
     *            Tab name resource for translation
     */
    protected void addTab(Component component, String caption) {
        addTab(component, caption, true);
    }

    /**
     * @param component
     *            Tab to add.
     * @param caption
     *            Tab name resource for translation
     * @param bCheckDialogHidden
     *            Whether to check configuration to hide the tab
     */
    private void addTab(Component component, String caption, boolean bCheckDialogHidden) {
        String dialogDisabledProperty = null;
        if (bCheckDialogHidden) {
            String dialogPropertyName = DIALOG_TAB_DISABLED_PROPETY_PREFIX + component.getClass().getSimpleName();
            dialogDisabledProperty = this.context.getDialogContext().getEnvironment().get(dialogPropertyName);
            LOG.debug("Disable property name / value for tab: {}/{}", dialogPropertyName, dialogDisabledProperty);
        }
        if (dialogDisabledProperty == null || !Boolean.parseBoolean(dialogDisabledProperty)) {
            this.tabSheet.addTab(component, this.ctx.tr(caption));
        }
    }

    @Override
    public void setContext(ConfigDialogContext newContext) {
        this.dialogContextHolder = newContext;
    }

    /**
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
        // Configure add-ons.
        for (AbstractExtensionDialog dialogs : this.context.addonDialogs) {
            dialogs.loadConfig(context.getConfigManager());

            //The following line is used in order to fix https://github.com/UnifiedViews/Core/issues/448. 
            //Note: If someone changes conf dialog of addon, user is not informed about that, but such change is silently used.
            dialogs.storeConfig(context.getConfigManager());
        }
        // Update last configuration.
        try {
            //last configuration is not taken from "conf" param of the method, but rather deserialized from master 
            //config object to fix https://github.com/UnifiedViews/Core/issues/448. As a result extra serialization/deserialization appears, which is not necessary.
            this.lastSetConfiguration = context.getSerializationXml().convert(context.getConfigManager().getMasterConfig());
        } catch (SerializationFailure | SerializationXmlFailure ex) {
            throw new DPUConfigException("Conversion failed.", ex);
        }
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
    public boolean hasConfigChanged() throws DPUConfigException {
        // We utilize string form of configuration to decide it the configuration has changed or not.
        // This could be done probably better, but not in general case.
        final String configString;
        try {
            configString = getConfig();
        } catch (DPUConfigException ex) {
            throw ex;
        } catch (Throwable ex) {
            LOG.warn("Unexpected exception. Configuration is assumed to be invalid.", ex);
            throw new DPUConfigException(ex);
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
