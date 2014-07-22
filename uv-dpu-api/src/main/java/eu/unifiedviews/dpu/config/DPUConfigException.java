package eu.unifiedviews.dpu.config;

/**
 * Exception used in relation do DPU invalid configuration.
 * 
 * @author Petyr
 */
public class DPUConfigException extends Exception {

    /**
     * Create exception with default message.
     */
    public DPUConfigException() {
        super("Invalid configuration.");
    }

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
