package eu.unifiedviews.helpers.dpu.extension.files.simple;

import eu.unifiedviews.dpu.DPUException;

/**
 *
 * @author Škoda Petr
 */
public class SimpleFilesException extends DPUException {

    public SimpleFilesException(String message) {
        super(message);
    }

    public SimpleFilesException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleFilesException(Throwable cause) {
        super(cause);
    }

}
