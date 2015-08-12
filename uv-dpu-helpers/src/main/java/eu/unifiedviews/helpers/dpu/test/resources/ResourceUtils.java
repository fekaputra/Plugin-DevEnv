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
package eu.unifiedviews.helpers.dpu.test.resources;

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
