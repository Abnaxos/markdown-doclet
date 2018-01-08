package ch.raffael.mddoclet.core.options;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Marks a method that takes command line options.
 *
 * @see ReflectedOptions
 *
 * @author Raffael Herzog
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptionConsumer {

    String[] names() default {};

    String description() default "";

    String parameters() default "";

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Converter {
        Class<ArgumentConverter<?>> value();
    }

}
