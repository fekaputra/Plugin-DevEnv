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

import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFactory;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXml;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFailure;
import org.junit.Assert;
import org.junit.Test;

import eu.unifiedviews.helpers.dpu.serialization.ConfigObject;
import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;
import eu.unifiedviews.helpers.dpu.serialization.subpackage.ConfigObjectCopy;

/**
 *
 * @author Škoda Petr
 */
public class SerializationXmlTest {

    @Test
    public void serializeAndDeserialize() throws SerializationXmlFailure, SerializationFailure {
        final ConfigObject original = new ConfigObject();
        original.setIntegralValue(10);
        final SerializationXml service = SerializationXmlFactory.serializationXml();
        // Convert to string.
        final String str = service.convert(original);
        // Convert back.
        final ConfigObject copy = service.convert(ConfigObject.class, str);
        // Test.
        Assert.assertNotEquals(original, copy);
        Assert.assertEquals(original.getIntegralValue(), copy.getIntegralValue());
        Assert.assertEquals("value", ConfigObject.PUBLIC_FINAL);
    }

    @Test
    public void crossObjects() throws SerializationXmlFailure, SerializationFailure {
        final ConfigObject original = new ConfigObject();
        original.setIntegralValue(10);
        // Serioalize ConfigObject under alias.
        final SerializationXml service_source = SerializationXmlFactory.serializationXml();
        service_source.addAlias(ConfigObject.class, "cnf");
        final String str = service_source.convert(original);
        // Deserialize as different object but with the same alias.
        final SerializationXml service_target = SerializationXmlFactory.serializationXml();
        service_target.addAlias(ConfigObjectCopy.class, "cnf");
        final ConfigObjectCopy copy = service_target.convert(ConfigObjectCopy.class, str);
        // Test.
        Assert.assertNotEquals(original, copy);
        Assert.assertEquals(original.getIntegralValue(), copy.getIntegralValue());
        Assert.assertEquals("value", ConfigObject.PUBLIC_FINAL);
    }

}
