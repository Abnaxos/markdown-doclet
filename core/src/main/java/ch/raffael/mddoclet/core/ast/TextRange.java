package ch.raffael.mddoclet.core.ast;

import com.google.common.base.Preconditions;


/**
 * Represents a range in a string.
 *
 * @author Raffael Herzog
 */
public final class TextRange {

    private final CharSequence source;
    private final int start;
    private final int length;

    private TextRange(CharSequence source, int start, int length) {
        this.source = source;
        this.start = start;
        this.length = length;
    }

    public static TextRange ofStartAndLength(CharSequence source, int start, int length) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(length >= 0, "length >= 0");
        return new TextRange(source, start, length);
    }

    public static TextRange ofStartAndEnd(CharSequence source, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end >= start, "end >= start");
        return new TextRange(source, start, end - start);
    }

    public static TextRange ofAll(CharSequence source) {
        return new TextRange(source, 0, source.length());
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public int getEnd() {
        return start + length;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public String getText() {
        return source.subSequence(getStart(), getEnd()).toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getStart() + "-" + getEnd() + ":" + getLength() + "]";
    }
}
