package testdocs

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Shared
import spock.lang.Specification


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
abstract class JavadocOutputSpecification extends Specification {

    final static String CHARSET = 'UTF-8'

    @Shared
    URI javadocBase

    def setupSpec() {
        for ( path in [ 'target/test-javadocs', 'doclet/target/test-javadocs' ] ) {
            def file = new File(path.replace('/', File.separator))
            if ( file.isDirectory() ) {
                javadocBase = file.toURI()
                break
            }
        }
        if ( javadocBase == null ) {
            throw new IOException('Cannot determine Javadoc base URL')
        }
        println "Using Javadoc base URL $javadocBase"
    }

    Document packageDoc(Class clazz) {
        def name = clazz.getName()
        int pos = name.lastIndexOf('.')
        assert pos > 0
        return parse(name.substring(0, pos).replace('.', '/')+'/package-summary')
    }

    Document doc(Class clazz) {
        parse(clazz.getName().replace('.', '/'))
    }

    Document parse(String path) {
        Jsoup.parse(new File(javadocBase.resolve(path.replace('.', '/') + '.html')), CHARSET)

    }

}
