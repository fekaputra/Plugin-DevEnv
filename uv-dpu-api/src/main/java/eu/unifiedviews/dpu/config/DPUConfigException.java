package eu.unifiedviews.dpu.config;

/**
 * Exception used in relation do DPU invalid configuration.
 * Thrown by DPU to indicate invalid configuration to backend/frontend.
 * 
 * @author Petyr
 */
public class DPUConfigException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 9217632742130977047L;

    /**
     * @param cause
     *            Cause of the {@link ConfigException}.
     */
    public DPUConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     *            Cause of the {@link ConfigException}.
     */
    public DPUConfigException(String message) {
        super(message);
    }

    /**
     * @param message
     *            Description of action that throws.
     * @param cause
     *            Cause of the {@link ConfigException}.
     */
    public DPUConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
