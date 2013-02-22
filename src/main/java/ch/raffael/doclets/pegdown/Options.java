/*
 * Copyright 2013 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.raffael.doclets.pegdown;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Ordering;
import com.sun.javadoc.DocErrorReporter;
import com.sun.tools.doclets.standard.Standard;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;

import static com.google.common.base.Objects.*;


/**
 * Processes and stores the command line options.
 *
 * @author <p><a href="mailto:herzog@raffael.ch">Raffael Herzog</a></p>
 */
public class Options {

    private static final Pattern LINE_START = Pattern.compile("^ ", Pattern.MULTILINE);

    /**
     * The default extensions for Pegdown. This includes the following extensions:
     *
     * * {@link Extensions#AUTOLINKS}
     * * {@link Extensions#DEFINITIONS}
     * * {@link Extensions#SMARTYPANTS}
     * * {@link Extensions#TABLES}
     * * {@link Extensions#WIKILINKS}
     */
    public static final int DEFAULT_PEGDOWN_EXTENSIONS =
            Extensions.AUTOLINKS
            | Extensions.DEFINITIONS
            | Extensions.FENCED_CODE_BLOCKS
            | Extensions.SMARTYPANTS
            | Extensions.TABLES
            | Extensions.WIKILINKS;

    private String[][] forwardedOptions = new String[0][];

    private Integer pegdownExtensions = null;
    private File overviewFile = null;
    private Charset encoding = null;

    private LinkRenderer linkRenderer = null;
    private PegDownProcessor processor = null;

    private Set<Integer> consumedOptions = new HashSet<>();

    public Options() {
    }

    /**
     * Retrieves the options to be forwarded to the standard Doclet.
     *
     * @return The options for the standard Doclet.
     */
    public String[][] forwardedOptions() {
        return forwardedOptions;
    }

    /**
     * Loads the options from the command line.
     *
     * @param options          The command line options.
     * @param errorReporter    The error reporter for printing messages.
     *
     * @return `true` if the options are valid.
     */
    public boolean load(String[][] options, DocErrorReporter errorReporter) {
        this.forwardedOptions = options;
        consumedOptions.clear();
        for ( int i = 0; i < options.length; i++ ) {
            String[] opt = options[i];
            if ( opt[0].equals("-extensions") ) {
                if ( pegdownExtensions != null ) {
                    errorReporter.printError("Only one -extensions option allowed");
                    return false;
                }
                try {
                    setPegdownExtensions(toExtensions(opt[1]));
                }
                catch ( IllegalArgumentException e ) {
                    errorReporter.printError(e.getMessage());
                    return false;
                }
                consumeOption(i);
            }
            else if ( opt[0].equals("-encoding") ) {
                try {
                    encoding = Charset.forName(opt[1]);
                }
                catch ( IllegalCharsetNameException e ) {
                    errorReporter.printError("Illegal charset: " + opt[1]);
                    return false;
                }
            }
            else if ( opt[0].equals("-overview") ) {
                if ( getOverviewFile() != null ) {
                    errorReporter.printError("-overview may only be specified once");
                    return false;
                }
                setOverviewFile(new File(opt[1]));
                consumeOption(i);
            }
        }
        if ( !customLoad(options, errorReporter) ) {
            return false;
        }
        if ( pegdownExtensions == null ) {
            setPegdownExtensions(DEFAULT_PEGDOWN_EXTENSIONS);
        }
        if ( !consumedOptions.isEmpty() ) {
            ArrayList<String[]> consuming = new ArrayList<>(Arrays.asList(options));
            for ( int i : Ordering.natural().reverse().sortedCopy(consumedOptions) ) {
                consuming.remove(i);
            }
            forwardedOptions = consuming.toArray(new String[consuming.size()][]);
            consumedOptions.clear();
        }
        return Standard.validOptions(forwardedOptions, errorReporter);
    }

    /**
     * Hook to do some custom option processing.
     *
     * @param options          The command line options.
     * @param errorReporter    An error reporter for printing messages.
     *
     * @return `true` if the options are valid.
     */
    protected boolean customLoad(String[][] options, DocErrorReporter errorReporter) {
        return true;
    }

    /**
     * Consumes an option. The option will then be excluded from the result of
     * {@link #forwardedOptions()}.
     *
     * @param index    The index of the consumed option.
     */
    protected void consumeOption(int index) {
        consumedOptions.add(index);
    }

    /**
     * Gets the Pegdown extension flags.
     *
     * @return The Pegdown extension flags.
     *
     * @see Extensions
     */
    public int getPegdownExtensions() {
        return pegdownExtensions != null ? pegdownExtensions : DEFAULT_PEGDOWN_EXTENSIONS;
    }

    /**
     * Sets the Pegdown extension flags.
     *
     * @param pegdownExtensions    The Pegdown extension flags.
     *
     * @see Extensions
     */
    public void setPegdownExtensions(int pegdownExtensions) {
        this.pegdownExtensions = pegdownExtensions;
        processor = null;
    }

    /**
     * Gets the overview file.
     *
     * @return The overview file.
     */
    public File getOverviewFile() {
        return overviewFile;
    }

    /**
     * Sets the overview file.
     *
     * @param overviewFile The overview file.
     */
    public void setOverviewFile(File overviewFile) {
        this.overviewFile = overviewFile;
    }

    /**
     * Gets the source encoding.
     *
     * @return The source encoding.
     */
    public Charset getEncoding() {
        return firstNonNull(encoding, Charset.defaultCharset());
    }

    /**
     * Sets the source encoding.
     *
     * @param encoding The source encoding.
     */
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    /**
     * Gets the link renderer.
     *
     * @return The link renderer.
     */
    public LinkRenderer getLinkRenderer() {
        if ( linkRenderer == null ) {
            linkRenderer = new DocletLinkRenderer();
        }
        return linkRenderer;
    }

    /**
     * Sets the link renderer.
     *
     * @param linkRenderer The link renderer.
     */
    public void setLinkRenderer(LinkRenderer linkRenderer) {
        this.linkRenderer = linkRenderer;
    }

    /**
     * Converts Markdown source to HTML according to this options object. Leading spaces
     * will be fixed.
     *
     * @param markup    The Markdown source.
     *
     * @return The resulting HTML.
     *
     * @see #toHtml(String, boolean)
     */
    public String toHtml(String markup) {
        return toHtml(markup, true);
    }

    /**
     * Converts Markdown source to HTML according to this options object. If
     * `fixLeadingSpaces` is `true`, exactly one leading whitespace character ('\\u0020')
     * will be removed, if it exists.
     *
     * @param markup           The Markdown source.
     * @param fixLeadingSpaces `true` if leading spaces should be fixed.
     *
     * @return The resulting HTML.
     */
    public String toHtml(String markup, boolean fixLeadingSpaces) {
        if ( processor == null ) {
            processor = createProcessor();
        }
        if ( fixLeadingSpaces ) {
            markup = LINE_START.matcher(markup).replaceAll("");
        }
        return processor.markdownToHtml(markup, getLinkRenderer());
    }

    /**
     * Create a new processor. If you need to further customise the markup processing,
     * you can override this method.
     *
     * @return A (possibly customised) Pegdown processor.
     */
    protected PegDownProcessor createProcessor() {
        return new PegDownProcessor(firstNonNull(pegdownExtensions, DEFAULT_PEGDOWN_EXTENSIONS));
    }

    public static int optionLength(String option) {
        if ( option.equals("-extensions") ) {
            return 2;
        }
        else {
            return Standard.optionLength(option);
        }
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param options          The command line options.
     * @param errorReporter    An error reporter to print messages.
     *
     * @return `true` if the options are valid.
     *
     * @see com.sun.javadoc.Doclet#validOptions(String[][], com.sun.javadoc.DocErrorReporter)
     */
    public static boolean validOptions(String[][] options, DocErrorReporter errorReporter) {
        return new Options().load(options, errorReporter);
    }

    /**
     * Convert a comma separated list of extension names to an int. Each name is
     * converted to upper case, any '-' is replaced by '_'. The result is expected to
     * be a flag from {@link Extensions}.
     *
     * @param extensions    A comma separated list of PegDown extensions.
     *
     * @return An int represi
     */
    public static int toExtensions(String extensions) {
        int result = 0;
        for ( String ext : Splitter.on(',').trimResults().omitEmptyStrings().split(extensions) ) {
            try {
                Field f = Extensions.class.getField(ext.replace('-', '_').toUpperCase());
                result |= (int)f.get(null);
            }
            catch ( NoSuchFieldException e ) {
                throw new IllegalArgumentException("No such extension: " + ext);
            }
            catch ( IllegalAccessException e ) {
                throw new IllegalArgumentException("Cannot read int value for extension " + ext + ": " + e, e);
            }
        }
        return result;
    }

}
