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
package eu.unifiedviews.helpers.dpu.extension;

/**
 * Used to report problem in/with {@link Addon}.
 *
 * @author Škoda Petr
 */
public class ExtensionException extends Exception {

    public ExtensionException(String message) {
        super(message);
    }

    public ExtensionException(String format, Object ... params) {
        super(String.format(format, params));
    }

    public ExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

}
