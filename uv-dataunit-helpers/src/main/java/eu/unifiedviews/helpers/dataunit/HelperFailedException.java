package eu.unifiedviews.helpers.dataunit;

/**
 * Base exception used by helpers.
 * 
 * @author Škoda Petr
 */
public class HelperFailedException extends Exception {

    public HelperFailedException(String message) {
        super(message);
    }

    public HelperFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HelperFailedException(Throwable cause) {
        super(cause);
    }

}
