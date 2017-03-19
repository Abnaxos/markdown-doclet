package testdocs

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Specification

import java.nio.file.Path


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
abstract class JavadocOutputSpecification extends Specification {

    final static String CHARSET = 'UTF-8'

    Path javadocBase

    def setupSpec() {
        JavadocRunner.instance.generateDocs()
//        for ( path in [ 'target/test-javadocs', 'doclet/target/test-javadocs' ] ) {
//            def file = new File(path.replace('/', File.separator))
//            if ( file.isDirectory() ) {
//                javadocBase = file.toURI()
//                break
//            }
//        }
//        if ( javadocBase == null ) {
//            throw new IOException('Cannot determine Javadoc base URL')
//        }
//        println "Using Javadoc base URL $javadocBase"
    }

    def setup() {
        javadocBase = JavadocRunner.OUTPUT_PATH
    }

    Document packageDoc(Class clazz) {
        //        int pos = name.lastIndexOf('.')
//        assert pos > 0
//        return parse(name.substring(0, pos).replace('.', '/')+'/package-summary')
        return parse(clazz.package.name+'.package-summary')
    }

    Document doc(Class clazz) {
        parse(clazz.getName())
    }

    Document parse(String path) {
        Jsoup.parse(javadocBase.resolve(path.replace('.', File.separator) + '.html').toFile(), CHARSET)

    }

}
