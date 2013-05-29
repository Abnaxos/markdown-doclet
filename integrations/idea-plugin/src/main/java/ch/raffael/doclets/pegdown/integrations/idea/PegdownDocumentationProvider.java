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

import java.util.List;

import com.intellij.codeInsight.javadoc.JavaDocExternalFilter;
import com.intellij.lang.ASTNode;
import com.intellij.lang.java.JavaDocumentationProvider;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.tree.JavaDocElementType;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownDocumentationProvider extends JavaDocumentationProvider {

    private final Class[] SUPPORTED_ELEMENT_TYPES = {
            PsiPackage.class, PsiDirectory.class, PsiClass.class, // FIXME: PsiFile.class?
            PsiMethod.class, PsiField.class, PsiParameter.class
    };

    public PegdownDocumentationProvider() {
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        // JavaDocumentationProvider will do the Right Thing
        return null;
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        // JavaDocumentationProvider will do the Right Thing
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        boolean process = false;
        for ( Class supported: SUPPORTED_ELEMENT_TYPES ) {
            if ( supported.isInstance(element) ) {
                process = true;
                break;
            }
        }
        if ( !process ) {
            return null;
        }
        PsiFile file = null;
        if ( element instanceof PsiDirectory ) {
            // let's see whether we can map the directory to a package; if so, change the
            // element to the package and continue
            PsiPackage pkg = JavaDirectoryService.getInstance().getPackage((PsiDirectory)element);
            if ( pkg != null ) {
                element = pkg;
            }
            else {
                return null;
            }
        }
        if ( element instanceof PsiPackage ) {
            for ( PsiDirectory dir : ((PsiPackage)element).getDirectories() ) {
                PsiFile info = dir.findFile(PsiPackage.PACKAGE_INFO_FILE);
                if ( info != null ) {
                    ASTNode node = info.getNode();
                    if ( node != null ) {
                        ASTNode docCommentNode = node.findChildByType(JavaDocElementType.DOC_COMMENT);
                        if ( docCommentNode != null ) {
                            // the default implementation will now use this file
                            // we're going to take over below, if Pegdown is enabled in
                            // the corresponding module
                            // see JavaDocInfoGenerator.generatePackageJavaDoc()
                            file = info;
                            break;
                        }
                    }
                }
                if ( dir.findFile("package.html") != null ) {
                    // leave that to the default
                    return null;
                }
            }
        }
        else {
            if ( JavaLanguage.INSTANCE.equals(element.getLanguage()) ) {
                element = element.getNavigationElement();
                if ( element.getContainingFile() != null ) {
                    file = element.getContainingFile();
                }
            }
        }
        if ( file != null ) {
            DocCommentProcessor processor = new DocCommentProcessor(file);
            if ( processor.isEnabled() ) {
                String docHtml;
                if ( element instanceof PsiMethod ) {
                    docHtml = super.generateDoc(PsiProxy.forMethod((PsiMethod)element), originalElement);
                }
                else if ( element instanceof PsiParameter ) {
                    docHtml = super.generateDoc(PsiProxy.forParameter((PsiParameter)element), originalElement);
                }
                else {
                    PegdownJavaDocInfoGenerator javaDocInfoGenerator = new PegdownJavaDocInfoGenerator(element.getProject(), element, processor);
                    List<String> docURLs = getExternalJavaDocUrl(element);
                    String text = javaDocInfoGenerator.generateDocInfo(docURLs);
                    Plugin.print("Intermediate HTML output", text);
                    docHtml = JavaDocExternalFilter.filterInternalDocInfo(text);
                }
                docHtml = extendCss(docHtml);
                Plugin.print("Final HTML output", docHtml);
                return docHtml;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        // nothing to do here
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        // JavaDocumentationProvider will do the Right Thing
        return null;
    }

    private static String extendCss(String html) {
        @Language("CSS") String css = "\n"
                // I know, these tables aren't beautiful; however, Swing CSS is so
                // limited, this is about as good as it gets ...
                +"table { /*unsupported: border-collapse: collapse;*/ border: 0; border-spacing: 0; }\n"
                +"table td, table th { border: outset 1px black; padding-left: 5px; padding-right: 5px;}\n"
                +"\n";
        String upperHtml = html.toUpperCase();
        int bodyPos = upperHtml.indexOf("<BODY>");
        if ( bodyPos < 0 ) {
            return html;
        }
        int stylePos = upperHtml.lastIndexOf("</STYLE>", bodyPos);
        if ( stylePos < 0 ) {
            return html;
        }
        return html.substring(0, stylePos) + css + html.substring(stylePos);
    }

}
