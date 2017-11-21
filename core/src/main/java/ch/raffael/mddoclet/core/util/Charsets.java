package ch.raffael.mddoclet.core.util;

import java.nio.charset.Charset;

import ch.raffael.nullity.Nullable;


/**
 * Some utilities for choosing the charsets.
 *
 * @author Raffael Herzog
 */
public final class Charsets {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private Charsets() {
        throw new AssertionError("Utility class: " + getClass().getName());
    }

    public static Charset utf8() {
        return UTF8;
    }

    public static Charset defaultToUtf8(@Nullable String charsetName) {
        return charsetName == null ? utf8() : Charset.forName(charsetName);
    }

    public static Charset defaultToSystem(@Nullable String charsetName) {
        return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    public static Charset defaultToUtf8(@Nullable Charset charset) {
        return charset == null ? utf8() : charset;
    }

    public static Charset defaultToSystem(@Nullable Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

}
