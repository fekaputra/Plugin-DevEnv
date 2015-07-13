package eu.unifiedviews.helpers.dpu.vaadin.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.helpers.dpu.context.UserContext;

/**
 * About page for DPU. Content should be generated based on context.
 * TODO Petr: We should consider localization here too.
 *
 * @author Škoda Petr
 */
public class AboutTab extends CustomComponent {

    private static final long serialVersionUID = -1097541500977063250L;

    private static final Logger LOG = LoggerFactory.getLogger(AboutTab.class);

    private static final String DPU_ABOUT_TAB_NAME = "dialog.dpu.tab.about";

    private final String BUNDLE_NAME = "build-info";

    private final String HTML_FOOTER = "<br/><hr/>"
            + "Powered by <a href=\"https://github.com/mff-uk\">CUNI helpers</a>.";

    public AboutTab() {
        // No-op here.
    }

    @Override
    public String getCaption() {
        return DPU_ABOUT_TAB_NAME;
    }

    public void buildLayout(DialogContext context) {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(false);

        // Informations from build-info.properties.
        final ResourceBundle buildInfo = ResourceBundle.getBundle(BUNDLE_NAME,
                context.getDialogContext().getLocale(),
                context.getDpuClass().getClassLoader());
        // Just as a shortcut for translation.
        final UserContext ctx = context.asUserContext();

        final StringBuilder aboutHtml = new StringBuilder();
        aboutHtml.append("<b>");
        aboutHtml.append(ctx.tr("lib.helpers.vaadin.about.buildInfo"));
        aboutHtml.append("</b></br>");
        aboutHtml.append("<ul>");

        aboutHtml.append("<li>");
        aboutHtml.append(ctx.tr("lib.helpers.vaadin.about.buildTime"));
        aboutHtml.append(buildInfo.getString("build.timestamp"));
        aboutHtml.append("</li>");

        final String gitInfo = buildGitInfo(buildInfo, ctx);
        if (gitInfo != null) {
            aboutHtml.append("<li>");
            aboutHtml.append(gitInfo);
            aboutHtml.append("</li>");
        }

        aboutHtml.append("</ul>");
        aboutHtml.append("<hr/>");

        // Load optional properties.

        // Add generated text into a dilaog.
        mainLayout.addComponent(new Label(aboutHtml.toString(), ContentMode.HTML));

        // Add user provided description if available.
        final String userDescription = loadUserAboutText(context);
        if (userDescription != null) {
            mainLayout.addComponent(new Label(userDescription, ContentMode.HTML));
        }

        // Logo at the verz end.
        mainLayout.addComponent(new Label(HTML_FOOTER, ContentMode.HTML));

        // Wrap all into a panel.
        final Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        List<String> result = new ArrayList<String>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(basename);

        temp.append('_');
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }

        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }

        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }

        return result;
    }

    protected String loadUserAboutText(DialogContext context) {
        final ClassLoader classLoader = context.getDpuClass().getClassLoader();
        final Locale locale = context.getDialogContext().getLocale();

        List<String> fileNames = calculateFilenamesForLocale("About", locale);
        fileNames.addAll(calculateFilenamesForLocale("about", locale));
        fileNames.add("About");
        fileNames.add("about");
        for (String filenamePrefix : fileNames) {
            String fileName = filenamePrefix + ".html";
            final String result = loadStringFromResource(classLoader, fileName);
            if (result != null) {
                return result;
            }
        }
        // no About found
        return null;

    }

    protected String loadStringFromResource(ClassLoader classLoader, String resourceName) {
        try (InputStream inStream = classLoader.getResourceAsStream(resourceName)) {
            if (inStream == null) {
                // Missing resource.
                return null;
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            final StringBuilder builder = new StringBuilder(256);
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException ex) {
            LOG.error("Failed to load about.html.", ex);
            return null;
        }
    }

    /**
     * @param buildInfo
     * @param ctx
     * @return Null if no GIT related info is available.
     */
    protected String buildGitInfo(final ResourceBundle buildInfo, UserContext ctx) {
        final StringBuilder gitInfo = new StringBuilder(100);
        gitInfo.append(ctx.tr("lib.helpers.vaadin.about.git"));
        gitInfo.append("<ul>");
        boolean notEmpty = false;
        try {
            final String gitBranch = buildInfo.getString("git.branch");
            notEmpty = true;
            // Add to output.
            gitInfo.append("<li>");
            gitInfo.append(ctx.tr("lib.helpers.vaadin.about.git.branch"));
            gitInfo.append(gitBranch);
            gitInfo.append("</li>");
        } catch (MissingResourceException | ClassCastException ex) {
        }
        //
        try {
            final String gitDirty = buildInfo.getString("git.dirty");
            notEmpty = true;
            // Add to output.
            gitInfo.append("<li>");
            gitInfo.append(ctx.tr("lib.helpers.vaadin.about.git.dirty"));
            gitInfo.append(gitDirty);
            gitInfo.append("</li>");
        } catch (MissingResourceException | ClassCastException ex) {
        }
        //
        try {
            String gitCommit = buildInfo.getString("git.commit.id");
            notEmpty = true;
            try {
                // Again optional.
                final String gitRepository = buildInfo.getString("git.repository.link");
                if (gitRepository != null && !gitRepository.isEmpty()) {
                    final StringBuilder gitCommitBuilder = new StringBuilder(20);
                    gitCommitBuilder.append("<a href=\"");
                    gitCommitBuilder.append(gitRepository);
                    gitCommitBuilder.append("/commit/");
                    gitCommitBuilder.append(gitCommit);
                    gitCommitBuilder.append("\">");
                    gitCommitBuilder.append(gitCommit);
                    gitCommitBuilder.append("</a>");
                    gitCommit = gitCommitBuilder.toString();
                }
            } catch (MissingResourceException | ClassCastException ex) {
            }
            // Add to output.
            gitInfo.append("<li>");
            gitInfo.append(ctx.tr("lib.helpers.vaadin.about.git.commit"));
            gitInfo.append(gitCommit);
            gitInfo.append("</li>");
        } catch (MissingResourceException | ClassCastException ex) {
        }
        gitInfo.append("</ul>");
        //
        if (notEmpty) {
            return gitInfo.toString();
        } else {
            return null;
        }
    }

}
