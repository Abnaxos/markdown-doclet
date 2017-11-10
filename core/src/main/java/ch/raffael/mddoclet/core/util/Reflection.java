package ch.raffael.mddoclet.core.util;

/**
 * TODO: 10.11.17 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class Reflection {

    private Reflection() {
        throw new AssertionError("Utility class: " + getClass().getName());
    }

    public static String shortClassName(Class<?> ofClass) {
        if ( ofClass.getEnclosingClass() == null ) {
            return ofClass.getSimpleName();
        } else {
            return shortClassName(new StringBuilder(), ofClass).toString();
        }
    }

    public static StringBuilder shortClassName(StringBuilder buf, Class<?> ofClass) {
        if ( ofClass.getEnclosingClass() == null ) {
            buf.append(ofClass.getSimpleName());
        } else {
            shortClassName(buf, ofClass.getEnclosingClass()).append('.').append(ofClass.getSimpleName());
        }
        return buf;
    }

}
