/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
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
 *
 */

package ch.raffael.doclets.pegdown.pdrepair;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InlineTagletRepair handle issues with inline taglets like &#123;@link ...&#125; .
 */
final class InlineTagletRepair implements PegdownRepair {

    private static final String STORED_MARKER = "-?-";
    private static final String MARKER = "{" + STORED_MARKER +"}";
                                                             // {@tag ...}        or the marker '{-?-}'
    private static final Pattern SUBST_REGEX=Pattern.compile("\\{(?<tag>@[^}]+)\\}|\\{-\\?-\\}");
    private static final Pattern RESTORE_REGEX =Pattern.compile("\\{-\\?-\\}");

    private final List<String> tags;

    InlineTagletRepair() {
        this(new LinkedList<String>());
    }

    InlineTagletRepair(List<String> tags) {
        this.tags = tags;
    }




    @Override
    public String beforePegdownParser(String markdown) {
        final StringBuffer result=new StringBuffer();
        final Matcher matcher=SUBST_REGEX.matcher(markdown);
        while ( matcher.find() )  {
            final String tag=matcher.group("tag");

            // store the tags content or the marker
            tags.add(tagContentOrMarker(tag));

            // Replace the tag with MARKER
            matcher.appendReplacement(result, MARKER);
        }

        matcher.appendTail(result);

        return result.toString();
    }

    private String tagContentOrMarker(String tag) {
        if( tag!=null ) {
            return tag;
        }
        return STORED_MARKER;
    }

    @Override
    public String afterPegdownParser(String markup) {
        final StringBuffer result=new StringBuffer();
        final Matcher matcher= RESTORE_REGEX.matcher(markup);
        while ( matcher.find() )  {
            String tag=STORED_MARKER;
            if( ! tags.isEmpty() ) {
                tag=tags.remove(0);
            }

            matcher.appendReplacement(result, "{" + tag + "}");
        }

        matcher.appendTail(result);

        return result.toString();
    }
}
