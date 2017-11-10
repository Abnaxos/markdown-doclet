package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * The annotated element must not be null.
 * <p>
 * Annotated fields must not be null after construction has completed.
 * <p>
 * When this annotation is applied to a method it applies to the method return value.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnull {
}
