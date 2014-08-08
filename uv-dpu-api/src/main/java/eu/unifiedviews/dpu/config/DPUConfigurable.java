package eu.unifiedviews.dpu.config;

/**
 * Interface describes object that can be configured by using configuration string.
 * 
 * @author Petyr
 */
public interface DPUConfigurable {

    /**
     * Use given configuration string to
     * configure object. If the invalid configuration is given then {@link DPUConfigException} is thrown. For null the configuration
     * is left unchanged.
     * 
     * @param config
     *            configuration.
     * @throws DPUConfigException to indicate invalid configuration
     */
    void configure(String config) throws DPUConfigException;

    /**
     * Return default configuration string.
     * 
     * @return configuration.
     * @throws DPUConfigException to indicate that default configuration cannot be obtained
     */
    String getDefaultConfiguration() throws DPUConfigException;

}
