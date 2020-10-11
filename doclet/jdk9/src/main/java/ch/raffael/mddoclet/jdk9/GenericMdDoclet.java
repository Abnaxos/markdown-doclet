package ch.raffael.mddoclet.jdk9;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import ch.raffael.mddoclet.jdk9.doctree.DocletEnvironmentProxy;
import ch.raffael.nullity.NotNull;


/**
 * A generic implementation of a doclet that processes Markdown doc
 * comments. Extending classes just have to instantiate the real doclet.
 *
 * This can't be an option the `javadoc` tool because the tool will first
 * call [[#getSupportedOptions()]], only after that, the doclet gets to
 * process the options. However, at the time [[#getSupportedOptions()]] is
 * called, the proxy needs to know the real doclet for querying its
 * supported options.
 *
 * @author Raffael Herzog
 */
public class GenericMdDoclet implements Doclet {

    private final static SourceVersion SUPPORTED_SOURCE_VERSION = SourceVersion.RELEASE_9;
    private final Doclet delegate;

    protected GenericMdDoclet(Doclet delegate) {
        this.delegate = delegate;
    }

    @Override
    public void init(@NotNull Locale locale, @NotNull Reporter reporter) {
        delegate.init(locale, reporter);
    }

    @Override
    public String getName() {
        return delegate.getName()+"$Markdown";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        // FIXME: Not implemented
        return Collections.emptySet();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        if (delegate.getSupportedSourceVersion().compareTo(SUPPORTED_SOURCE_VERSION) < 0) {
            return delegate.getSupportedSourceVersion();
        } else {
            return SUPPORTED_SOURCE_VERSION;
        }
    }

    @Override
    public boolean run(@NotNull DocletEnvironment environment) {
        return delegate.run(new DocletEnvironmentProxy(environment));
    }
}
