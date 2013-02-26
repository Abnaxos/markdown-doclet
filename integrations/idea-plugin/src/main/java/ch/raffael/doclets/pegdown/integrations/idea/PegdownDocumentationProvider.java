package ch.raffael.doclets.pegdown.integrations.idea;

import java.util.List;

import com.intellij.codeInsight.javadoc.JavaDocExternalFilter;
import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.source.tree.JavaDocElementType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.jetbrains.annotations.Nullable;

import static com.intellij.lang.java.JavaDocumentationProvider.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownDocumentationProvider implements DocumentationProvider {

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
            ProjectFileIndex fileIndex = ProjectRootManager.getInstance(element.getProject()).getFileIndex();
            Module module = fileIndex.getModuleForFile(file.getVirtualFile());
            if ( module == null ) {
                return null;
            }
            if ( !fileIndex.isInSourceContent(file.getVirtualFile()) ) {
                return null;
            }
            if ( !Plugin.moduleConfiguration(module).isPegdownEnabled() ) {
                return null;
            }
            final PegdownJavaDocInfoGenerator javaDocInfoGenerator = new PegdownJavaDocInfoGenerator(element.getProject(), element, Plugin.moduleConfiguration(module).getRenderingOptions());
            final List<String> docURLs = getExternalJavaDocUrl(element);
            return JavaDocExternalFilter.filterInternalDocInfo(javaDocInfoGenerator.generateDocInfo(docURLs));
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

    private boolean isPegdownEnabled(PsiClass psiClass) {
        if ( psiClass.getNavigationElement() instanceof PsiClass ) {
            psiClass = (PsiClass)psiClass.getNavigationElement();
        }
        PsiFile file = psiClass.getContainingFile();
        if ( file instanceof PsiJavaFile ) {
            return isPegdownEnabled(JavaPsiFacade.getInstance(psiClass.getProject()).findPackage(((PsiJavaFile)file).getPackageName()));
        }
        else {
            return false;
        }
    }

    private boolean isPegdownEnabled(PsiPackage pkg) {
        if ( pkg == null || pkg.getName() == null || pkg.getName().isEmpty() ) {
            return false;
        }
        if ( pkg.getNavigationElement() != null ) {
            pkg = (PsiPackage)pkg.getNavigationElement();
        }
        System.out.println("Checking: " + pkg);
        if ( pkg.getDirectories() != null ) {
            for ( PsiDirectory dir : pkg.getDirectories() ) {
                // FIXME: won't recognise package-info.java in source path of libraries
                PsiFile info = dir.findFile("package-info.java");
                if ( info != null ) {
                    System.out.println("Scanning: " + pkg);
                    for ( PsiElement child : info.getChildren() ) {
                        if ( child instanceof PsiDocComment ) {
                            for ( PsiElement tok : child.getChildren() ) {
                                if ( tok instanceof PsiDocTag ) {
                                    if ( ((PsiDocTag)tok).getName().equals("pegdown") ) {
                                        return true;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return isPegdownEnabled(pkg.getParentPackage());
    }

}
