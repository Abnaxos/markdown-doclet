package testdocs

import ch.raffael.doclets.pegdown.PegdownDoclet

import javax.tools.ToolProvider
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * TODO: 30.03.16 Javadoc
 */
final class JavadocRunner {

    final static List<Path> INPUT_PATHS = [Paths.get('src', 'main', 'java'), Paths.get('src', 'test', 'javadoc')].asImmutable()
    final static Path OUTPUT_PATH = Paths.get('target', 'test-javadocs')

    final static instance = new JavadocRunner()
    private boolean didGenerate = false

    synchronized void generateDocs() {
        if ( didGenerate ) {
            return
        }
        didGenerate = true
        println "[${JavadocRunner.simpleName}] Current working directory: ${System.properties['user.dir']}"
        def sourceFiles = new LinkedHashSet<File>()
        INPUT_PATHS.each { p ->
            Files.find(p, Integer.MAX_VALUE,
                       { f, a -> Files.isRegularFile(f) && f.toString().endsWith('.java') }).
                    forEach({ sourceFiles.add it.toFile() })
        }
        def javadocTool = ToolProvider.systemDocumentationTool
        def fileManager = javadocTool.getStandardFileManager(null, null, null)
        def task = javadocTool.getTask(null, fileManager, null, PegdownDoclet,
                                       [ '-locale', 'en',
                                         '-d', OUTPUT_PATH.toString(),
                                         '-windowtitle', 'Pegdown Doclet Test Javado',
                                         '-overview', Paths.get('src', 'test', 'javadoc', 'overview.md').toString(),
                                         '-link', 'http://docs.oracle.com/javase/7/docs/api/',
                                         '-link', 'http://docs.oracle.com/javase/7/docs/jdk/api/javadoc/doclet',
                                         '-link', 'http://www.decodified.com/pegdown/api',
                                       ],
                                       fileManager.getJavaFileObjectsFromFiles(sourceFiles))
        task.call()
    }

}
