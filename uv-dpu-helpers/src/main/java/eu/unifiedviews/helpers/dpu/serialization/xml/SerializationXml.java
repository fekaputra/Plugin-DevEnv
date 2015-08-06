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

import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;

/**
 * Interface for XML serialisation interface.
 * Notes:
 * <ul>
 * <li>Support substitution of new items only to first level.</li>
 * </ul>
 * 
 * @author Škoda Petr
 * @deprecated Use {@link SerializationXmlGeneral} instead.
 */
public interface SerializationXml {

    /**
     * Add alias to class.
     *
     * @param clazz
     * @param alias
     */
    public void addAlias(Class<?> clazz, String alias);

    public <T> T convert(Class<T> clazz, String string) throws SerializationFailure, SerializationXmlFailure;

    public <T> String convert(T object) throws SerializationFailure, SerializationXmlFailure;
    
}
