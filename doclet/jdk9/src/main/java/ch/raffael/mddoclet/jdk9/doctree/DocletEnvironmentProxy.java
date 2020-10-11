package ch.raffael.mddoclet.jdk9.doctree;

import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;

import ch.raffael.nullity.NotNull;


/**
 * TODO: 02.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public class DocletEnvironmentProxy implements DocletEnvironment {

    private final DocletEnvironment delegate;
    private final DocTreesProxy docTreesProxy;

    public DocletEnvironmentProxy(DocletEnvironment delegate) {
        this.delegate = delegate;
        docTreesProxy = new DocTreesProxy(delegate.getDocTrees());
    }

    @Override
    public Set<? extends Element> getSpecifiedElements() {
        return delegate.getSpecifiedElements();
    }

    @Override
    public Set<? extends Element> getIncludedElements() {
        return delegate.getIncludedElements();
    }

    @Override
    public DocTrees getDocTrees() {
        return docTreesProxy;
    }

    @Override
    public Elements getElementUtils() {
        return delegate.getElementUtils();
    }

    @Override
    public Types getTypeUtils() {
        return delegate.getTypeUtils();
    }

    @Override
    public boolean isIncluded(@NotNull Element e) {
        return delegate.isIncluded(e);
    }

    @Override
    public boolean isSelected(@NotNull Element e) {
        return delegate.isSelected(e);
    }

    @Override
    public JavaFileManager getJavaFileManager() {
        return delegate.getJavaFileManager();
    }

    @Override
    public SourceVersion getSourceVersion() {
        return delegate.getSourceVersion();
    }

    @Override
    public ModuleMode getModuleMode() {
        return delegate.getModuleMode();
    }

    @Override
    public JavaFileObject.Kind getFileKind(@NotNull TypeElement type) {
        return delegate.getFileKind(type);
    }
}
