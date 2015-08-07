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
package eu.unifiedviews.helpers.dpu.config;

/**
 *
 * @author Škoda Petr
 */
public interface ConfigSerializer {

    /**
     * 
     * @param <TYPE>
     * @param configAsString
     * @param clazz
     * @return True if given object may be deserialise by this serializer.
     */
    public <TYPE> boolean canDeserialize(String configAsString, Class<TYPE> clazz);

    /**
     * 
     * @param <TYPE>
     * @param configAsString
     * @param className
     * @return True if given object may be deserialise by this serializer.
     */
    public <TYPE> boolean canDeserialize(String configAsString, String className);

    /**
     * Convert string form of representation info a configuration object.
     *
     * @param <TYPE>
     * @param configAsString
     * @param clazz
     * @return Null if object can't be deserialise.
     */
    public <TYPE> TYPE deserialize(String configAsString, Class<TYPE> clazz);

    /**
     * Convert object into a string form.
     *
     * @param <TYPE>
     * @param configObject
     * @return Null if object can't be serialised.
     */
    public <TYPE> String serialize(TYPE configObject);

}
