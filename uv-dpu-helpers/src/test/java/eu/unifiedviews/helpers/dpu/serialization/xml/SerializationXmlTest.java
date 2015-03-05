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