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
package eu.unifiedviews.helpers.dpu.config.serializer;

import eu.unifiedviews.helpers.dpu.config.ConfigSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXml;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFactory;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFailure;

/**
 * Configuration deserializer based on SerializationXml - xStream.
 * 
 * @author Škoda Petr
 */
public class XStreamSerializer implements ConfigSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(XStreamSerializer.class);

    private final SerializationXml serializationXml;

    public XStreamSerializer() {
        serializationXml = SerializationXmlFactory.serializationXml();
    }

    public XStreamSerializer(SerializationXml serializationXml) {
        this.serializationXml = serializationXml;
    }

    @Override
    public <TYPE> boolean canDeserialize(String configAsString, Class<TYPE> clazz) {
        return canDeserialize(configAsString, getClassName(clazz));
    }

    @Override
    public <TYPE> boolean canDeserialize(String configAsString, String className) {
        return configAsString != null && configAsString.contains(className);
    }

    @Override
    public <TYPE> TYPE deserialize(String configAsString, Class<TYPE> clazz) {
        try {
            // Add alias.
            return serializationXml.convert(clazz, configAsString);
        } catch (SerializationFailure | SerializationXmlFailure ex) {
            LOG.info("Can't deserialized configuration.", ex);
            return null;
        }
    }

    @Override
    public <TYPE> String serialize(TYPE configObject) {
        try {
            return serializationXml.convert(configObject);
        } catch (SerializationFailure | SerializationXmlFailure ex) {
            LOG.info("Can't deserialized configuration.", ex);
            return null;
        }
    }

    /**
     *
     * @param clazz
     * @return Class name in format of xStream.
     */
    private String getClassName(Class<?> clazz) {
        // Get class name and update it into way in which xStream use it
        // so we can search in string directly.
        String className = clazz.getCanonicalName().replace("_", "__");
        if (clazz.getEnclosingClass() != null) {
            // Change last "." into "_-" used for sub classes - addon configurations.
            int lastDot = className.lastIndexOf(".");
            className = className.substring(0, lastDot) + "_-" + className.substring(lastDot + 1);
        }
        return className;
    }

}
