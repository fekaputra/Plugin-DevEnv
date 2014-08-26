package eu.unifiedviews.helpers.dpu.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * Class provides functionality to serialize, deserialize and create instance config which is serialized as XML, using
 * XStream.
 *
 * @author Petyr
 * @param <C>
 */
public class ConfigWrap<C> {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigWrap.class);

    /**
     * Configuration's class.
     */
    private final Class<C> configClass;

    /**
     * Stream for deserialized.
     */
    private final XStream xstream;

    /**
     * Stream for serialization.
     */
    private final XStream xstreamUTF;

    /**
     * Store list of skipped fields during deserialization.
     */
    private final LinkedList<String> loadedFields = new LinkedList<>();

    /**
     * Create configuration wrap for given configuration class.
     *
     * @param configClass
     *            Configuration class.
     */
    public ConfigWrap(Class<C> configClass) {
        this.configClass = configClass;
        // stream for loading, not so strict, ignore missing fields
        //this.xstream.ignoreUnknownElements();
        this.xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn,
                            String fieldName) {
                        // the goal of this is to ignore missing fields
                        if (definedIn == Object.class) {
                            // skip the missing
                            LOG.warn("Skipped missing field: {}", fieldName);
                            return false;
                        }

                        if (super.shouldSerializeMember(definedIn, fieldName)) {
                            if (ConfigWrap.this.configClass.equals(definedIn)) {
                                loadedFields.add(fieldName);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }
        };
        this.xstream.setClassLoader(configClass.getClassLoader());
        this.xstream.alias("Configuration", configClass);
        this.xstream.alias("ConfigurationVersion", ConfigurationVersion.class);

        // save always in utf8
        this.xstreamUTF = new XStream(new DomDriver("UTF-8"));
        this.xstreamUTF.setClassLoader(configClass.getClassLoader());
        this.xstreamUTF.alias("Configuration", configClass);
        this.xstreamUTF.alias("ConfigurationVersion", ConfigurationVersion.class);
    }

    /**
     * Create instance generic ConfigSerializer object. In case of error return
     * null.
     *
     * @return Object instance or null.
     */
    public C createInstance() {
        try {
            return configClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Failed to create configuration instance", e);
            return null;
        }
    }

    /**
     * Deserialize configuration. If the parameter is null or empty then null is
     * returned.
     *
     * @param configStr
     *            Serialized configuration.
     * @return Deserialized configuration.
     * @throws DPUConfigException
     */
    @SuppressWarnings("unchecked")
    public C deserialize(String configStr) throws DPUConfigException {
        if (configStr == null || configStr.isEmpty()) {
            return null;
        }

        // clear the skip list
        loadedFields.clear();

        C config = null;
        // reconstruct object form byte[]
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(
                configStr.getBytes(Charset.forName("UTF-8")));
                ObjectInputStream objIn = xstream
                        .createObjectInputStream(byteIn)) {
            ConfigurationVersion configurationVersion = (ConfigurationVersion) objIn.readObject();
            Object obj = objIn.readObject();
            config = (C) obj;
        } catch (IOException e) {
            throw new DPUConfigException("Can't deserialize configuration.", e);
        } catch (ClassNotFoundException e) {
            throw new DPUConfigException("Can't re-cast configuration object.", e);
        } catch (Exception e) {
            throw new DPUConfigException(e);
        }

        return config;
    }

    /**
     * Serialized actual stored configuration. Can return null if configuration
     * is null.
     *
     * @param config
     *            Configuration to serialize.
     * @return Serialized configuration, can be null.
     * @throws DPUConfigException
     */
    public String serialize(C config) throws DPUConfigException {
        if (config == null) {
            return null;
        }

        byte[] result = null;
        // serialise object into byte[]
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            // use XStream for serialisation
            try (ObjectOutputStream objOut = xstreamUTF
                    .createObjectOutputStream(
                    byteOut)) {
                ConfigurationVersion configurationVersion = new ConfigurationVersion();
                configurationVersion.setClassName(config.getClass().getCanonicalName());
                configurationVersion.setVersion(1);

                objOut.writeObject(configurationVersion);
                objOut.writeObject(config);
            }
            result = byteOut.toByteArray();
        } catch (IOException e) {
            throw new DPUConfigException("Can't serialize configuration.", e);
        }
        return new String(result, Charset.forName("UTF-8"));
    }
}
