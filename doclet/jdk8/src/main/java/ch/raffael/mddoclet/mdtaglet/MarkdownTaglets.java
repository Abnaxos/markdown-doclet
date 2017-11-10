/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
 *
 * This file is part of markdown-doclet.
 *
 * markdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with markdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package ch.raffael.mddoclet.mdtaglet;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.sun.javadoc.DocErrorReporter;

import static ch.raffael.mddoclet.mdtaglet.MarkdownTaglet.OPT_MD_TAGLET_OPTION_PREFIX;

/**
 * # MarkdownTaglets contains the glue code for handling the {@link MarkdownTaglet}s.
 *
 * + It's a singleton.
 * + It handles the registration and initialization of {@link MarkdownTaglet}s.
 * + It handles the options.
 *
 * @todo Singletons are bad!
 */
public final class MarkdownTaglets {
    private static MarkdownTaglets INSTANCE = createInstance();

    private final Set<Class<? extends MarkdownTaglet>> markdownTagletClasses=new HashSet<>();
    private final List<MarkdownTaglet> markdownTaglets=new LinkedList<>();

    private final Multimap<String,String> options=MultimapBuilder.<String,String>hashKeys().arrayListValues().build();

    private final MarkdownTagletExecutor executor;
    private boolean initialized =false;
    private ErrorHandlerImpl errorHandler;

    private MarkdownTaglets(MarkdownTagletExecutor executor) {
        this.executor = executor;
    }

    /**
     * # Returns the instance.
     * @return THE INSTANCE
     */
    public static MarkdownTaglets instance() {
        return INSTANCE;
    }

    /**
     * # Resets the instances (clear all option and registered.
     *
     * Useful for tests.
     */
    public static void reset() {
        INSTANCE = createInstance();
    }

    private static MarkdownTaglets createInstance() {
        final MarkdownTaglets markdownTaglets = new MarkdownTaglets(new MarkdownTagletExecutor());
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if ( loader == null ) {
            loader = MarkdownTaglets.class.getClassLoader();
        }
        markdownTaglets.loadTagletPrototypes(loader);
        return markdownTaglets;
    }

    private void loadTagletPrototypes(ClassLoader classLoader) {
        ServiceLoader<MarkdownTaglet> tagletsLoader = ServiceLoader.load(MarkdownTaglet.class, MarkdownTaglet.class.getClassLoader());
        for ( MarkdownTaglet prototype : tagletsLoader ) {
            markdownTagletClasses.add(prototype.getClass());
            markdownTaglets.add(prototype);
        }
    }

    /**
     * # Return the option length of a markdown taglet specific option or 0.
     * @param option the option
     * @return the option length
     */
    public static int optionLengths(String option) {
        if( option.startsWith(MarkdownTaglet.OPT_MD_TAGLET_OPTION_PREFIX) ) {
            return 2;
        }

        return 0;
    }


    /**
     * # Handle the (potential) markdown options.
     *
     * @param options the options
     * @param errorReporter the error reporter
     * @return {@code true} if a markdown option has been found, otherwise false
     *
     * @see MarkdownTaglet#OPT_MD_TAGLET_OPTION_PREFIX
     * @see #optionLengths(String)
     */
    public boolean handleOptions(String[] options, DocErrorReporter errorReporter) {
        final String potentialMarkdownTagletOption = options[0];

        if( potentialMarkdownTagletOption.startsWith(OPT_MD_TAGLET_OPTION_PREFIX) ) {
            storeMarkdownTagletOption(potentialMarkdownTagletOption, options[1]);
            return true;
        }

        return false;
    }

    /**
     * # Makes the error reporter available to the markdown taglets.
     * @param errorReporter the error reporter.
     */
    public void setDocErrorReporter(DocErrorReporter errorReporter) {
        this.errorHandler = new ErrorHandlerImpl(errorReporter);
        this.executor.setErrorHandler(errorHandler);
    }

    /**
     * # Applies the registered {@link MarkdownTaglet}s on the markup.
     * @param markup the markup.
     * @return the markdown.
     */
    public String apply(String markup) {
        if(!initialized) {
            doInitExecutor();
            initialized = true;
        }

        return executor.apply(markup);
    }

    private void storeMarkdownTagletOption(String markdownTagletOption, String markdownTagletOptionValue) {
        final String stripped=markdownTagletOption.replace(OPT_MD_TAGLET_OPTION_PREFIX,"");
        options.put(stripped, markdownTagletOptionValue);
        options.put(markdownTagletOption, markdownTagletOptionValue);
    }

    private void doInitExecutor() {
        for (MarkdownTaglet markdownTaglet : markdownTaglets) {
            try {
                executor.register(setup(markdownTaglet));
            } catch (Exception e) {
                this.errorHandler.afterOptionsSetError(markdownTaglet, e);
                e.printStackTrace();
            }
        }
    }

    private MarkdownTaglet setup(MarkdownTaglet markdownTaglet) throws Exception {
        final List<Method> methods = resolveOptionMethods(markdownTaglet);
        for (Method method : methods) {
            final MarkdownTaglet.Option annotation = method.getAnnotation(MarkdownTaglet.Option.class);
            for (String value : options.get(annotation.value())) {
                invokeOptionMethod(markdownTaglet, method, value);
            }
        }
        markdownTaglet.afterOptionsSet();
        return markdownTaglet;
    }

    private void invokeOptionMethod(MarkdownTaglet markdownTaglet, Method method, String value) {
        try {
            method.invoke(markdownTaglet, value);
        } catch (Exception e) {
            this.errorHandler.optionsSetError(markdownTaglet, e.getCause());
            e.printStackTrace();
        }
    }

    private List<Method> resolveOptionMethods(MarkdownTaglet markdownTaglet) {
        final Class<? extends MarkdownTaglet> mdc = markdownTaglet.getClass();
        final Method[] methods = mdc.getMethods();
        List<Method> methodList=new ArrayList<>(methods.length);
        for (Method method : methods) {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length==1 && parameterTypes[0]==String.class && method.isAnnotationPresent(MarkdownTaglet.Option.class) ) {
                methodList.add(method);
            }

        }
        return methodList;
    }

    /**
     * The implementation of {@link MarkdownTagletErrorHandler} using {@link DocErrorReporter}.
     */
    private static class ErrorHandlerImpl implements MarkdownTagletErrorHandler {
        private final DocErrorReporter errorReporter;

        private ErrorHandlerImpl(DocErrorReporter errorReporter) {
            this.errorReporter = errorReporter;
        }

        @Override
        public void overrideMarkdownTaglet(MarkdownTaglet oldMarkdownTaglet, MarkdownTaglet newMarkdownTaglet) {
            errorReporter.printWarning(
                    MessageFormat.format("Override taglet {0}. Old taglet class={1}, new taglet class={2}",
                            oldMarkdownTaglet.getName(),
                            oldMarkdownTaglet.getClass().getName(),
                            newMarkdownTaglet.getClass().getName()
                    )
            );
        }

        @Override
        public void invalidTagletArguments(MarkdownTaglet markdownTaglet, String errorDescription) {
            final String description = markdownTaglet.getDescription();
            if (description.isEmpty()) {
                errorReporter.printWarning(
                        MessageFormat.format("Invalid taglet arguments for taglet {0}: {1}",
                                markdownTaglet.getName(),
                                errorDescription
                        )
                );
            } else {
                errorReporter.printWarning(
                        MessageFormat.format("Invalid taglet arguments for taglet {0}: {1}\n\nTaglet''s description: {2}",
                                markdownTaglet.getName(),
                                errorDescription,
                                description
                        )
                );

            }
        }

        @Override
        public void caughtUnexpectedException(MarkdownTaglet markdownTaglet, String tag, Exception exception) {
            errorReporter.printWarning(
                    MessageFormat.format("Caught unexpected exception ({1}) for taglet {0}: {2}\n\nTag: {3}",
                            markdownTaglet.getName(),
                            exception.getClass().getName(),
                            exception.getMessage(),
                            tag
                    )
            );
        }

        @Override
        public void afterOptionsSetError(MarkdownTaglet markdownTaglet, Exception exception) {
            errorReporter.printError(
                    MessageFormat.format("After options set: Caught exception ({1}) for taglet {0}: {2}\n\n{3}",
                            markdownTaglet.getName(),
                            exception.getClass().getName(),
                            exception.getMessage(),
                            "The taglet will not be applied."
                    )
            );

        }

        @Override
        public void optionsSetError(MarkdownTaglet markdownTaglet, Throwable exception) {
            errorReporter.printWarning(
                    MessageFormat.format("Options set: Caught exception ({1}) for taglet {0}: {2}\n\n{3}",
                            markdownTaglet.getName(),
                            exception.getClass().getName(),
                            exception.getMessage(),
                            "The taglet is no correctly set, but still applicable."
                    )
            );

        }
    }

}
