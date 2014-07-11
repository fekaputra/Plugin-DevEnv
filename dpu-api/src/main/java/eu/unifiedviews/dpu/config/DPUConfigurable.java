package eu.unifiedviews.dpu.config;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.dpu.config.DPUConfig;

/**
 * Interface describes object that can be configured by using configuration.
 * 
 * @author Petyr
 * @param <C>
 *            Configuration object that carries the configuration.
 */
public interface DPUConfigurable<C extends DPUConfig> {

    /**
     * Deserialize given configuration and then use it to
     * configure object. If the invalid configuration is given then {@link DPUConfigException} is thrown. For null the configuration
     * is left unchanged.
     * 
     * @param config
     *            Serialized configuration.
     * @throws DPUConfigException
     */
    void configure(String config) throws DPUConfigException;

    /**
     * Return serialized configuration object. If no configuration has
     * been previously set by {@link #configure(java.lang.String) } then serialized default
     * configuration should be returned.
     * 
     * @return Serialized configuration.
     * @throws DPUConfigException
     *             If the configuration can't be serialized.
     */
    String getDefaultConfiguration() throws DPUConfigException;

}
