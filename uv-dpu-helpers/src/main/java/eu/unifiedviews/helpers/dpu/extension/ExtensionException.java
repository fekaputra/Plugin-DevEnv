package eu.unifiedviews.helpers.dpu.extension;

/**
 * Used to report problem in/with {@link Addon}.
 *
 * @author Škoda Petr
 */
public class ExtensionException extends Exception {

    public ExtensionException(String message) {
        super(message);
    }

    public ExtensionException(String format, Object ... params) {
        super(String.format(format, params));
    }

    public ExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

}
