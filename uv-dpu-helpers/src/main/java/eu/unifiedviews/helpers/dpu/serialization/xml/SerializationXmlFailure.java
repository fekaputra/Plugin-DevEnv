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
package eu.unifiedviews.helpers.dpu.serialization.xml;

/**
 *
 * @author Škoda Petr
 */
public class SerializationXmlFailure extends Exception {

    public SerializationXmlFailure(String message) {
        super(message);
    }

    public SerializationXmlFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationXmlFailure(Throwable cause) {
        super(cause);
    }

}
