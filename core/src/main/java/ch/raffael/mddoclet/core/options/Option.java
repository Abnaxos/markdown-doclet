package ch.raffael.mddoclet.core.options;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.raffael.nullity.Nullable;


/**
 * Represents an option and its arguments.
 *
 * @author Raffael Herzog
 */
public final class Option {

    private final String name;
    private final List<String> arguments;

    private Option(Builder builder) {
        if (builder.name == null) throw new IllegalStateException("No option name specified");
        name = builder.name;
        arguments = ImmutableList.copyOf(builder.arguments);
    }

    private Option(String name, @Nullable List<String> arguments) {
        this.name = name;
        if (arguments == null) {
            this.arguments = ImmutableList.of();
        } else {
            this.arguments = ImmutableList.copyOf(arguments);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Option of(String name) {
        return new Option(name, null);
    }

    public static Option of(String name, @Nullable List<String> arguments) {
        return new Option(name, arguments);
    }

    public static Option of(String name, @Nullable String... arguments) {
        return new Option(name, arguments != null ? ImmutableList.copyOf(arguments) : null);
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option that = (Option)o;
        return name.equals(that.name) && arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + arguments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Option[" + name + ":" + arguments + "]";
    }

    public static final class Builder {

        @Nullable
        private String name;
        private List<String> arguments = new ArrayList<>();

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder arguments(List<String> arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder addArgument(String argument) {
            arguments.add(argument);
            return this;
        }

        public Option build() {
            return new Option(this);
        }
    }
}
