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

import java.util.ArrayList;
import java.util.List;

/**
 * PegdownRepairKit provides a repair kit for {@link org.pegdown.PegDownProcessor}.
 */
public final class PegdownRepairKit implements PegdownRepair {
    private final List<PegdownRepair> before=new ArrayList<>();
    private final List<PegdownRepair> after=new ArrayList<>();

    public PegdownRepairKit(boolean dropLeadingSpace) {
        final PegdownRepair spaceCharacterRepair = new SpaceCharacterRepair();
        final PegdownRepair inlineTagletPegdownRepair = new InlineTagletRepair();
        final PegdownRepair atCharacterRepair=new AtSymbolRepair();
        final PegdownRepair htmlEntitiesRepair=new HtmlEntitiesRepair();

        // before
        if( dropLeadingSpace ) {
            before.add(spaceCharacterRepair);
        }

        before.add(inlineTagletPegdownRepair);
        before.add(atCharacterRepair);
        before.add(htmlEntitiesRepair);

        // after
        after.add(htmlEntitiesRepair);
        after.add(atCharacterRepair);
        after.add(inlineTagletPegdownRepair);
        after.add(spaceCharacterRepair);
    }

    public String beforePegdownParser(String markdown) {
        String result=markdown;
        for (PegdownRepair pegdownRepair : before) {
            result = pegdownRepair.beforePegdownParser(result);
        }
        return result;
    }

    @Override
    public String afterPegdownParser(String markup) {
        String result=markup;
        for (PegdownRepair pegdownRepair : after) {
            result = pegdownRepair.afterPegdownParser(result);
        }
        return result;
    }
}
