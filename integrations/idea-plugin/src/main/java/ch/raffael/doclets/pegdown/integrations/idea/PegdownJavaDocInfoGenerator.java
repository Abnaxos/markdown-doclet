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

import com.intellij.codeInsight.javadoc.JavaDocInfoGenerator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;


/**
 * Overrides `generateCommonSection()` to enable rendering of doc comments using the
 * {@link DocCommentProcessor}.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownJavaDocInfoGenerator extends JavaDocInfoGenerator {

    private final Project project;
    private final PsiElement element;
    private final DocCommentProcessor processor;

    public PegdownJavaDocInfoGenerator(Project project, PsiElement element, DocCommentProcessor processor) {
        super(project, element);
        this.project = project;
        this.element = element;
        this.processor = processor;
    }

    @Override
    public void generateCommonSection(StringBuilder buffer, PsiDocComment docComment) {
        docComment = processor.processDocComment(docComment);
        super.generateCommonSection(buffer, docComment);
    }

}
