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
package ch.raffael.doclets.pegdown.tags;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.io.Files;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.preproc.Defines;

import ch.raffael.doclets.pegdown.PegdownDoclet;


/**
 * A tag renderer that invokes PlantUML.
 *
 * ![Demo Diagram](demo.png)
 *
 * **Note:** This tag renderer is stateful and shouldn't be reused across several JavaDoc
 * runs.
 *
 * @uml demo.png
 * Alice -> Bob: Authentication Request
 * Bob --> Alice: Authentication Response
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UmlTagRenderer implements TagRenderer<Tag> {

    private List<String> config = null;

    @Override
    public void render(Tag tag, StringBuilder target, PegdownDoclet doclet) {
        if ( config == null ) {
            if ( doclet.getOptions().getPlantUmlConfigFile() != null ) {
                try {
                    config = Collections.singletonList(
                            Files.toString(doclet.getOptions().getPlantUmlConfigFile(),
                                           doclet.getOptions().getEncoding()));
                }
                catch ( IOException e ) {
                    doclet.printError("Error loading PlantUML configuration file " + doclet.getOptions().getPlantUmlConfigFile() + ": " + e.getLocalizedMessage());
                }
            }
            else {
                config = Collections.emptyList();
            }
        }
        String packageName;
        if ( tag.holder() instanceof ProgramElementDoc ) {
            packageName = ((ProgramElementDoc)tag.holder()).containingPackage().name();
        }
        else if ( tag.holder() instanceof PackageDoc ) {
            packageName = ((PackageDoc)(tag.holder())).name();
        }
        else if ( tag.holder() instanceof RootDoc ) {
            packageName = null;
        }
        else {
            doclet.printError(tag.position(), "Cannot handle tag for holder " + tag.holder());
            return;
        }
        String source = tag.text().trim();
        int pos = CharMatcher.WHITESPACE.indexIn(source);
        if ( pos < 0 ) {
            doclet.printError(tag.position(), "Invalid @startuml tag: Expected filename and PlantUML source");
            return;
        }
        String fileName = source.substring(0, pos);
        source = "@startuml " + fileName + "\n" + source.substring(pos).trim() + "\n@enduml";
        File outputFile;
        if ( packageName == null ) {
            outputFile = doclet.getOptions().getDestinationDir();
        }
        else {
            outputFile = new File(doclet.getOptions().getDestinationDir(), packageName.replace(".", File.separator));
        }
        outputFile.mkdirs();
        outputFile = new File(outputFile, fileName.replace("/", File.separator));
        doclet.printNotice("Generating UML diagram " + outputFile);
        // render
        SourceStringReader reader = new SourceStringReader(new Defines(), source, config);
        try {
            reader.generateImage(outputFile);
        }
        catch ( IOException e ) {
            doclet.printError(tag.position(), "Error generating UML image " + outputFile + ": " + e.getLocalizedMessage());
        }
    }
}
