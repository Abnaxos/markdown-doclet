package ch.raffael.mddoclet.jdk9.doctree;

import javax.lang.model.element.Name;

import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.EndElementTree;

import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 08.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class MdEndElementTree implements EndElementTree {

    private final MdName name;

    public MdEndElementTree(MdName name) {
        this.name = name;
    }

    public MdEndElementTree(Name name) {
        this(MdName.of(name));
    }

    public MdEndElementTree(String name) {
        this(MdName.of(name));
    }

    @Override
    public Kind getKind() {
        return Kind.END_ELEMENT;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitEndElement(this, data);
    }
}
