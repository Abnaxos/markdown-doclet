package ch.raffael.doclets.pegdown.integrations.idea;

import ch.raffael.doclets.pegdown.Options;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownOptions {

    public Boolean enabled;
    public RenderingOptions renderingOptions;

    public PegdownOptions() {
    }

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


        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            RenderingOptions that = (RenderingOptions)o;
            if ( abbreviations != that.abbreviations ) {
                return false;
            }
            if ( autolinks != that.autolinks ) {
                return false;
            }
            if ( definitions != that.definitions ) {
                return false;
            }
            if ( fencedCodeBlocks != that.fencedCodeBlocks ) {
                return false;
            }
            if ( noHtmlBlocks != that.noHtmlBlocks ) {
                return false;
            }
            if ( noInlineHtml != that.noInlineHtml ) {
                return false;
            }
            if ( quotes != that.quotes ) {
                return false;
            }
            if ( smarts != that.smarts ) {
                return false;
            }
            if ( tables != that.tables ) {
                return false;
            }
            if ( wikiLinks != that.wikiLinks ) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = (autolinks ? 1 : 0);
            result = 31 * result + (definitions ? 1 : 0);
            result = 31 * result + (quotes ? 1 : 0);
            result = 31 * result + (smarts ? 1 : 0);
            result = 31 * result + (tables ? 1 : 0);
            result = 31 * result + (wikiLinks ? 1 : 0);
            result = 31 * result + (fencedCodeBlocks ? 1 : 0);
            result = 31 * result + (abbreviations ? 1 : 0);
            result = 31 * result + (noHtmlBlocks ? 1 : 0);
            result = 31 * result + (noInlineHtml ? 1 : 0);
            return result;
        }

        public void applyTo(Options options) {
        }
    }
}
