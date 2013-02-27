/*
 * Copyright 2013 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.raffael.doclets.pegdown;

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
