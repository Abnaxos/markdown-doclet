Pegdown Doclet
==============

A Doclet that allows the use of Markdown in JavaDoc comments. It uses [Pegdown](http://www.pegdown.org/) as Markdown processor. It's a simple preprocessor to the standard Doclet: It processes all JavaDoc comments in the documentation tree and then forwards the result to the standard Doclet.

This Doclet is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


Leading Spaces
--------------

Sometimes, leading whitespaces are significant in Markdown. Because of the way we usually write JavaDoc comments and the way JavaDoc is implemented, this may lead to some problems:

```
/**
 * Title
 * =====
 *
 * Text
 * /
```

In this example, each line has one leading space. Because of this, the title won't be recognised as such by Pegdown. To work around this problem, the Doclet uses a simple trick: The first leading space character (the *actual* space character, i.e. `\\u0020`) will be cut off, if it exists.

This may be important e.g. for code blocks, which should be indented by 4 spaces: Well, it's 5 spaces now. ;)

This behaviour is currently *not* customisable.


Javadoc Tags
------------

The following known tags handled by Pegdown so you can use Markup with them:

* `@author`
* `@version`
* `@return`
* `@deprecated`
* `@since`
* `@param`
* `@throws`

### `@see` Tags

The `@see` tag, is a special case, as there are several variants of this tag. These two variants will be unchanged:

* Javadoc-Links: `@see Foo#bar()`
* Links: `@see <a href="http://www.example.com/">Example</a>`

The third variant however, which is originally meant to refer to a printed book, may also contain Markdown-style links:

* `@see "[Example](http://www.example.com/)"`
* `@see "<http://www.example.com/>"`
* `@see "Example <http://www.example.com/>"`
* `@see "[[http://www.example.com/]]"`
* `@see "[[http://www.example.com/ Example]]"`

These are all rendered as `@see <a href="http://www.example.com/">LABEL</a>`, where LABEL falls back to the link's URL, if no label is given.

### Custom Tag Handling

Tag handling can be customised by implementing your own `TagRenderer`s and registering them with the PegdownDoclet. You'll have to write your own Doclet, though, there's currently no way to do this using the command line. Extending the PegdownDoclet is easy, though, it's been written with that in mind. See the JavaDocs and sources for details on this.


Doclet Options
--------------

* *-extensions <ext>*: Specify the Pegdown extensions. The extensions list a comma separated list of constants as specified in [org.pegdown.Extensions](http://www.decodified.com/pegdown/api/org/pegdown/Extensions.html), converted to upper case and '-' replaced by '_'. The default is `autolinks,definitions,smartypants,tables,wikilinks`.

* *-overview <page>*: Specify an overview page. This is basically the same as with the standard Doclet, however, the specified page will be rendered using Pegdown


Markdown Extensions
-------------------

* *Autolinks*: URLs are rendered as links automatically.

* *Definition lists*: Extended syntax for definition lists:

        Term
        : Definition of the term.

* *Fenced code blocks*: Fenced code blocks as known from GitHub:

        ```
        public class FencedCodeBlock {
            public void cool() {
                // do something
            }
        }
        ```

* *Smartypants*: Typographic quotes, en- and em-dashes, ellipsis.

* *Tables*: Extended syntax for tables:

    ```
    Foo | Bar
    ----|----
    A   | B
    C   | D
    ```

* *Wiki-style links*: Prettier syntax for links: `[[http://www.google.com Link Title]]`
