package ch.raffael.doclets.pegdown;

import com.sun.javadoc.Tag;


/**
 * A renderer for the newly introduced `todo` tag.
 *
 * @todo This is just an example.
 *
 * You can use any content in here, like:
 *
 * ```java
 * public void sayHello() {
 *     System.out.println("Hello World");
 * }
 * ```
 *
 * @todo Several TODOs are possible.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TodoTagRenderer implements TagRenderer<Tag> {

    public static final TodoTagRenderer INSTANCE = new TodoTagRenderer();

    /**
     * Render the tag.
     *
     * @todo TODOs can also be used in member docs ...
     *
     * @param tag       The tag to render.
     * @param target    The target {@link StringBuilder}.
     * @todo ... or even parameter docs -- which is a little strange, actually. ;)
     * @param doclet    The doclet.
     */
    @Override
    public void render(Tag tag, StringBuilder target, PegdownDoclet doclet) {
        target.append("<div class=\"todo\">");
        target.append("<div class=\"todoTitle\"><span class=\"todoTitle\"></span><span class=\"todoCounter\"></span></div>");
        target.append("<div class=\"todoText\">");
        target.append(doclet.toHtml(tag.text().trim()));
        target.append("</div></div>");
    }
}
