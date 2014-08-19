package eu.unifiedviews.dpu;

/**
 * Base class for exception that are connected to the DPU execution problems.
 * Can also be thrown by {@link DPU#execute(DPUContext)} to indicate that the execution has failed.
 */
public class DPUException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 7553781737871929862L;

    /**
     * {@inheritDoc}
     */
    public DPUException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DPUException(String cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DPUException(String message, Throwable cause) {
        super(message, cause);
    }
}
