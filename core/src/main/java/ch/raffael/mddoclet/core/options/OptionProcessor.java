package ch.raffael.mddoclet.core.options;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableSet;


/**
 * @author Raffael Herzog
 */
public interface OptionProcessor {

    void process(String name, List<String> arguments) throws InvalidOptionArgumentsException;

    static OptionProcessor verifyName(Collection<String> names, OptionProcessor processor) {
        ImmutableSet<String> immutableNames = ImmutableSet.copyOf(names);
        return (name, arguments) -> {
            if (!immutableNames.contains(name)) {
                throw new InvalidOptionArgumentsException("Unexpected option name: '" + name + "'");
            }
            processor.process(name, arguments);
        };
    }

}
