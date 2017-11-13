package ch.raffael.mddoclet.core.options;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ForwardingMap;


/**
 * A special option type that adds or removes elements from collections of
 * enum values (usually sets).
 *
 * @author Raffael Herzog
 */
public class EnumSetOption<E extends Enum<E>> extends ForwardingMap<E, Boolean> {

    private final Map<E, Boolean> adjustments = new LinkedHashMap<>();

    @Override
    protected Map<E, Boolean> delegate() {
        return adjustments;
    }

    public <C extends Collection<? super E>> C applyTo(C enumSet) {
        for (Entry<E, Boolean> adjustment : adjustments.entrySet()) {
            if (adjustment.getValue() != null) {
                if (adjustment.getValue()) {
                    enumSet.add(adjustment.getKey());
                } else {
                    enumSet.remove(adjustment.getKey());
                }
            }
        }
        return enumSet;
    }

}
