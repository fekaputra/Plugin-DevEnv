package eu.unifiedviews.helpers.test.resources;

import java.io.File;
import java.net.URL;

/**
 * Provide access to files in projects resources.
 *
 * @author Škoda Petr
 */
public class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * Return {@link File} to file if given name located in the java
     * resources directory.
     *
     * If file is not found then {@link RuntimeException} is thrown to 
     * terminate tests.
     *
     * @param name FIle name.
     * @return File representation.
     */
    public static File getFile(String name) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(name);
        if (url == null) {
            throw new RuntimeException("Required resourcce '" + name + "' is missing.");
        }
        return new File(url.getPath());
    }

}
