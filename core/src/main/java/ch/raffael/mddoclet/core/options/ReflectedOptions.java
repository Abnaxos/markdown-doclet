package ch.raffael.mddoclet.core.options;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import ch.raffael.mddoclet.core.util.ReflectionException;


/**
 * Provides the tools to scan objects/methods for the {@link OptionConsumer @Option}
 * annotation and return option processors or descriptors for it.
 *
 * @author Raffael Herzog
 */
public final class ReflectedOptions {

    private ReflectedOptions() {
        throw new AssertionError("Utility class: " + getClass().getName());
    }

    public static List<OptionDescriptor> descriptorsForObject(Object target) {
        List<OptionDescriptor> descriptors = new ArrayList<>();
        for (Method method : target.getClass().getMethods()) {
            if (method.isAnnotationPresent(OptionConsumer.class)) {
                descriptors.add(descriptorForMethod(target, method));
            }
        }
        return descriptors;
    }

    public static OptionDescriptor descriptorForMethod(Object target, Method method) {
        OptionConsumer annotation = method.getAnnotation(OptionConsumer.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Method " + method + " not annotated with @" + OptionConsumer.class.getSimpleName());
        }
        return OptionDescriptor.builder()
                .names(annotation.names())
                .description(annotation.description())
                .parameters(annotation.parameters())
                .argumentCount(method.getParameterCount())
                .processor(processorForMethod(target, method))
                .build();
    }

    public static OptionProcessor processorForMethod(Object target, Method method) {
        List<ArgumentConverter<?>> converters = new ArrayList<>(method.getParameterCount());
        for (Parameter parameter : method.getParameters()) {
            ArgumentConverter<?> converter;
            OptionConsumer.Converter converterAnnotation = parameter.getAnnotation(OptionConsumer.Converter.class);
            if (converterAnnotation != null) {
                try {
                    converter = converterAnnotation.value().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ReflectionException("Error instantiating converter for parameter " + parameter + " method " + method);
                }
            } else {
                converter = StandardArgumentConverters.forType(parameter.getParameterizedType());
                if (converter == null) {
                    throw new ReflectionException("No argument converter found for parameter " + parameter.getName() + " of " + method);
                }
            }
            converters.add(converter);
        }
        return (name, arguments) -> {
            if (arguments.size() != converters.size()) {
                throw new InvalidOptionArgumentsException("Unexpected argument count: " + arguments.size() + "!=" + converters.size() + "(expeted)");
            }
            Object[] methodArguments = new Object[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                methodArguments[i] = converters.get(i).convert(arguments.get(i));
            }
            try {
                method.invoke(target, methodArguments);
            } catch (IllegalAccessException e) {
                throw new ArgumentsProcessingException(e);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof InvalidOptionArgumentsException) {
                    throw (InvalidOptionArgumentsException)e.getTargetException();
                } else {
                    throw new ArgumentsProcessingException(e);
                }
            }
        };
    }

}
