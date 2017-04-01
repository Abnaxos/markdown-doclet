/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
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
 *
 */
package ch.raffael.mddoclet.mdtaglet;

import java.util.ArrayList;
import java.util.List;

import ch.raffael.mddoclet.mdt.gist.GistMarkdownTaglet;

/**
 * StandardTaglets contains all Standard Taglets, which will be registered on {@link MarkdownTaglets}, without using
 * {@link MarkdownTaglet#OPT_MD_TAGLET}.
 *
 * @see GistMarkdownTaglet
 */
public final class StandardTaglets {
    private static final List<Class<? extends MarkdownTaglet>> STANDARD_TAGLETS= new ArrayList<Class<? extends MarkdownTaglet>>() {{
        add(GistMarkdownTaglet.class);
    }};

    /**
     * Register all standard markdown taglets on {@link MarkdownTaglets}.
     *
     * @param markdownTaglets the markdownTaglets instance.
     */
    static void registerStandardTaglets(MarkdownTaglets markdownTaglets)  {
        for (Class<? extends MarkdownTaglet> standardTaglet : STANDARD_TAGLETS) {
            try {
                markdownTaglets.registerMarkdownTaglet(standardTaglet.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Could no create instance of " + standardTaglet, e);
            }
        }
    }
}
