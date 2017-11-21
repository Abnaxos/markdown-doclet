package ch.raffael.mddoclet.core.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.google.common.collect.Iterators;

import ch.raffael.nullity.Nullable;


/**
 * TODO: 17.11.17 Javadoc?
 *
 * @author Raffael Herzog
 */
public class OptionSet implements Iterable<OptionDescriptor> {

    private final List<OptionDescriptor> options = new ArrayList<>();

    public OptionSet addOptions(OptionDescriptor... options) {
        return addOptions(Arrays.asList(options));
    }

    public OptionSet addOptions(Iterable<OptionDescriptor> options) {
        if (options instanceof Collection) {
            addOptions((Collection<OptionDescriptor>)options);
        } else {
            options.forEach(this.options::add);
        }
        return this;
    }

    public OptionSet addOptions(Collection<OptionDescriptor> options) {
        this.options.addAll(options);
        return this;
    }

    @Nullable
    public OptionDescriptor findByName(String name) {
        return options.stream().filter(o -> o.getNames().contains(name)).findFirst().orElse(null);
    }

    @Override
    public Iterator<OptionDescriptor> iterator() {
        return Iterators.unmodifiableIterator(options.iterator());
    }

    @Override
    public Spliterator<OptionDescriptor> spliterator() {
        return options.spliterator();
    }

    @Override
    public void forEach(Consumer<? super OptionDescriptor> action) {
        options.forEach(action);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[." + options + "]";
    }
}
