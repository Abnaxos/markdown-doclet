package ch.raffael.mddoclet.core.options;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.raffael.nullity.Nullable;


/**
 * @author Raffael Herzog
 */
public final class OptionDescriptor {

    private final List<String> names;
    private final int argumentCount;
    @Nullable
    private final String description;
    @Nullable
    private final String parameters;
    private final OptionProcessor processor;

    private OptionDescriptor(Builder builder) {
        names = builder.names;
        argumentCount = builder.argumentCount;
        description = builder.description;
        parameters = builder.parameters;
        processor = builder.processor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getNames() {
        return names;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getParameters() {
        return parameters;
    }

    public OptionProcessor processor() {
        return processor;
    }

    public static final class Builder {

        private List<String> names;
        private int argumentCount;
        @Nullable
        private String description;
        @Nullable
        private String parameters;
        private OptionProcessor processor;

        private Builder() {
        }

        public Builder names(List<String> names) {
            this.names = ImmutableList.copyOf(names);
            return this;
        }

        @SafeVarargs
        public final Builder names(String... names) {
            this.names = ImmutableList.copyOf(names);
            return this;
        }

        public Builder argumentCount(int argumentCount) {
            this.argumentCount = argumentCount;
            return this;
        }

        public Builder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public Builder parameters(@Nullable String parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder processor(OptionProcessor processor) {
            this.processor = processor;
            return this;
        }

        public OptionDescriptor build() {
            return new OptionDescriptor(this);
        }
    }
}
