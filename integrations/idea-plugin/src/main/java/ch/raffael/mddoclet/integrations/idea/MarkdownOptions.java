/*
 * Copyright 2013 Raffael Herzog
 *
 * This file is part of markdown-doclet.
 *
 * markdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with markdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.raffael.mddoclet.integrations.idea;

import java.util.Objects;

import ch.raffael.mddoclet.Options;

import static org.pegdown.Extensions.ABBREVIATIONS;
import static org.pegdown.Extensions.ANCHORLINKS;
import static org.pegdown.Extensions.ATXHEADERSPACE;
import static org.pegdown.Extensions.AUTOLINKS;
import static org.pegdown.Extensions.DEFINITIONS;
import static org.pegdown.Extensions.EXTANCHORLINKS;
import static org.pegdown.Extensions.FENCED_CODE_BLOCKS;
import static org.pegdown.Extensions.FORCELISTITEMPARA;
import static org.pegdown.Extensions.QUOTES;
import static org.pegdown.Extensions.RELAXEDHRULES;
import static org.pegdown.Extensions.SMARTS;
import static org.pegdown.Extensions.STRIKETHROUGH;
import static org.pegdown.Extensions.SUPPRESS_HTML_BLOCKS;
import static org.pegdown.Extensions.SUPPRESS_INLINE_HTML;
import static org.pegdown.Extensions.TABLES;
import static org.pegdown.Extensions.TASKLISTITEMS;
import static org.pegdown.Extensions.WIKILINKS;


/**
 * A holder for the markdown options.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("WeakerAccess")
public class MarkdownOptions {

    /**
     * Flag indicating whether Markdown is enabled or not. If `null`, inherit from project.
     */
    public Boolean enabled;

    /**
     * The rendering options for Markdown. If `null`, inherit from project.
     */
    public RenderingOptions renderingOptions;

    public MarkdownOptions() {
    }

    /**
     * Creates a deep copy from the given options.
     *
     * @param that    The options to copy.
     */
    public MarkdownOptions(MarkdownOptions that) {
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
        return "MarkdownOptions{enabled=" + enabled + ",renderingOptions=" + renderingOptions + "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        MarkdownOptions that = (MarkdownOptions)o;
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
    public static class RenderingOptions {

        public boolean smarts = true;
        public boolean quotes = true;
        public boolean abbreviations = false;
        public boolean autolinks = true;
        public boolean tables = true;
        public boolean definitions = true;
        public boolean fencedCodeBlocks = true;
        public boolean wikiLinks = true;
        public boolean strikethrough = true;
        public boolean anchorLinks = false;
        public boolean suppressHtmlBlocks = false;
        public boolean suppressInlineHtml = false;
        public boolean atxHeaderSpace = false;
        public boolean forceListItemPara = false;
        public boolean relaxedHRules = false;
        public boolean taskListItems = true;
        public boolean extAnchorLinks = false;

        public RenderingOptions() {
        }

        /**
         * Creates a copy of the given rendering options.
         *
         * @param that    The rendering options to copy.
         */
        public RenderingOptions(RenderingOptions that) {
            this.quotes = that.quotes;
            this.smarts = that.smarts;
            this.abbreviations = that.abbreviations;
            this.autolinks = that.autolinks;
            this.tables = that.tables;
            this.definitions = that.definitions;
            this.fencedCodeBlocks = that.fencedCodeBlocks;
            this.wikiLinks = that.wikiLinks;
            this.strikethrough = that.strikethrough;
            this.anchorLinks = that.anchorLinks;
            this.suppressHtmlBlocks = that.suppressHtmlBlocks;
            this.suppressInlineHtml = that.suppressInlineHtml;
            this.atxHeaderSpace = that.atxHeaderSpace;
            this.forceListItemPara = that.forceListItemPara;
            this.relaxedHRules = that.relaxedHRules;
            this.taskListItems = that.taskListItems;
            this.extAnchorLinks = that.extAnchorLinks;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder("{");
            boolean first = true;
            first = opt(buf, "smarts", smarts, first);
            first = opt(buf, "quotes", quotes, first);
            first = opt(buf, "abbreviations", abbreviations, first);
            first = opt(buf, "autolinks", autolinks, first);
            first = opt(buf, "tables", tables, first);
            first = opt(buf, "definitions", definitions, first);
            first = opt(buf, "fencedCodeBlocks", fencedCodeBlocks, first);
            first = opt(buf, "wikiLinks", wikiLinks, first);
            first = opt(buf, "strikethrough", strikethrough, first);
            first = opt(buf, "anchorLinks", anchorLinks, first);
            first = opt(buf, "suppressHtmlBlocks", suppressHtmlBlocks, first);
            first = opt(buf, "suppressInlineHtml", suppressInlineHtml, first);
            first = opt(buf, "atxHeaderSpace", atxHeaderSpace, first);
            first = opt(buf, "forceListItemPara", forceListItemPara, first);
            first = opt(buf, "relaxedHRules", relaxedHRules, first);
            first = opt(buf, "taskListItems", taskListItems, first);
            first = opt(buf, "extAnchorLinks", extAnchorLinks, first);
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

        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            RenderingOptions that = (RenderingOptions)o;
            return smarts == that.smarts &&
                    quotes == that.quotes &&
                    abbreviations == that.abbreviations &&
                    autolinks == that.autolinks &&
                    tables == that.tables &&
                    definitions == that.definitions &&
                    fencedCodeBlocks == that.fencedCodeBlocks &&
                    wikiLinks == that.wikiLinks &&
                    strikethrough == that.strikethrough &&
                    anchorLinks == that.anchorLinks &&
                    suppressHtmlBlocks == that.suppressHtmlBlocks &&
                    suppressInlineHtml == that.suppressInlineHtml &&
                    atxHeaderSpace == that.atxHeaderSpace &&
                    forceListItemPara == that.forceListItemPara &&
                    relaxedHRules == that.relaxedHRules &&
                    taskListItems == that.taskListItems &&
                    extAnchorLinks == that.extAnchorLinks;
        }

        @Override
        public int hashCode() {
            return Objects.hash(smarts, quotes, abbreviations, autolinks, tables, definitions, fencedCodeBlocks,
                    wikiLinks, strikethrough, anchorLinks, suppressHtmlBlocks, suppressInlineHtml, atxHeaderSpace,
                    forceListItemPara, relaxedHRules, taskListItems, extAnchorLinks);
        }

        public void applyTo(Options options) {
            int ext = 0;
            ext = ext(ext, smarts, SMARTS);
            ext = ext(ext, quotes, QUOTES);
            ext = ext(ext, abbreviations, ABBREVIATIONS);
            ext = ext(ext, autolinks, AUTOLINKS);
            ext = ext(ext, tables, TABLES);
            ext = ext(ext, definitions, DEFINITIONS);
            ext = ext(ext, fencedCodeBlocks, FENCED_CODE_BLOCKS);
            ext = ext(ext, wikiLinks, WIKILINKS);
            ext = ext(ext, strikethrough, STRIKETHROUGH);
            ext = ext(ext, anchorLinks, ANCHORLINKS);
            ext = ext(ext, suppressHtmlBlocks, SUPPRESS_HTML_BLOCKS);
            ext = ext(ext, suppressInlineHtml, SUPPRESS_INLINE_HTML);
            ext = ext(ext, atxHeaderSpace, ATXHEADERSPACE);
            ext = ext(ext, forceListItemPara, FORCELISTITEMPARA);
            ext = ext(ext, relaxedHRules, RELAXEDHRULES);
            ext = ext(ext, taskListItems, TASKLISTITEMS);
            ext = ext(ext, extAnchorLinks, EXTANCHORLINKS);
            options.setPegdownExtensions(ext);
        }

        private int ext(int current, boolean flag, int ext) {
            if ( flag ) {
                return current | ext;
            }
            else {
                return current;
            }
        }

    }

}
