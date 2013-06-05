Pegdown Doclet
==============

A Doclet that allows the use of Markdown and [PlantUML](http://plantuml.sourceforge.net/) in JavaDoc comments. It uses [Pegdown](http://www.pegdown.org/) as Markdown processor. It's a simple preprocessor to the standard Doclet: It processes all JavaDoc comments in the documentation tree and then forwards the result to the standard Doclet. The JavaDoc output by this Doclet for this Doclet can be found [here](http://projects.raffael.ch/pegdown-doclet).

This Doclet is released under the [GPL 3.0](http://www.gnu.org/licenses/gpl-3.0-standalone.html).


Leading Spaces
--------------

Sometimes, leading whitespaces are significant in Markdown. Because of the way we usually write JavaDoc comments and the way JavaDoc is implemented, this may lead to some problems:

```
/**
 * Title
 * =====
 *
 * Text
 */
```

In this example, each line has one leading space. Because of this, the title won't be recognised as such by Pegdown. To work around this problem, the Doclet uses a simple trick: The first leading space character (the *actual* space character, i.e. `\\u0020`) will be cut off, if it exists.

This may be important e.g. for code blocks, which should be indented by 4 spaces: Well, it's 5 spaces now. ;)

**Note:** If an `overview.md` file is specified, leading spaces will be treated normally in this file. The first space will *not* be ignored.

This behaviour is currently *not* customisable.


Javadoc Tags
------------

The following known tags handled by Pegdown so you can use Markup with them:

 *  `@author`
 *  `@version`
 *  `@return`
 *  `@deprecated`
 *  `@since`
 *  `@param`
 *  `@throws`

### `@see` Tags

The `@see` tag is a special case, as there are several variants of this tag. These two variants will remain unchanged:

 *  Javadoc-Links: `@see Foo#bar()`
 *  Links: `@see <a href="http://www.example.com/">Example</a>`

The third variant however, which is originally meant to refer to a printed book, may also contain Markdown-style links:

 *  `@see "[Example](http://www.example.com/)"`
 *  `@see "<http://www.example.com/>"`
 *  `@see "Example <http://www.example.com/>"`
 *  `@see "[[http://www.example.com/]]"`
 *  `@see "[[http://www.example.com/ Example]]"`

These are all rendered as `@see <a href="http://www.example.com/">LABEL</a>`, where LABEL falls back to the link's URL, if no label is given.

**Warning:** Version 1.2 of this doclet will redefine Wiki-Style links (see [issue #7](https://github.com/Abnaxos/pegdown-doclet/issues/7)). It's recommended not to use them for now.

### Custom Tag Handling

Tag handling can be customised by implementing your own `TagRenderer`s and registering them with the PegdownDoclet. You'll have to write your own Doclet, though, there's currently no way to do this using the command line. See the JavaDocs and sources for details on this.

This currently only works for block tags.

### Inline Tags

Inline tags will be removed before processing the Markdown source and re-inserted afterwards. Therefore, markup within inline tags won't work.

There's currently no way to customise this behaviour or customize the way inline tags are rendered back into the processed doc comment.


PlantUML
--------

This Doclet has built-in support for PlantUML. Just use the `@uml` tag:

```
/**
 * Description.
 *
 * ![Example Diagram](example.png)
 *
 * @uml example.png
 * Alice -> Bob: Authentication Request
 * Bob --> Alice: Authentication Response
 */
```

It's also possible to use `@startuml` and `@enduml` instead, as usual. `@startuml` is simply a synonym for `@uml` and `@enduml` will be ignored entirely. Use this for compatibility with other tools, like e.g. the [PlantUML IDEA Plugin](https://github.com/esteinberg/plantuml4idea).


Syntax Highlighting
-------------------

The Pegdown Doclet integrates [highlight.js](http://softwaremaniacs.org/soft/highlight/en/) to enable syntax highlighting in code examples. See "Fenced code blocks" below for details.


Invoking
--------

Specify the Doclet on JavaDoc's command line:

```
javadoc -doclet ch.raffael.doclets.pegdown.PegdownDoclet -docletpath /path/to/pegdown-doclet-1.1-all.jar
```

It supports all options the standard Doclet supports and some additional options:

 *  *`-extensions <ext>`*: Specify the Pegdown extensions. The extensions list a comma separated list of constants as specified in [org.pegdown.Extensions](http://www.decodified.com/pegdown/api/org/pegdown/Extensions.html), converted to upper case and '-' replaced by '_'. The default is `autolinks,definitions,smartypants,tables,wikilinks`.

 *  *`-overview <page>`*: Specify an overview page. This is basically the same as with the standard Doclet, however, the specified page will be rendered using Pegdown

 *  *`-plantuml-config <file>`*: A configuration file that will be included before each diagram.

 *  *`-highlight-style <style>`*: The style to be used for syntax highlighting.

 *  *`-disable-highlight`*: Disable syntax highlighting entirely.

 *  *`-enable-auto-highlight`*: Enable auto-highlighting. If no language is specified in code blocks, the highlighter will try to guess the correct language.

 *  *`-todo-title`*: Set the title of TODO boxes.

 *  *`-parse-timeout <seconds>`*: Set the parse timeout for Pegdown. The default is 2 seconds. Try raising the parse timeout if you encounter timeout errors when generating your JavaDocs. You may also specify fractions of seconds (e.g. 2.5).

### Maven

Add the following to your POM to use the doclet with Maven:

```xml
<build>
  <plugins>
    <plugin>
      <artifactId>maven-javadoc-plugin</artifactId>
      <version>2.9</version>
      <configuration>
        <doclet>ch.raffael.doclets.pegdown.PegdownDoclet</doclet>
        <docletArtifact>
          <groupId>ch.raffael.pegdown-doclet</groupId>
          <artifactId>pegdown-doclet</artifactId>
          <version>1.1</version>
        </docletArtifact>
        <useStandardDocletOptions>true</useStandardDocletOptions>
      </configuration>
    </plugin>
  </plugins>
</build>
```

The doclet is available in Maven Central.


IDE support
-----------

There is a plugin that enables *Ctrl-Q* in [IntelliJ IDEA](http://www.jetbrains.com/idea/). Just download it from the plugin repository ("Settings -- Plugins -- Browse Repositories"), or build it yourself (see `integrations/idea-plugin/README.md`).

Use the [PlantUML integration plugin](http://plugins.jetbrains.com/plugin/7017?pr=idea) for a live preview while editing PlantUML diagrams. This only works if you use the "classic" `@startuml` and `@enduml` tags.

If you think your favourite IDE is missing, feel free to add a plugin for it and send me a pull request. ;)


Markdown Extensions
-------------------

 *  *Autolinks*: URLs are rendered as links automatically.

 *  *Definition lists*: Extended syntax for definition lists:

        Term
        : Definition of the term.

 *  *Fenced code blocks*: Fenced code blocks as known from GitHub:

        ```java
        public class FencedCodeBlock {
            public void cool() {
                // do something
            }
        }
        ```

    If no language is specified, no syntax highlighting will be applied to the code block (except if `-enable-auto-highlight` was specified on the command line, in which case the highlighter will try to guess the language).

 *  *Smartypants*: Typographic quotes, en- and em-dashes, ellipsis.

 *  *Tables*: Extended syntax for tables:

    ```
    Foo | Bar
    ----|----
    A   | B
    C   | D
    ```

 *  *Wiki-style links*: Prettier syntax for links: `[[http://www.google.com Link Title]]`

    **Warning:** Version 1.2 of this doclet will redefine Wiki-Style links (see [issue #7](https://github.com/Abnaxos/pegdown-doclet/issues/7)). It's recommended not to use them for now.
