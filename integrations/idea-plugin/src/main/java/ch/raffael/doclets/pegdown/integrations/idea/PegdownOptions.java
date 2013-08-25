/*
 * Copyright 2013 Raffael Herzog
 *
 * This file is part of pegdown-doclet.
 *
 * pegdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pegdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pegdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.raffael.doclets.pegdown.integrations.idea;

import org.pegdown.Extensions;

import ch.raffael.doclets.pegdown.Options;
import lombok.EqualsAndHashCode;


/**
 * A holder for the pegdown options.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownOptions {

    /**
     * Flag indicating whether Pegdown is enabled or not. If `null`, inherit from project.
     */
    public Boolean enabled;

    /**
     * The rendering options for Pegdown. If `null`, inherit from project.
     */
    public RenderingOptions renderingOptions;

    public PegdownOptions() {
    }

    /**
     * Creates a deep copy from the given options.
     *
     * @param that    The options to copy.
     */
    public PegdownOptions(PegdownOptions that) {
        this.enabled = that.enabled;
        if ( that.renderingOptions == null ) {
            this.renderingOptions = null;
        }
        else {
            this.renderingOptions = new RenderingOptions(that.renderingOptions);
        }
    }

    @Override
    public String toString() {
        return "PegdownConfiguration{enabled=" + enabled + ",renderingOptions=" + renderingOptions + "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        PegdownOptions that = (PegdownOptions)o;
        if ( enabled != null ? !enabled.equals(that.enabled) : that.enabled != null ) {
            return false;
        }
        if ( renderingOptions != null ? !renderingOptions.equals(that.renderingOptions) : that.renderingOptions != null ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = enabled != null ? enabled.hashCode() : 0;
        result = 31 * result + (renderingOptions != null ? renderingOptions.hashCode() : 0);
        return result;
    }

    /**
     * Holds the rendering options.
     */
    @EqualsAndHashCode
    public static class RenderingOptions {

        public boolean autolinks = true;
        public boolean definitions = true;
        public boolean quotes = true;
        public boolean smarts = true;
        public boolean tables = true;
        public boolean wikiLinks = true;
        public boolean fencedCodeBlocks = true;
        public boolean abbreviations = false;
        public boolean noHtmlBlocks = false;
        public boolean noInlineHtml = false;

        public RenderingOptions() {
        }

        /**
         * Creates a copy of the given rendering options.
         *
         * @param that    The rendering options to copy.
         */
        public RenderingOptions(RenderingOptions that) {
            this.autolinks = that.autolinks;
            this.definitions = that.definitions;
            this.quotes = that.quotes;
            this.smarts = that.smarts;
            this.tables = that.tables;
            this.wikiLinks = that.wikiLinks;
            this.fencedCodeBlocks = that.fencedCodeBlocks;
            this.abbreviations = that.abbreviations;
            this.noHtmlBlocks = that.noHtmlBlocks;
            this.noInlineHtml = that.noInlineHtml;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder("{");
            boolean first = true;
            first = opt(buf, "autolinks", autolinks, first);
            first = opt(buf, "definitions", definitions, first);
            first = opt(buf, "quotes", quotes, first);
            first = opt(buf, "smarts", smarts, first);
            first = opt(buf, "tables", tables, first);
            first = opt(buf, "wikiLinks", wikiLinks, first);
            first = opt(buf, "fencedCodeBlocks", fencedCodeBlocks, first);
            first = opt(buf, "abbreviations", abbreviations, first);
            first = opt(buf, "noHtmlBlocks", noHtmlBlocks, first);
            first = opt(buf, "noInlineHtml", noInlineHtml, first);
            buf.append('}');
            return buf.toString();
        }

        private boolean opt(StringBuilder buf, String name, boolean value, boolean first) {
            if ( value ) {
                if ( !first ) {
                    buf.append(',');
                }
                buf.append(name);
                return false;
            }
            else {
                return first;
            }
        }

        public void applyTo(Options options) {
            options.setPegdownExtensions(
                    (autolinks ? Extensions.AUTOLINKS : 0)
                            | (definitions ? Extensions.DEFINITIONS : 0)
                            | (quotes ? Extensions.QUOTES : 0)
                            | (smarts ? Extensions.SMARTS : 0)
                            | (tables ? Extensions.TABLES : 0)
                            | (wikiLinks ? Extensions.WIKILINKS : 0)
                            | (fencedCodeBlocks ? Extensions.FENCED_CODE_BLOCKS : 0)
                            | (abbreviations ? Extensions.ABBREVIATIONS : 0)
                            | (noHtmlBlocks ? Extensions.SUPPRESS_HTML_BLOCKS : 0)
                            | (noInlineHtml ? Extensions.SUPPRESS_INLINE_HTML : 0)
            );
        }
    }

}
