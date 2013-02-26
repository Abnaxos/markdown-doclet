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

import com.intellij.codeInsight.javadoc.JavaDocInfoGenerator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.tree.IElementType;

import ch.raffael.doclets.pegdown.Options;
import ch.raffael.doclets.pegdown.PegdownDoclet;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownJavaDocInfoGenerator extends JavaDocInfoGenerator {

    private final static Pattern CLEANUP_RE = Pattern.compile("^(\\s*\\*+) ?", Pattern.MULTILINE);

    private final PegdownOptions.RenderingOptions pegdownOptions;
    private final Project project;
    private final PsiElementFactory psiFactory;

    public PegdownJavaDocInfoGenerator(Project project, PsiElement element, PegdownOptions.RenderingOptions pegdownOptions) {
        super(project, element);
        this.pegdownOptions = pegdownOptions;
        this.project = project;
        this.psiFactory = JavaPsiFacade.getInstance(project).getElementFactory();
    }

    @Override
    public void generateCommonSection(StringBuilder buffer, PsiDocComment docComment) {
        //for ( PsiElement elem : docComment.getDescriptionElements() ) {
        //    System.out.println(elem);
        //}
        StringBuilder buf = new StringBuilder();
        boolean start = true;
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
            else {
                start = false;
                buf.append(elem.getText());
            }
        }
        String markdown = CLEANUP_RE.matcher(buf).replaceAll("");
        Options options = new Options();
        pegdownOptions.applyTo(options);
        PegdownDoclet doclet = new PegdownDoclet(options, null);
        super.generateCommonSection(buffer, psiFactory.createDocCommentFromText("/**\n" + doclet.toHtml(markdown) + "\n*/"));
    }

}
