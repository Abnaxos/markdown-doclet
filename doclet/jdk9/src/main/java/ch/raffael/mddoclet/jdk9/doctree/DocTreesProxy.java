package ch.raffael.mddoclet.jdk9.doctree;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.DocSourcePositions;
import com.sun.source.util.DocTreeFactory;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;

import ch.raffael.nullity.NotNull;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
final class DocTreesProxy extends DocTrees {

    private final DocTrees delegate;

    DocTreesProxy(DocTrees delegate) {
        this.delegate = delegate;
    }

    @Override
    public BreakIterator getBreakIterator() {
        return delegate.getBreakIterator();
    }

    @Override
    public DocCommentTree getDocCommentTree(@NotNull TreePath path) {
        return delegate.getDocCommentTree(path);
    }

    @Override
    public DocCommentTree getDocCommentTree(@NotNull Element e) {
        return delegate.getDocCommentTree(e);
    }

    @Override
    public DocCommentTree getDocCommentTree(@NotNull FileObject fileObject) {
        return delegate.getDocCommentTree(fileObject);
    }

    @Override
    public DocCommentTree getDocCommentTree(@NotNull Element e, @NotNull String relativePath) throws IOException {
        return delegate.getDocCommentTree(e, relativePath);
    }

    @Override
    public DocTreePath getDocTreePath(@NotNull FileObject fileObject, @NotNull PackageElement packageElement) {
        return delegate.getDocTreePath(fileObject, packageElement);
    }

    @Override
    public Element getElement(@NotNull DocTreePath path) {
        return delegate.getElement(path);
    }

    @Override
    public List<DocTree> getFirstSentence(@NotNull List<? extends DocTree> list) {
        return delegate.getFirstSentence(list);
    }

    @Override
    public DocSourcePositions getSourcePositions() {
        return delegate.getSourcePositions();
    }

    @Override
    public void printMessage(@NotNull Diagnostic.Kind kind, @NotNull CharSequence msg, @NotNull DocTree t, @NotNull DocCommentTree c, @NotNull CompilationUnitTree root) {
        delegate.printMessage(kind, msg, t, c, root);
    }

    @Override
    public void setBreakIterator(@NotNull BreakIterator breakiterator) {
        delegate.setBreakIterator(breakiterator);
    }

    @Override
    public DocTreeFactory getDocTreeFactory() {
        return delegate.getDocTreeFactory();
    }

    @Override
    public Tree getTree(@NotNull Element element) {
        return delegate.getTree(element);
    }

    @Override
    public ClassTree getTree(@NotNull TypeElement element) {
        return delegate.getTree(element);
    }

    @Override
    public MethodTree getTree(@NotNull ExecutableElement method) {
        return delegate.getTree(method);
    }

    @Override
    public Tree getTree(@NotNull Element e, @NotNull AnnotationMirror a) {
        return delegate.getTree(e, a);
    }

    @Override
    public Tree getTree(@NotNull Element e, @NotNull AnnotationMirror a, @NotNull AnnotationValue v) {
        return delegate.getTree(e, a, v);
    }

    @Override
    public TreePath getPath(@NotNull CompilationUnitTree unit, @NotNull Tree node) {
        return delegate.getPath(unit, node);
    }

    @Override
    public TreePath getPath(@NotNull Element e) {
        return delegate.getPath(e);
    }

    @Override
    public TreePath getPath(@NotNull Element e, @NotNull AnnotationMirror a) {
        return delegate.getPath(e, a);
    }

    @Override
    public TreePath getPath(@NotNull Element e, @NotNull AnnotationMirror a, @NotNull AnnotationValue v) {
        return delegate.getPath(e, a, v);
    }

    @Override
    public Element getElement(@NotNull TreePath path) {
        return delegate.getElement(path);
    }

    @Override
    public TypeMirror getTypeMirror(@NotNull TreePath path) {
        return delegate.getTypeMirror(path);
    }

    @Override
    public Scope getScope(@NotNull TreePath path) {
        return delegate.getScope(path);
    }

    @Override
    public String getDocComment(@NotNull TreePath path) {
        return delegate.getDocComment(path);
    }

    @Override
    public boolean isAccessible(@NotNull Scope scope, @NotNull TypeElement type) {
        return delegate.isAccessible(scope, type);
    }

    @Override
    public boolean isAccessible(@NotNull Scope scope, @NotNull Element member, @NotNull DeclaredType type) {
        return delegate.isAccessible(scope, member, type);
    }

    @Override
    public TypeMirror getOriginalType(@NotNull ErrorType errorType) {
        return delegate.getOriginalType(errorType);
    }

    @Override
    public void printMessage(@NotNull Diagnostic.Kind kind, @NotNull CharSequence msg, @NotNull Tree t, @NotNull CompilationUnitTree root) {
        delegate.printMessage(kind, msg, t, root);
    }

    @Override
    public TypeMirror getLub(@NotNull CatchTree tree) {
        return delegate.getLub(tree);
    }
}
