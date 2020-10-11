package ch.raffael.mddoclet.jdk9;

import jdk.javadoc.doclet.StandardDoclet;


/**
 * Markdown doclet forwarding to the standard doclet.
 *
 * @author Raffael Herzog
 */
@SuppressWarnings("unused")
public class MdDoclet extends GenericMdDoclet {

    public MdDoclet() {
        super(new StandardDoclet());
    }

}
