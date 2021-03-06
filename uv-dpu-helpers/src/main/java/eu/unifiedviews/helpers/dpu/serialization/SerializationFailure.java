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
package eu.unifiedviews.helpers.dpu.serialization;

import eu.unifiedviews.helpers.dataunit.HelperFailedException;

/**
 * Base exception used by serialization module.
 * 
 * @author Škoda Petr
 */
public class SerializationFailure extends HelperFailedException {

    public SerializationFailure(String message) {
        super(message);
    }

    public SerializationFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationFailure(Throwable cause) {
        super(cause);
    }

}
