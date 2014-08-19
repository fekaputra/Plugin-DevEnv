package eu.unifiedviews.dpu.config;

import eu.unifiedviews.dpu.DPU;

/**
 * Exception used in relation do {@link DPU} invalid configuration.
 * Thrown by {@link DPU} to indicate invalid configuration to application.
 */
public class DPUConfigException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 9217632742130977047L;

    /**
     * {@inheritDoc}
     */
    public DPUConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DPUConfigException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public DPUConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
