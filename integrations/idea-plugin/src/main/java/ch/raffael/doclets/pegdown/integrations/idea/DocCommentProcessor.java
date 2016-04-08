/*
 * Copyright 2013 Raffael Herzog
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
package ch.raffael.doclets.pegdown.integrations.idea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.MoreObjects;
import com.intellij.codeInsight.javadoc.JavaDocUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDocCommentOwner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.javadoc.PsiInlineDocTag;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import net.sourceforge.plantuml.SourceStringReader;
import org.pegdown.LinkRenderer;
import org.pegdown.ToHtmlSerializer;

import ch.raffael.doclets.pegdown.DocletSerializer;
import ch.raffael.doclets.pegdown.Options;
import ch.raffael.doclets.pegdown.PegdownDoclet;
import ch.raffael.doclets.pegdown.SeeTagRenderer;
import ch.raffael.doclets.pegdown.TagRendering;


/**
 * The work-horse, renders the JavaDoc comments using Pegdown and creates a new
 * PsiDocComment that can be passed to IDEA's default QuickDoc implementation.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DocCommentProcessor {

    private static final Logger LOG = Logger.getInstance(DocCommentProcessor.class);

    private final static Pattern LEADING_ASTERISK_RE = Pattern.compile("^[ \\t]*\\*+ ?", Pattern.MULTILINE);
    public static final Pattern ABSOLUTE_IMG_RE = Pattern.compile("([a-zA-Z0-9+.-]+:|/).*");

    private final PsiFile file;
    private final Project project;
    private final PsiElementFactory psiElementFactory;
    private final PegdownOptions.RenderingOptions pegdownOptions;

    public DocCommentProcessor(PsiFile file) {
        this.file = file;
        if ( file == null ) {
            project = null;
            pegdownOptions = null;
            psiElementFactory = null;
        }
        else {
            project = file.getProject();
            psiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
            ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
            Module module = fileIndex.getModuleForFile(file.getVirtualFile());
            if ( module == null ) {
                pegdownOptions = null;
            }
            else if ( !fileIndex.isInSourceContent(file.getVirtualFile()) ) {
                pegdownOptions = null;
            }
            else if ( !Plugin.moduleConfiguration(module).isPegdownEnabled() ) {
                pegdownOptions = null;
            }
            else {
                pegdownOptions = Plugin.moduleConfiguration(module).getRenderingOptions();
            }
        }
    }

    /**
     * Checks whether Pegdown is enabled for the current file.
     *
     * @return `true`, if Pegdown is enabled.
     */
    public boolean isEnabled() {
        return pegdownOptions != null;
    }

    /**
     * Process the given `PsiDocComment` using Pegdown and return a new `PsiDocComment`
     * representing the resulting HTML. The resulting `PsiDocComment` can then be passed
     * to IDEA's default QuickDoc implementation.
     *
     * @param docComment    The `PsiDocComment` to process.
     *
     * @return A `PsiDocComment` representing the resulting HTML.
     */
    public PsiDocComment processDocComment(PsiDocComment docComment) {
        if ( !isEnabled() || docComment == null ) {
            return docComment;
        }
        final Map<String, URL> umlDiagrams = generateUmlDiagrams(docComment);
        Options options = new Options() {
            @Override
            protected ToHtmlSerializer createDocletSerializer() {
                return new DocletSerializer(this, getLinkRenderer()) {
                    @Override
                    protected void printImageTag(LinkRenderer.Rendering rendering) {
                        URL diagram = umlDiagrams.get(rendering.href);
                        if ( diagram != null ) {
                            super.printImageTag(mapHref(rendering, diagram.toString()));
                        }
                        else if ( !ABSOLUTE_IMG_RE.matcher(rendering.href).matches() || rendering.href.contains("{@}") ) {
                            URL baseUrl = VfsUtilCore.convertToURL(file.getVirtualFile().getUrl());
                            if ( baseUrl != null ) {
                                try {
                                    super.printImageTag(mapHref(rendering, new URL(baseUrl, rendering.href).toString()));
                                }
                                catch ( MalformedURLException e ) {
                                    super.printImageTag(rendering);
                                }
                            }
                        }
                        else {
                            super.printImageTag(rendering);
                        }
                    }
                    private LinkRenderer.Rendering mapHref(LinkRenderer.Rendering rendering, String newHref) {
                        LinkRenderer.Rendering mapped = new LinkRenderer.Rendering(
                                newHref, rendering.text);
                        mapped.attributes.addAll(rendering.attributes);
                        return mapped;
                    }
                };
            }
        };
        PsiDocCommentOwner context = PsiTreeUtil.getParentOfType(docComment, PsiDocCommentOwner.class);
        pegdownOptions.applyTo(options);
        PegdownDoclet doclet = new PegdownDoclet(options, null);
        StringBuilder buf = new StringBuilder();
        StringBuilder tagBlock = new StringBuilder();
        boolean start = true;
        //List<String> inlineTags = new ArrayList<>();
        //List<String> tagBlockInlineTags = new ArrayList<>();
        for ( PsiElement elem : docComment.getChildren() ) {
            if ( elem instanceof PsiDocToken ) {
                IElementType tokenType = ((PsiDocToken)elem).getTokenType();
                if ( tokenType == JavaDocTokenType.DOC_COMMENT_START || tokenType == JavaDocTokenType.DOC_COMMENT_END ) {
                    continue;
                }
            }
            if ( start && elem instanceof PsiWhiteSpace ) {
                continue;
            }
            else if ( elem instanceof PsiInlineDocTag ) {
                start = false;
                if ( tagBlock.length() == 0 ) {
                    //inlineTags.add(elem.getText());
                    buf.append(elem.getText());
                }
                else {
                    //tagBlockInlineTags.add(elem.getText());
                    tagBlock.append(elem.getText());
                }
            }
            else if ( elem instanceof PsiDocTag ) {
                PsiDocTag docTag = (PsiDocTag)elem;
                switch ( docTag.getName() ) {
                    case "see":
                        tagBlock.append('\n');
                        renderSeeTag(doclet, tagBlock, docTag);
                        break;
                    case "param":
                    case "throws":
                    case "exception":
                        renderSimpleTag(doclet, tagBlock, docTag, true);
                        break;
                    case "return":
                        renderSimpleTag(doclet, tagBlock, docTag, false);
                        break;
                    case "todo":
                        renderTodoTag(doclet, tagBlock, docTag);
                        break;
                    default:
                        tagBlock.append('\n').append(stripLead(elem.getText()));
                        break;
                }
            }
            else {
                start = false;
                if ( tagBlock.length() == 0 ) {
                    buf.append(elem.getText());
                }
                else {
                    tagBlock.append(elem.getText());
                }
            }
        }
        String markdown = stripLead(buf.toString());

        Plugin.print("Markdown source", markdown);
        String docCommentText = "/**\n" + escapeAsterisks(doclet.toHtml(markdown, false))
                + "\n" + escapeAsterisks(tagBlock.toString()) + "\n*/";
        Plugin.print("Processed DocComment", docCommentText);
        PsiDocComment processedDocComment = docCommentFromText(context, docCommentText);
        LinkExpander linkExpander = new LinkExpander(context, processedDocComment, docCommentText);
        processedDocComment = linkExpander.apply();
        return processedDocComment;
    }

    private PsiDocComment docCommentFromText(PsiElement context, CharSequence text) {
        return psiElementFactory.createDocCommentFromText(text.toString(), context);
    }

    /**
     * Generates all PlantUML diagrams in the given `PsiDocComment`. It returns a Map of
     * file names and the URLs where the image for that file has been saved to. Use this
     * URL for the `<img>` tag.
     *
     * @param docComment    The `PsiDocComment`.
     *
     * @return A map mapping the file names to the image URLs.
     *
     * @see TempFileManager#saveTempFile(byte[], String)
     */
    private Map<String, URL> generateUmlDiagrams(PsiDocComment docComment) {
        TempFileManager tempFiles = Plugin.tempFileManager();
        Map<String, URL> urls = null;
        for ( PsiDocTag tag : docComment.getTags() ) {
            if ( tag instanceof PsiInlineDocTag ) {
                continue;
            }
            else if ( tag.getName().equals("uml") || tag.getName().equals("startuml") ) {
                if ( urls == null ) {
                    urls = new HashMap<>();
                }
                String text = stripLead(tag.getText());
                text = stripFirstWord(text)[1];
                String[] stripped = stripFirstWord(text);
                String fileName = stripped[0];
                text = stripped[1];
                String plantUml = "@startuml " + fileName + "\n"
                        + "skinparam backgroundColor transparent\n"
                        + text
                        + "\n@enduml";
                Plugin.print("UML Diagram Source", plantUml);
                ByteArrayOutputStream image = new ByteArrayOutputStream();
                try {
                    new SourceStringReader(plantUml).generateImage(image);
                }
                catch ( IOException e ) {
                    LOG.error("Error generating UML", e, fileName, String.valueOf(docComment.toString()), String.valueOf(docComment.getContainingFile()));
                }
                try {
                    urls.put(fileName, tempFiles.saveTempFile(image.toByteArray(), "png"));
                }
                catch ( IOException e ) {
                    LOG.error("Error generating UML", e, fileName, String.valueOf(docComment.toString()), String.valueOf(docComment.getContainingFile()));
                }
            }
        }
        return MoreObjects.firstNonNull(urls, Collections.<String, URL>emptyMap());
    }

    private void renderSeeTag(PegdownDoclet doclet, StringBuilder tagBlock, PsiDocTag docTag) {
        final String seeText = toString(docTag, false);
        if ( seeText.startsWith("\"") ) {
            SeeTag tag = new SeeTag() {
                @Override
                public String label() {
                    return null;
                }
                @Override
                public PackageDoc referencedPackage() {
                    return null;
                }
                @Override
                public String referencedClassName() {
                    return null;
                }
                @Override
                public ClassDoc referencedClass() {
                    return null;
                }
                @Override
                public String referencedMemberName() {
                    return null;
                }
                @Override
                public MemberDoc referencedMember() {
                    return null;
                }
                @Override
                public String name() {
                    return "@see";
                }
                @Override
                public Doc holder() {
                    return null;
                }
                @Override
                public String kind() {
                    return "@see";
                }
                @Override
                public String text() {
                    return seeText;
                }
                @Override
                public Tag[] inlineTags() {
                    return new Tag[0];
                }
                @Override
                public Tag[] firstSentenceTags() {
                    return new Tag[0];
                }
                @Override
                public SourcePosition position() {
                    return null;
                }
            };
            SeeTagRenderer.INSTANCE.render(tag, tagBlock, doclet);
        }
        else {
            tagBlock.append("\n@").append(docTag.getName());
            tagBlock.append(' ').append(seeText);
        }
    }

    private void renderSimpleTag(PegdownDoclet doclet, StringBuilder tagBlock, PsiDocTag docTag, boolean stripFirstWord) {
        tagBlock.append("\n@").append(docTag.getName()).append(' ');
        String firstWord = null;
        String text = toString(docTag, false);
        if ( stripFirstWord ) {
            String[] stripped = stripFirstWord(text);
            firstWord = stripped[0];
            text = stripped[1].trim();
        }
        text = TagRendering.simplifySingleParagraph(doclet.toHtml(text, false));
        if ( firstWord != null ) {
            tagBlock.append(firstWord).append(' ');
        }
        tagBlock.append(text).append('\n');
    }

    private void renderTodoTag(PegdownDoclet doclet, StringBuilder tagBlock, PsiDocTag docTag) {
        tagBlock.append("\n<DL style=\"border:solid 1px;padding:5px;\"><DT><B>To Do</B></DT><DD>");
        tagBlock.append(toString(docTag, false));
        tagBlock.append("\n</DD></DL>");
    }

    /**
     * Strip leading asterisks as specified by the JavaDoc specification.
     *
     * @param doc    The JavaDoc comment.
     *
     * @return A JavaDoc comment with leading asterisks stripped.
     */
    private static String stripLead(String doc) {
        return LEADING_ASTERISK_RE.matcher(doc).replaceAll("");
    }

    /**
     * HTML-escape all asterisks in the given doc comment. After all leading asterisks
     * have been {@link #stripLead(String) stripped}, previously non-leading asterisks
     * would now be interpreted as leading (and therefore be ignored). Escaping them
     * avoids this confusion.
     *
     * @param doc    The doc comment where leading (ignorable) asterisks have been
     *               stripped.
     *
     * @return A doc comment where all asterisks have been HTML-escaped.
     */
    private static String escapeAsterisks(String doc) {
        return doc.replace("*", "&#42;");
    }

    /**
     * Convert a given `PsiDocTag` to a string.
     *
     * @param docTag            The `PsiDocTag` to be converted to a string.
     * @param stripFirstWord    `true`, if the first word should be stripped (e.g. the
     *                          parameter name of a `@param` tag.
     *
     * @return The `PsiDocTag` as string.
     */
    private static String toString(PsiDocTag docTag, boolean stripFirstWord) {
        String tagText = stripLead(docTag.getText());
        tagText = stripFirstWord(tagText)[1]; // remove the tag itself
        if ( stripFirstWord ) {
            String[] stripped = stripFirstWord(tagText);
            return stripped[0] + " " + stripped[1].trim();
        }
        else {
            return tagText.trim();
        }
    }

    private static String[] stripFirstWord(String string) {
        string = CharMatcher.WHITESPACE.trimLeadingFrom(string);
        int pos = CharMatcher.WHITESPACE.indexIn(string);
        if ( pos >= 0 ) {
            return new String[] {
                    string.substring(0, pos),
                    CharMatcher.WHITESPACE.trimLeadingFrom(string.substring(pos))
            };
        }
        else {
            return new String[] {
                    string.trim(),
                    ""
            };
        }
    }

    /**
     * Because the PsiDocComment returned is rooted in a method without any root, resolving relative
     * links like `{{@literal @}link #myMethod}` won't be resolved correctly. It resolves from the
     * {@link PsiDocTag} upwards using {@link PsiDocTag#getParent()}, completely ignoring the given
     * `context` (this is probably a bug of IDEA, method
     * {@link JavaDocUtil#findReferenceTarget(PsiManager, String, PsiElement, boolean)}).
     *
     * Expanding the links to absolute references before processing them fixes this. We can use the
     * methods of {@link JavaDocUtil} to do this.
     *
     * @see "[Issue #39 on GitHub](https://github.com/Abnaxos/pegdown-doclet/issues/39)"
     * @see JavaDocUtil#findReferenceTarget(PsiManager, String, PsiElement, boolean)
     */
    private class LinkExpander extends PsiElementVisitor {

        private final PsiDocCommentOwner docContext;
        private final PsiDocComment originalDocComment;
        private final String docText;
        private int docTextPosition = 0;
        @SuppressWarnings("StringBufferField")
        private StringBuilder buffer = null;

        LinkExpander(PsiDocCommentOwner docContext, PsiDocComment originalDocComment, String docText) {
            this.docContext = docContext;
            this.originalDocComment = originalDocComment;
            this.docText = docText;
        }

        PsiDocComment apply() {
            if ( docContext == null ) {
                return originalDocComment;
            }
            docTextPosition = 0;
            buffer = null;
            originalDocComment.acceptChildren(LinkExpander.this);
            if ( buffer == null ) {
                return originalDocComment;
            }
            else {
                buffer.append(docText, docTextPosition, docText.length());
                String text = buffer.toString();
                Plugin.print("After expanding links", text);
                return docCommentFromText(docContext, text);
            }
        }

        @Override
        public void visitElement(PsiElement element) {
            if ( element instanceof PsiDocTag ) {
                PsiDocTag tag = (PsiDocTag)element;
                String tagName = null;
                switch ( tag.getName() ) {
                    case "link":
                    case "linkplain":
                    case "see":
                        // todo: @ssee
                        tagName = tag.getName();
                }
                if ( tagName != null ) {
                    int inlineOffset = (tag instanceof PsiInlineDocTag) ? 1 : 0;
                    String linkText = tag.getText().substring(inlineOffset + 1 + tagName.length(), tag.getTextLength() - inlineOffset).trim();
                    if ( !linkText.startsWith("#") ) {
                        return;
                    }
                    StringBuilder newLink = new StringBuilder(100);
                    if ( inlineOffset > 0 ) {
                        newLink.append('{');
                    }
                    newLink.append('@').append(tagName).append(' ');
                    int refEndIndex = JavaDocUtil.extractReference(linkText);
                    String refText = linkText.substring(0, refEndIndex);
                    PsiElement target = JavaDocUtil.findReferenceTarget(docContext.getManager(), refText, docContext, true);
                    if ( target == null ) {
                        return;
                    }
                    newLink.append(JavaDocUtil.getReferenceText(project, target)).append(' ');
                    String labelText = linkText.substring(refEndIndex).trim();
                    if ( labelText.isEmpty() ) {
                        labelText = JavaDocUtil.getLabelText(project, docContext.getManager(), refText, docContext);
                    }
                    newLink.append(labelText);
                    if ( inlineOffset > 0 ) {
                        newLink.append('}');
                    }
                    int start = getStartOffsetInComment(element);
                    if ( buffer == null ) {
                        buffer = new StringBuilder(docText.length() + 100);
                    }
                    buffer.append(docText, docTextPosition, start);
                    buffer.append(newLink);
                    docTextPosition += start - docTextPosition + element.getTextLength();
                }
            }
            element.acceptChildren(this);
        }

        private int getStartOffsetInComment(PsiElement element) {
            int offset = 0;
            while ( element != null && !(element instanceof PsiDocComment) ) {
                offset += element.getStartOffsetInParent();
                element = element.getParent();
            }
            return offset;
        }

    }

}
