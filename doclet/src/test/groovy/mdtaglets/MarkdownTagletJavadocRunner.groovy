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

package mdtaglets

import ch.raffael.doclets.pegdown.PegdownDoclet

import javax.tools.ToolProvider
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
/**
 * TODO: 30.03.16 Javadoc
 */
final class MarkdownTagletJavadocRunner {

    final static List<Path> INPUT_PATHS = [Paths.get('src', 'main', 'java'), Paths.get('src', 'test', 'javadoc')].asImmutable()
    final static Path OUTPUT_PATH = Paths.get('target', 'test-taglets')

    static void cleanTargetPath() {
        OUTPUT_PATH.toFile().delete();
    }

    boolean generateJavadoc(List<Class<?>> classes=[], List<String> additionalOptions=[]) {
        def sourceFiles = sourceFiles(classes)
        return runJavadoc(sourceFiles, additionalOptions)
    }

    private synchronized boolean runJavadoc(Set<File> sourceFiles, List<String> additionalOptions) {
        println "[${MarkdownTagletJavadocRunner.simpleName}] Current working directory: ${System.properties['user.dir']}"
        println "[${MarkdownTagletJavadocRunner.simpleName}] Processing source files: $sourceFiles"
        println "[${MarkdownTagletJavadocRunner.simpleName}] Additional options(s): $additionalOptions"

        def javadocTool = ToolProvider.systemDocumentationTool
        def fileManager = javadocTool.getStandardFileManager(null, null, null)
        def standardOptions = ['-locale', 'en',
                               '-d', OUTPUT_PATH.toString(),
                               '-windowtitle', 'Pegdown MarkdownTaglet Test Javadoc',
        ]
        def task = javadocTool.getTask(null, fileManager, null, PegdownDoclet,
                standardOptions + additionalOptions,
                fileManager.getJavaFileObjectsFromFiles(sourceFiles))
        return task.call()
    }

    private static Set<File> sourceFiles(List<Class<?>> sources) {
        def sourceFiles = new LinkedHashSet<File>()
        if( sources.isEmpty() ) {
            INPUT_PATHS.each { path ->
                Files.find(path,
                        Integer.MAX_VALUE,
                        { Path path2, bfa -> Files.isRegularFile(path2) && path2.toString().endsWith('.java') }
                    ).forEach({sourceFiles.add(it.toFile())})
                }
        }
        else {
            def sourceFileNames=toFileNames(sources)
            INPUT_PATHS.each { path ->
                Files.find(path,
                        Integer.MAX_VALUE,
                        { Path path2, bfa -> Files.isRegularFile(path2) && isOneOf(path2, sourceFileNames) }
                ).forEach({sourceFiles.add(it.toFile())})
            }
        }
        return sourceFiles;
    }

    private static boolean isOneOf(Path file, Set<String> sourceFileNames) {
        sourceFileNames.any { source -> file.endsWith(source)}
    }

    private static Set<String> toFileNames(List<Class<?>> classes) {
        return new LinkedHashSet<String>(classes.collect { cls -> cls.name.replace('.',File.separator) + ".java" })
    }

}
