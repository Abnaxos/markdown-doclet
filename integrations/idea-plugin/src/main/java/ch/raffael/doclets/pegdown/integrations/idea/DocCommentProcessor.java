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

import java.util.regex.Pattern;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.javadoc.PsiInlineDocTag;
import com.intellij.psi.tree.IElementType;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

import ch.raffael.doclets.pegdown.Options;
import ch.raffael.doclets.pegdown.PegdownDoclet;
import ch.raffael.doclets.pegdown.SeeTagRenderer;
import ch.raffael.doclets.pegdown.TagRendering;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DocCommentProcessor {

    private final static Pattern CLEANUP_RE = Pattern.compile("^(\\s*\\*+) ?", Pattern.MULTILINE);

    private final Project project;
    private final PegdownOptions.RenderingOptions pegdownOptions;

    public DocCommentProcessor(PsiFile file) {
        if ( file == null ) {
            project = null;
            pegdownOptions = null;
        }
        else {
            project = file.getProject();
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

    public boolean isEnabled() {
        return pegdownOptions != null;
    }

    public PsiDocComment processDocComment(PsiDocComment docComment) {
        if ( !isEnabled() || docComment == null ) {
            return docComment;
        }
        Options options = new Options();
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
                    case "return":
                        renderSimpleTag(doclet, tagBlock, docTag);
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
        docComment = JavaPsiFacade.getInstance(project).getElementFactory().createDocCommentFromText(
                "/**\n" + addLead(doclet.toHtml(markdown))
                        + "\n" + addLead(tagBlock.toString()) + "\n */");
        Plugin.print("Processed DocComment", docComment.getText());
        return docComment;
    }

    private void renderSeeTag(PegdownDoclet doclet, StringBuilder tagBlock, PsiDocTag docTag) {
        final String seeText = toString(null, docTag.getDataElements());
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

    private void renderSimpleTag(PegdownDoclet doclet, StringBuilder tagBlock, PsiDocTag docTag) {
        tagBlock.append("\n@").append(docTag.getName());
        if ( docTag.getValueElement() != null ) {
            tagBlock.append(' ').append(docTag.getValueElement().getText());
        }
        tagBlock.append(' ').append(TagRendering.simplifySingleParagraph(
                doclet.toHtml(toString(docTag.getValueElement(),
                                       docTag.getDataElements()))));
    }

    private void renderTodoTag(PegdownDoclet doclet, StringBuilder tagBlock, PsiDocTag docTag) {
        tagBlock.append("\n<DL style=\"border:solid 1px;padding:5px;\"><DT><B>To Do</B></DT><DD>");
        tagBlock.append(toString(docTag.getNameElement(), docTag.getChildren()));
        tagBlock.append("\n</DD></DL>");
    }

    private static String stripLead(String doc) {
        return CLEANUP_RE.matcher(doc).replaceAll("");
    }

    private static String addLead(String doc) {
        return " * " + doc.replaceAll("\n", "\n * ");
    }

    private static String toString(PsiElement value, PsiElement[] elements) {
        // Start the comment with a "* ". This is necessary because stripLead() will strip
        // leading asterisks. However, in this case, the leading asterisk was before the
        // tag. E.g., in the comment "@return *Return value*" would strip the first
        // asterisk, resultin in "@return Return value*" instead of
        // "@return <em>Return value</em>", as intended.
        StringBuilder buf = new StringBuilder("* ");
        for ( PsiElement elem : elements ) {
            if ( !elem.equals(value) ) {
                buf.append(elem.getText());
            }
        }
        return stripLead(buf.toString()).trim();
    }

}
