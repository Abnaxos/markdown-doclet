package ch.raffael.mddoclet.jdk9;

import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import ch.raffael.nullity.NotNull;


/**
 * TODO: 02.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public class MdDoclet implements Doclet {

    @Override
    public void init(@NotNull Locale locale, @NotNull Reporter reporter) {

    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        // FIXME: Not implemented
        return null;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_9;
    }

    @Override
    public boolean run(@NotNull DocletEnvironment environment) {
        // FIXME: Not implemented
        return false;
    }
}
