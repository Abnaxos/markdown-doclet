package testdocs

import testdocs.issue44_topTitleNotProcessed.Issue44_TopTitleNotProcessed


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class IssuesSpec extends JavadocOutputSpecification {

    def "(#44) Markdown is being processed correctly in package-info.java"() {
      given:
        def doc = packageDoc(Issue44_TopTitleNotProcessed)

      expect:
        doc.select('div.contentContainer div.block h1').text() == 'November 2015, Java Users Group of Greater Louisville'
    }

    def "(#44) The first header should be used as documentation summary"() {
      given:
        def doc = packageDoc(Issue44_TopTitleNotProcessed)

      expect:
        doc.select('div.header div.docSummary div.block').text() == 'November 2015, Java Users Group of Greater Louisville'
    }

}
