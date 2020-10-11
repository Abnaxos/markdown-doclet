package ch.raffael.mddoclet.jdk9.doctree;

import java.util.List;

import javax.lang.model.element.Name;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;

import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 08.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class MdAttributeTree extends AttributeTree {

    private final MdName name;
    private final ValueKind valueKind;

    public MdAttributeTree(MdName name, ValueKind valueKind) {
        this.name = name;
        this.valueKind = valueKind;
    }

    @Override
    public Kind getKind() {
        return Kind.ATTRIBUTE;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public ValueKind getValueKind() {
        return valueKind;
    }

    @Override
    public List<? extends DocTree> getValue() {
        return proxyOf();
    }

    @Override
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitAttribute(this, data);
    }
}
