package ch.raffael.mddoclet.core.options;

/**
 * Exception thrown when something's wrong with the options. This is usually
 * a user error and should be handled gracefully (i.e. nice,
 * human-presentable message).
 *
 * @author Raffael Herzog
 */
public class InvalidOptionArgumentsException extends Exception {

    public InvalidOptionArgumentsException() {
        super();
    }

    public InvalidOptionArgumentsException(String message) {
        super(message);
    }

    public InvalidOptionArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOptionArgumentsException(Throwable cause) {
        super(cause);
    }
}
