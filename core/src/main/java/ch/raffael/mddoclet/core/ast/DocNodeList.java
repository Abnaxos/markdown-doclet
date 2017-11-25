package ch.raffael.mddoclet.core.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 25.11.17 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class DocNodeList extends ArrayList<DocNode> {

    private final DocNode parent;

    DocNodeList(DocNode parent) {
        this.parent = parent;
    }

    DocNodeList(DocNode parent, List<? extends DocNode> nodes) {
        super(nodes);
        this.parent = parent;
        forEach(this::reparent);
    }

    static DocNodeList ofNullableList(DocNode parent,@Nullable List<? extends DocNode> nodes) {
        return nodes == null ? new DocNodeList(parent) : new DocNodeList(parent, nodes);
    }

    public void insertBefore(DocNode after, DocNode newChild) {
        add(requireIndexOf(after), newChild);
    }

    public void insertAfter(DocNode after, DocNode newChild) {
        add(requireIndexOf(after) + 1, newChild);
    }

    public void replace(DocNode after, DocNode... newChildren) {
        int index = requireIndexOf(after);
        if (newChildren.length == 0) {
            remove(index);
        } else {
            set(index, newChildren[0]);
            ensureCapacity(size() + newChildren.length - 1);
            for (int i = 1; i < newChildren.length; i++) {
                add(index + i, newChildren[i]);
            }
        }
    }

    public void replace(DocNode after, Collection<? extends DocNode> newChildren) {
        int index = requireIndexOf(after);
        switch (newChildren.size()) {
        case 0:
            remove(index);
            break;
        case 1:
            if (newChildren instanceof List) {
                set(index, (DocNode)((List)newChildren).get(index));
            } else {
                set(index, newChildren.iterator().next());
            }
            break;
        default:
            remove(index);
            addAll(index, newChildren);
        }
    }

    private int requireIndexOf(DocNode after) {
        int index = indexOf(after);
        if (index < 0) {
            throw new IllegalArgumentException("Node " + this + " has no child " + after);
        }
        return index;
    }

    public <T extends DocNodeVisitor> T visitAll(T visitor) {
        forEach(n -> n.accept(visitor));
        return visitor;
    }

    /*
     * reparenting
     */

    @Override
    public DocNode set(int index, @NotNull DocNode node) {
        return super.set(index, reparent(node));
    }

    @Override
    public boolean add(@NotNull DocNode node) {
        return super.add(reparent(node));
    }

    @Override
    public void add(int index, @NotNull DocNode node) {
        super.add(index, reparent(node));
    }

    @Override
    public DocNode remove(int index) {
        return unparent(super.remove(index));
    }

    @Override
    public boolean remove(Object element) {
        if (element instanceof DocNode) {
            DocNode node = (DocNode)element;
            if (super.remove(node)) {
                unparent(node);
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        forEach(this::unparent);
        super.clear();
    }

    @Override
    public boolean addAll(Collection<? extends DocNode> nodes) {
        nodes.forEach(this::reparent);
        return super.addAll(nodes);
    }

    @Override
    public boolean addAll(int index, Collection<? extends DocNode> nodes) {
        nodes.forEach(this::reparent);
        return super.addAll(index, nodes);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        subList(fromIndex, toIndex).forEach(this::unparent);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean didRemove = false;
        for (Object element : collection) {
            if (remove(element)) didRemove = true;
        }
        return didRemove;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return removeIf(element -> !collection.contains(element));
    }

    @Override
    public boolean removeIf(Predicate<? super DocNode> filter) {
        return super.removeIf((DocNode n) -> {
            if (filter.test(n)) {
                unparent(n);
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void replaceAll(UnaryOperator<DocNode> operator) {
        super.replaceAll(n -> reparent(operator.apply(unparent(n))));
    }

    private DocNode reparent(DocNode node) {
        return node.reparent(parent);
    }

    private DocNode unparent(DocNode node) {
        return node.reparent(null);
    }

}
