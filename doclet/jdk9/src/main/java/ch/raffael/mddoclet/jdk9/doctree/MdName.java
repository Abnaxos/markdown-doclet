package ch.raffael.mddoclet.jdk9.doctree;

import java.util.stream.IntStream;

import javax.lang.model.element.Name;

import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 08.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class MdName implements Name {

    private final String name;

    private MdName(String name) {
        this.name = name;
    }

    public static MdName of(String name) {
        return new MdName(name);
    }

    public static MdName of(Name name) {
        return (name instanceof MdName) ? (MdName)name : new MdName(name.toString());
    }

    @Override
    public boolean contentEquals(@NotNull CharSequence cs) {
        return name.contentEquals(cs);
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.substring(start, end);
    }

    @Override
    public IntStream chars() {
        return name.chars();
    }

    @Override
    public IntStream codePoints() {
        return name.codePoints();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        //noinspection ObjectEquality
        if (o == null || getClass() != o.getClass()) return false;
        return name.equals(((MdName)o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
