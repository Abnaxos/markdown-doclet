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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.standard.Standard;
import com.sun.tools.javadoc.Main;


/**
 * The Doclet implementation. This implementation uses [[http://www.pegdown.org/ Pegdown]]
 * to process the JavaDoc comments and tags, and sets a new JavaDoc comment using
 * {@link Doc#setRawCommentText(String)}. It then passes the `RootDoc` to the standard
 * Doclet.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 * @see "[[http://docs.oracle.com/javase/1.5.0/docs/guide/javadoc/doclet/spec/index.html The Doclet Specification]]"
 */
public class PegdownDoclet implements DocErrorReporter {

    private final Map<String, TagRenderer<?>> tagRenderers = new HashMap<>();

    private final Set<PackageDoc> packages = new HashSet<>();
    private final Options options;
    private final RootDoc rootDoc;

    private boolean error = false;

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
        tagRenderers.put("@todo", TodoTagRenderer.INSTANCE);
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
     * @see com.sun.javadoc.Doclet#start(com.sun.javadoc.RootDoc)
     */
    public static boolean start(RootDoc rootDoc) {
        Options options = new Options();
        if ( !options.load(rootDoc.options(), rootDoc) ) {
            return false;
        }
        PegdownDoclet doclet = new PegdownDoclet(options, rootDoc);
        doclet.process();
        if ( doclet.isError() ) {
            return false;
        }
        return Standard.start(rootDoc) && doclet.postProcess();
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
        return new Options().load(options, errorReporter);
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
            success &= copyResource("stylesheet.css", "stylesheet.css", "CSS stylesheet");
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
        defaultProcess(doc, true);
    }

    /**
     * Default processing of any documentation node.
     *
     * @param doc              The documentation.
     * @param fixLeadingSpaces `true` if leading spaces should be fixed.
     *
     * @see Options#toHtml(String, boolean)
     */
    protected void defaultProcess(Doc doc, boolean fixLeadingSpaces) {
        StringBuilder buf = new StringBuilder();
        if ( options.isHighlightEnabled() ) {
            installHighlightJS(doc, buf);
            buf.append('\n');
        }
        buf.append(getOptions().toHtml(doc.commentText(), fixLeadingSpaces));
        buf.append('\n');
        for ( Tag tag : doc.tags() ) {
            processTag(tag, buf);
            buf.append('\n');
        }
        doc.setRawCommentText(buf.toString());
    }

    /**
     * Adds the HTML required for highlight.js to the output.
     *
     * ```html
     * <script type="text/javascript" src="highlight.pack.js"></script>
     * <script type="text/javascript"><!--
     * hljs.initHighlightingOnLoad();
     * // -->
     * </script>
     * ```
     *
     *
     * @param doc    The documented element.
     * @param buf    A {@link StringBuilder} to add the HTML to.
     */
    protected void installHighlightJS(Doc doc, StringBuilder buf) {
        String prefix = null;
        if ( doc instanceof PackageDoc ) {
            prefix = rootUrlPrefix((PackageDoc)doc);
        }
        else if ( doc instanceof ClassDoc ) {
            prefix = rootUrlPrefix(((ClassDoc)doc).containingPackage());
        }
        else if ( doc instanceof RootDoc ) {
            prefix = rootUrlPrefix(null);
        }
        if ( prefix != null ) {
            buf.append("<script type=\"text/javascript\" src=\"")
                    .append(prefix).append("highlight.pack.js")
                    .append("\"></script>\n")
                    .append("<script type=\"text/javascript\"><!--\nhljs.initHighlightingOnLoad();\n//--></script>");
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
        return options.toHtml(markup);
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
