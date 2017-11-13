package ch.raffael.mddoclet.core.util;

/**
 * Exception thrown when some unexpected reflection error occurs. This
 * usually indicates programming errors.
 *
 * @author Raffael Herzog
 */
public class ReflectionException extends RuntimeException {

    public ReflectionException() {
        super();
    }

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
