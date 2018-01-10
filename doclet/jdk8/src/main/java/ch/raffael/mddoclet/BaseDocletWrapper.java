package ch.raffael.mddoclet;

import com.google.doclava.Doclava;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

import java.io.File;

/**
 * A wrapper for the {@link com.sun.javadoc.Doclet}.
 * Based on a fact whatever Doclava presents in classpath or not, forwards all calls to
 * <ul>
 *  <li>{@link Standard}, if Doclava is not found in classpath</li>
 *  <li>{@link Doclava}, if Doclava is in classpath</li>
 * </ul>
 *
 * Extra method {@link #maybeAdjustDestinationDir(File)} helps to set proper output folder.
 *
 * @author <a href="mailto:vladimir.grachev@gmail.com">Vladimir Grachev</a>
 */
class BaseDocletWrapper {

    private static final boolean isDoclava;

    /**
     * Checks whatever doclava is present in classpath
     *
     * @return `true` if doclava is in classpath, `false` otherwise.
     */
    private static boolean isDoclavaAvailable() {
        try {
            Class.forName("com.google.doclava.Doclava");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static {
        isDoclava = isDoclavaAvailable();
    }

    /**
     * Returns `true` if doclava can be used, `false` otherwise.
     *
     * @return `true` if doclava can be used, `false` otherwise.
     */
    private static boolean isDoclava() {
        return isDoclava;
    }

    /**
     * <p>Chooses and starts a base doclet, as specified by the Doclet specification.</p>
     * Base doclet can be on of the following:
     * <ul>
     *  <li>Standard (gets chosen by default)</li>
     *  <li>Doclava (gets chosen if doclava presents in classpath)</li>
     * </ul>
     *
     * @param rootDoc The root doc.
     *
     * @return `true`, if process was successful.
     *
     * @see com.sun.javadoc.Doclet#start(RootDoc)
     * @see com.google.doclava.Doclava#start(RootDoc)
     */
    static boolean start(RootDoc rootDoc) {
        if (isDoclava) {
            return Doclava.start(rootDoc);
        }
        return Standard.start(rootDoc);
    }

    /**
     * <p>Chooses and redirects call to a base doclet,
     * as specified by the Doclet specification.</p>
     * Base doclet can be on of the following:
     * <ul>
     *  <li>Standard (gets chosen by default)</li>
     *  <li>Doclava (gets chosen if doclava presents in classpath)</li>
     * </ul>
     *
     * @param option The option name.
     *
     * @return The length of the option.
     *
     * @see com.sun.javadoc.Doclet#start(RootDoc)
     * @see com.google.doclava.Doclava#start(RootDoc)
     */
    static int optionLength(String option) {
        if (isDoclava) {
            return Doclava.optionLength(option);
        }
        return Standard.optionLength(option);
    }

    /**
     * <p>Chooses and redirects call to a base doclet,
     * as specified by the Doclet specification.</p>
     * Base doclet can be on of the following:
     * <ul>
     *  <li>Standard (gets chosen by default)</li>
     *  <li>Doclava (gets chosen if doclava presents in classpath)</li>
     * </ul>
     *
     * @param options       The command line options.
     * @param errorReporter An error reporter to print errors.
     *
     * @return `true`, if the options are valid.
     *
     * @see com.sun.javadoc.Doclet#start(RootDoc)
     * @see com.google.doclava.Doclava#start(RootDoc)
     */
    static boolean validOptions(String[][] options, DocErrorReporter errorReporter) {
        if (isDoclava) {
            return Doclava.validOptions(options, errorReporter);
        }
        return Standard.validOptions(options, errorReporter);
    }

    /**
     * Adjusts destination dir based on which base doclet is used.
     * Base doclet can be on of the following:
     * <ul>
     *  <li>Standard (gets chosen by default)</li>
     *  <li>Doclava (gets chosen if doclava presents in classpath)</li>
     * </ul>
     *
     * Doclava places generated javadocs into "reference" subfolder,
     * having destination dir mach it is especially important for
     * UML diagrams generation.
     *
     * @param original       The command line options.
     *
     * @return `original` if `Standard` doclet is used,
     * `original/reference` if `Doclava` is used.
     *
     * @see com.sun.javadoc.Doclet#start(RootDoc)
     * @see com.google.doclava.Doclava#start(RootDoc)
     */
    static File maybeAdjustDestinationDir(File original) {
        if (isDoclava()) {
            return new File(original, "reference");
        }
        return original;
    }
}
