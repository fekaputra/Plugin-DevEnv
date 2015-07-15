/*******************************************************************************
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
 *******************************************************************************/
package eu.unifiedviews.helpers.dpu.config;

import eu.unifiedviews.helpers.dpu.config.ConfigSerializer;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.ConfigException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import eu.unifiedviews.helpers.dpu.config.serializer.XStreamSerializer;
import eu.unifiedviews.helpers.dpu.serialization.SerializationFailure;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXml;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFactory;
import eu.unifiedviews.helpers.dpu.serialization.xml.SerializationXmlFailure;

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
