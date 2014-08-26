package eu.unifiedviews.helpers.dpu.config;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import org.junit.Before;
import org.junit.Test;

import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * Test suite for {@link ConfigWrap} class.
 *
 * @author Petyr
 */
public class ConfigWrapTest {

    @SuppressWarnings("rawtypes")
    private ConfigWrap configWrap;

    private Object configObject;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() {
        configObject = mock(Object.class, withSettings().serializable());
        configWrap = new ConfigWrap(configObject.getClass());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serialisationNull() throws DPUConfigException {
        String serialized = configWrap.serialize(null);
        assertNull(serialized);
    }

}
