/*
 * Copyright 2013-2016 Raffael Herzog / Marko Umek
 *
 * This file is part of pegdown-doclet.
 *
 * pegdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pegdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pegdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.raffael.doclets.pegdown;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.sun.javadoc.*;
import com.sun.tools.doclets.standard.Standard;
import com.sun.tools.javadoc.Main;
import org.parboiled.errors.ParserRuntimeException;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

import static ch.raffael.doclets.pegdown.Options.DEFAULT_PEGDOWN_EXTENSIONS;
import static com.google.common.base.MoreObjects.firstNonNull;


/**
 * The Doclet implementation. This implementation uses [Pegdown](http://www.pegdown.org/)
 * to process the JavaDoc comments and tags, and sets a new JavaDoc comment using
 * {@link Doc#setRawCommentText(String)}. It then passes the `RootDoc` to the standard
 * Doclet.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 * @see "[The Doclet Specification](http://docs.oracle.com/javase/1.5.0/docs/guide/javadoc/doclet/spec/index.html)"
 */
public class PegdownDoclet implements DocErrorReporter {

    public static final String HIGHLIGHT_JS_HTML =
            "<script type=\"text/javascript\" src=\"" + "{@docRoot}/highlight.pack.js" + "\"></script>\n"
            + "<script type=\"text/javascript\"><!--\nhljs.initHighlightingOnLoad();\n//--></script>";

    private static final Pattern LINE_START = Pattern.compile("^ ", Pattern.MULTILINE);

    private final Map<String, TagRenderer<?>> tagRenderers = new HashMap<>();

    private final Set<PackageDoc> packages = new HashSet<>();
    private final Options options;
    private final RootDoc rootDoc;

    private boolean error = false;

    private LinkRenderer linkRenderer = null;
    private PegDownProcessor processor = null;


    /**
     * Construct a new Pegdown Doclet.
     *
     * @param options The command line options.
     * @param rootDoc The root document.
     */
    public PegdownDoclet(Options options, RootDoc rootDoc) {
        this.options = options;
        this.rootDoc = rootDoc;
        tagRenderers.put("@author", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@version", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@return", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@deprecated", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@since", SimpleTagRenderer.INSTANCE);
        tagRenderers.put("@param", ParamTagRenderer.INSTANCE);
        tagRenderers.put("@throws", ThrowsTagRenderer.INSTANCE);
        tagRenderers.put("@see", SeeTagRenderer.INSTANCE);
        UmlTagRenderer umlTagRenderer = new UmlTagRenderer();
        tagRenderers.put("@uml", umlTagRenderer);
        tagRenderers.put("@startuml", umlTagRenderer);
        tagRenderers.put("@enduml", TagRenderer.ELIDE);
        tagRenderers.put("@todo", new TodoTagRenderer());
    }

    /**
     * As specified by the Doclet specification.
     *
     * @return Java 1.5.
     *
     * @see com.sun.javadoc.Doclet#languageVersion()
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param option The option name.
     *
     * @return The length of the option.
     *
     * @see com.sun.javadoc.Doclet#optionLength(String)
     */
    public static int optionLength(String option) {
        return Options.optionLength(option);
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param rootDoc The root doc.
     *
     * @return `true`, if process was successful.
     *
     * @see com.sun.javadoc.Doclet#start(RootDoc)
     */
    public static boolean start(RootDoc rootDoc) {
        Options options = new Options();
        String[][] forwardedOptions = options.load(rootDoc.options(), rootDoc);
        if ( forwardedOptions == null ) {
            return false;
        }
        PegdownDoclet doclet = new PegdownDoclet(options, rootDoc);
        doclet.process();
        if ( doclet.isError() ) {
            return false;
        }
        RootDocWrapper rootDocWrapper = new RootDocWrapper(rootDoc, forwardedOptions);
        if ( options.isHighlightEnabled() ) {
            // find the footer option
            int i = 0;
            for ( ; i < rootDocWrapper.options().length; i++ ) {
                if ( rootDocWrapper.options()[i][0].equals("-footer") ) {
                    rootDocWrapper.options()[i][1] += HIGHLIGHT_JS_HTML;
                    break;
                }
            }
            if ( i >= rootDocWrapper.options().length ) {
                rootDocWrapper.appendOption("-footer", HIGHLIGHT_JS_HTML);
            }
        }
        return Standard.start(rootDocWrapper) && doclet.postProcess();
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param options       The command line options.
     * @param errorReporter An error reporter to print errors.
     *
     * @return `true`, if the options are valid.
     */
    public static boolean validOptions(String[][] options, DocErrorReporter errorReporter) {
        return Options.validOptions(options, errorReporter);
    }

    /**
     * Removes all tag renderers.
     */
    public void clearTagRenderers() {
        tagRenderers.clear();
    }

    /**
     * Adds a tag renderer for the specified {@link com.sun.javadoc.Tag#kind() kind}.
     *
     * @param kind        The kind of the tag the renderer renders.
     * @param renderer    The tag renderer.
     */
    public void addTagRenderer(String kind, TagRenderer<?> renderer) {
        tagRenderers.put(kind, renderer);
    }

    /**
     * Removes a tag renderer for the specified {@link com.sun.javadoc.Tag#kind() kind}.
     *
     * @param kind        The kind of the tag.
     */
    public void removeTagRenderer(String kind) {
        tagRenderers.remove(kind);
    }

    /**
     * Get the options.
     *
     * @return The options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Get the root doc.
     *
     * @return The root doc.
     */
    public RootDoc getRootDoc() {
        return rootDoc;
    }

    /**
     * Process the documentation tree. If any errors occur during processing,
     * {@link #isError()} will return `true` afterwards.
     */
    public void process() {
        processOverview();
        for ( ClassDoc doc : rootDoc.classes() ) {
            packages.add(doc.containingPackage());
            processClass(doc);
        }
        for ( PackageDoc doc : packages ) {
            processPackage(doc);
        }
    }

    /**
     * Called after the standard Doclet *successfully* did its work.
     *
     * @return `true` if postprocessing succeeded.
     */
    public boolean postProcess() {
        boolean success = true;
        if ( options.getStylesheetFile() == null ) {
            success &= copyResource(options.getJavadocVersion().getStylesheet(), "stylesheet.css", "CSS stylesheet");
        }
        if ( options.isHighlightEnabled() ) {
            success &= copyResource("highlight.pack.7.3.js", "highlight.pack.js", "highlight.js");
            success &= copyResource("highlight-LICENSE.txt", "highlight-LICENSE.txt", "highlight.js license");
            success &= copyResource("classref.txt", "classref.txt", "highlight.js class reference");
            success &= copyResource("highlight-styles/" + options.getHighlightStyle() + ".css", "highlight.css", "highlight.js style '" + options.getHighlightStyle() + "'");
        }
        else {
            success &= copyResource("no-highlight.css", "highlight.css", "no-highlight CSS");
        }
        return success;
    }

    private boolean copyResource(String resource, String destination, String description) {
        try (
                InputStream in = PegdownDoclet.class.getResourceAsStream(resource);
                OutputStream out = new FileOutputStream(new File(options.getDestinationDir(), destination))
        )
        {
            ByteStreams.copy(in, out);
            return true;
        }
        catch ( IOException e ) {
            printError("Error writing " + description + ": " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Check whether any errors occurred during processing of the documentation tree.
     *
     * @return `true` if there were errors processing the documentation tree.
     */
    public boolean isError() {
        return error;
    }

    /**
     * Process the overview file, if specified.
     */
    protected void processOverview() {
        if ( options.getOverviewFile() != null ) {
            try {
                rootDoc.setRawCommentText(Files.toString(options.getOverviewFile(), options.getEncoding()));
                defaultProcess(rootDoc, false);
            }
            catch ( IOException e ) {
                printError("Error loading overview from " + options.getOverviewFile() + ": " + e.getLocalizedMessage());
                rootDoc.setRawCommentText("");
            }
        }
    }

    /**
     * Process the class documentation.
     *
     * @param doc   The class documentation.
     */
    protected void processClass(ClassDoc doc) {
        defaultProcess(doc, true);
        for ( MemberDoc member : doc.fields() ) {
            processMember(member);
        }
        for ( MemberDoc member : doc.constructors() ) {
            processMember(member);
        }
        for ( MemberDoc member : doc.methods() ) {
            processMember(member);
        }
        if ( doc instanceof AnnotationTypeDoc ) {
            for ( MemberDoc member : ((AnnotationTypeDoc)doc).elements() ) {
                processMember(member);
            }
        }
    }

    /**
     * Process the member documentation.
     *
     * @param doc    The member documentation.
     */
    protected void processMember(MemberDoc doc) {
        defaultProcess(doc, true);
    }

    /**
     * Process the package documentation.
     *
     * @param doc    The package documentation.
     */
    protected void processPackage(PackageDoc doc) {
        // (#1) Set foundDoc to false if possible.
        // foundDoc will be set to true when setRawCommentText() is called, if the method
        // is called again, JavaDoc will issue a warning about multiple sources for the
        // package documentation. If there actually *are* multiple sources, the warning
        // has already been issued at this point, we will, however, use it to set the
        // resulting HTML. So, we're setting it back to false here, to suppress the
        // warning.
        try {
            Field foundDoc = doc.getClass().getDeclaredField("foundDoc");
            foundDoc.setAccessible(true);
            foundDoc.set(doc, false);
        }
        catch ( Exception e ) {
            printWarning(doc.position(), "Cannot suppress warning about multiple package sources: " + e + "\n"
                    + "Please report this at https://github.com/Abnaxos/pegdown-doclet/issues with the exact JavaDoc version you're using");
        }
        defaultProcess(doc, true);
    }

    /**
     * Default processing of any documentation node.
     *
     * @param doc              The documentation.
     * @param fixLeadingSpaces `true` if leading spaces should be fixed.
     *
     * @see #toHtml(String, boolean)
     */
    protected void defaultProcess(Doc doc, boolean fixLeadingSpaces) {
        try {
            StringBuilder buf = new StringBuilder();
            buf.append(toHtml(doc.commentText(), fixLeadingSpaces));
            buf.append('\n');
            for ( Tag tag : doc.tags() ) {
                processTag(tag, buf);
                buf.append('\n');
            }
            doc.setRawCommentText(buf.toString());
        }
        catch ( final ParserRuntimeException e ) {
            if ( doc instanceof RootDoc ) {
                printError(new SourcePosition() {
                    @Override
                    public File file() {
                        return options.getOverviewFile();
                    }
                    @Override
                    public int line() {
                        return 0;
                    }
                    @Override
                    public int column() {
                        return 0;
                    }
                }, e.getMessage());
            }
            else {
                printError(doc.position(), e.getMessage());
            }
        }
    }

    /**
     * Process a tag.
     *
     * @param tag      The tag.
     * @param target   The target string builder.
     */
    @SuppressWarnings("unchecked")
    protected void processTag(Tag tag, StringBuilder target) {
        TagRenderer<Tag> renderer = (TagRenderer<Tag>)tagRenderers.get(tag.kind());
        if ( renderer == null ) {
            renderer = TagRenderer.VERBATIM;
        }
        renderer.render(tag, target, this);
    }

    /**
     * Convert the given markup to HTML according to the {@link Options}.
     *
     * @param markup    The Markdown source.
     *
     * @return The resulting HTML.
     */
    public String toHtml(String markup) {
        return toHtml(markup, true);
    }

    /**
     * Convert the given markup to HTML according to the {@link Options}.
     *
     * @param markup            The Markdown source.
     * @param fixLeadingSpaces  `true` to strip one leading space if present.
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

        List<String> tags = new ArrayList<>();
        String html = createDocletSerializer().toHtml(processor.parseMarkdown(Tags.extractInlineTags(markup, tags).toCharArray()));
        return Tags.insertInlineTags(html, tags);
    }

    /**
     * Create a new processor. If you need to further customise the markup processing,
     * you can override this method.
     *
     * @return A (possibly customised) Pegdown processor.
     */
    protected PegDownProcessor createProcessor() {
        return new PegDownProcessor(firstNonNull(options.getPegdownExtensions(), DEFAULT_PEGDOWN_EXTENSIONS), options.getParseTimeout());
    }

    /**
     * Create a new serializer. If you need to further customize the HTML rendering, you
     * can override this method.
     *
     * @return A (possibly customised) ToHtmlSerializer.
     */
    protected ToHtmlSerializer createDocletSerializer() {
        return new DocletSerializer(this.options, getLinkRenderer());
    }

    /**
     * Gets the link renderer.
     *
     * @return The link renderer.
     */
    private LinkRenderer getLinkRenderer() {
        if ( linkRenderer == null ) {
            linkRenderer = new DocletLinkRenderer();
        }
        return linkRenderer;
    }


    /**
     * Indicate that an error occurred. This method will also be called by
     * {@link #printError(String)} and
     * {@link #printError(com.sun.javadoc.SourcePosition, String)}.
     */
    public void error() {
        error = true;
    }

    @Override
    public void printError(String msg) {
        error();
        rootDoc.printError(msg);
    }

    @Override
    public void printError(SourcePosition pos, String msg) {
        error();
        rootDoc.printError(pos, msg);
    }

    @Override
    public void printWarning(String msg) {
        rootDoc.printWarning(msg);
    }

    @Override
    public void printWarning(SourcePosition pos,
                             String msg)
    {
        rootDoc.printWarning(pos, msg);
    }

    @Override
    public void printNotice(String msg) {
        rootDoc.printNotice(msg);
    }

    @Override
    public void printNotice(SourcePosition pos, String msg) {
        rootDoc.printNotice(pos, msg);
    }

    /**
     * Returns a prefix for relative URLs from a documentation element relative to the
     * given package. This prefix can be used to refer to the root URL of the
     * documentation:
     *
     * ```java
     * doc = "<script type=\"text/javascript\" src=\""
     *     + rootUrlPrefix(classDoc.containingPackage()) + "highlight.js"
     *     + "\"></script>";
     * ```
     *
     * @param doc    The package containing the element from where to reference the root.
     *
     * @return A URL prefix for URLs referring to the doc root.
     */
    public String rootUrlPrefix(PackageDoc doc) {
        if ( doc == null || doc.name().isEmpty() ) {
            return "";
        }
        else {
            StringBuilder buf = new StringBuilder();
            buf.append("../");
            for ( int i = 0; i < doc.name().length(); i++ ) {
                if ( doc.name().charAt(i) == '.' ) {
                    buf.append("../");
                }
            }
            return buf.toString();
        }
    }

    /**
     * Just a main method for debugging.
     *
     * @param args The command line arguments.
     *
     * @throws Exception If anything goes wrong.
     */
    public static void main(String[] args) throws Exception {
        args = Arrays.copyOf(args, args.length + 2);
        args[args.length - 2] = "-doclet";
        args[args.length - 1] = PegdownDoclet.class.getName();
        Main.main(args);
    }

}
