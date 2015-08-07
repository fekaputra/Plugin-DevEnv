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
package eu.unifiedviews.helpers.dpu.serialization.rdf;

/**
 *
 * @author Škoda Petr
 */
public class SerializationRdfFailure extends Exception {

    public SerializationRdfFailure(String message) {
        super(message);
    }

    public SerializationRdfFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationRdfFailure(Throwable cause) {
        super(cause);
    }

}
