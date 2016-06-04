/*
 * Copyright 2013-2016 Raffael Herzog / Marko Umek
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


package ch.raffael.doclets.pegdown.inlinetag.gist

import spock.lang.Specification

/**
 * GistTagContentConverterSpec contains specification for ... .
 */
class GistTagContentConverterSpec extends Specification {
    def "What is the render result from one gist?"() {
        given: "gist client"
        def gistClient=Stub(GistClient) {
            it.resolveGists(_) >> { String gistid ->
                [                                                                                       //
                    new GistItem(                                                                       //
                            gistid,                                                                     //
                            "https://gist.github.com/${gistid}",                                        //
                            'GeofenceListenerImpl.java',                                                //
                            'Java',                                                                     //
                            "https://gist.githubusercontent.com/${gistid}/GeofenceListenerImpl.java",   //
                            'Any content of GeofenceListenerImpl.java\n\nA new line.'                   //
                    )
                ]
            }
        }

        and: "a gist markdown"
        def gistMarkdown=new GistTagContentConverter(gistClient)

        when: "applying markdown converter"
        def markdown=gistMarkdown.markdown("123456")

        then: "one code markdown with two links (one to the gist and the second to the raw file)"
        markdown == """
                      |```java
                      |Any content of GeofenceListenerImpl.java
                      |
                      |A new line.
                      |```
                      |[Gist on Github](https://gist.github.com/123456) and [Raw File GeofenceListenerImpl.java](https://gist.githubusercontent.com/123456/GeofenceListenerImpl.java)
                    """.stripMargin().trim()
    }


    def "What is the render result from multiple gists?"() {
        given: "gist client"
        def gistClient=Stub(GistClient) {
            it.resolveGists(_) >> { String gistid ->
                [
                    new GistItem(gistid,
                            "https://gist.github.com/${gistid}",                                         //
                            'GeofenceListenerImpl.java',                                                 //
                            'Java',                                                                      //
                            "https://gist.githubusercontent.com/${gistid}/GeofenceListenerImpl.java",    //
                            'A example in Java'                                                          //
                    ),                                                                                   //
                    new GistItem(gistid,                                                                 //
                            "https://gist.github.com/${gistid}",                                         //
                            'GeofenceListenerImpl.scala',                                                //
                            'Scala',                                                                     //
                            "https://gist.githubusercontent.com/${gistid}/GeofenceListenerImpl.scala",   //
                            'A example in Scala'                                                         //
                    )                                                                                    //
                ]
            }
        }

        and: "a gist markdown"
        def gistMarkdown=new GistTagContentConverter(gistClient)

        when: "applying markdown converter"
        def markdown=gistMarkdown.markdown("123456")

        then: "should result in multiple code markdowns and links"
        markdown == """
                    |```java
                    |A example in Java
                    |```
                    |[Gist on Github](https://gist.github.com/123456) and [Raw File GeofenceListenerImpl.java](https://gist.githubusercontent.com/123456/GeofenceListenerImpl.java)
                    |
                    |```scala
                    |A example in Scala
                    |```
                    |[Gist on Github](https://gist.github.com/123456) and [Raw File GeofenceListenerImpl.scala](https://gist.githubusercontent.com/123456/GeofenceListenerImpl.scala)
                    """.stripMargin().trim()
    }
}