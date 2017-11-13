package ch.raffael.mddoclet.core.options;


/**
 * Convert a single argument to a Java object.
 *
 * @author Raffael Herzog
 */
public interface ArgumentConverter<T> {

    T convert(String argument) throws InvalidOptionArgumentsException;

}
