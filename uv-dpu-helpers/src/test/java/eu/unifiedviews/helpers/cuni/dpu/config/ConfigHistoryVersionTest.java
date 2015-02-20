package eu.unifiedviews.helpers.cuni.dpu.config;

import eu.unifiedviews.helpers.cuni.dpu.config.ConfigSerializer;
import eu.unifiedviews.helpers.cuni.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.cuni.dpu.config.ConfigException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import eu.unifiedviews.helpers.cuni.dpu.config.serializer.XStreamSerializer;
import eu.unifiedviews.helpers.cuni.serialization.SerializationFailure;
import eu.unifiedviews.helpers.cuni.serialization.xml.SerializationXml;
import eu.unifiedviews.helpers.cuni.serialization.xml.SerializationXmlFactory;
import eu.unifiedviews.helpers.cuni.serialization.xml.SerializationXmlFailure;

/**
 * 
 * @author Škoda Petr
 */
public class ConfigHistoryVersionTest {

    SerializationXml serialization = SerializationXmlFactory.serializationXml();

    ConfigHistory<Config_V3> historyHolder = ConfigHistory
            .history(Config_V1.class)
            .add(Config_V2.class)
            .addCurrent(Config_V3.class);

    @Test
    public void fromFirstToThird() throws SerializationXmlFailure, ConfigException, SerializationFailure {
        Config_V1 v1 = new Config_V1();
        v1.setValue(3);

        final String v1Str = serialization.convert(v1);
        final List<ConfigSerializer> serializers = new LinkedList<>();
        serializers.add(new XStreamSerializer(serialization));
        Config_V3 v3 = historyHolder.parse(v1Str, serializers);

        Assert.assertEquals("3", v3.getStr1());
        Assert.assertEquals("<a>3</a>", v3.getStr2());
    }

//    @Test
    public void lastToLast() throws SerializationXmlFailure, ConfigException, SerializationFailure {
        Config_V3 v3 = new Config_V3();
        v3.setStr1("3");
        v3.setStr2("abc");

        final String v3Str = serialization.convert(v3);

        final List<ConfigSerializer> serializers = new LinkedList<>();
        serializers.add(new XStreamSerializer(serialization));
        Config_V3 v3New = historyHolder.parse(v3Str, serializers);

        Assert.assertEquals("3", v3New.getStr1());
        Assert.assertEquals("abc", v3New.getStr2());
    }

}
