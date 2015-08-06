package eu.unifiedviews.helpers.dpu.config;

import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * Interface for versioned configuration.
 *
 * @author Škoda Petr
 * @param <CONFIG> Configuration to which this configuration can be converted (updated) to.
 */
public interface VersionedConfig<CONFIG> {

    /**
     *
     * @return Next version of configuration with "same" settings.
     * @throws DPUConfigException
     */
    CONFIG toNextVersion() throws DPUConfigException;

}
