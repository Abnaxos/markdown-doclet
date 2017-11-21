package ch.raffael.mddoclet.core.util;

import ch.raffael.nullity.Nullable;


/**
 * TODO: 20.11.17 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class Exceptions {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private Exceptions() {
        throw new AssertionError("Utility class: " + getClass().getName());
    }

    public static String formatMessage(String baseMessage, @Nullable String userMessage) {
        return formatMessage(baseMessage, userMessage, EMPTY_ARGS);
    }

    public static String formatMessage(String baseMessage, @Nullable String userMessage, Object... args) {
        StringBuilder message = new StringBuilder(String.format(baseMessage, args));
        if (userMessage != null) {
            message.append(": ").append(userMessage);
        }
        return message.toString();
    }

}
