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
package ch.raffael.mddoclet.mdrepair;

/**
 * DefaultMarkdownRepair provides default implementation.
 */
abstract class DefaultMarkdownRepair implements MarkdownRepair {

    @Override
    public String afterMarkdownParser(String markup) {
        return markup;
    }

    @Override
    public String beforeMarkdownTaglets(String markdown) {
        return markdown;
    }

    @Override
    public String beforeMarkdownParser(String markdown) {
        return markdown;
    }
}
