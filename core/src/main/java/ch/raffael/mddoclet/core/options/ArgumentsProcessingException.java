package ch.raffael.mddoclet.core.options;

/**
 * Runtime exception thrown when a runtime error occurs during options
 * processing. This is usually a programming error.
 *
 * @author Raffael Herzog
 */
public class ArgumentsProcessingException extends RuntimeException {

    public ArgumentsProcessingException() {
        super();
    }

    public ArgumentsProcessingException(String message) {
        super(message);
    }

    public ArgumentsProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentsProcessingException(Throwable cause) {
        super(cause);
    }
}
