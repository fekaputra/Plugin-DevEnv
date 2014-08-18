package eu.unifiedviews.dataunit;

import eu.unifiedviews.dpu.DPU;

/**
 * Thrown by {@link DataUnit} to report problems with itself to {@link DPU} execution.
 */
public class DataUnitException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -8479349779218724204L;

    /**
     * {@inheritDoc}
     */
    public DataUnitException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataUnitException(String cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataUnitException(String message, Throwable cause) {
        super(message, cause);
    }

}
