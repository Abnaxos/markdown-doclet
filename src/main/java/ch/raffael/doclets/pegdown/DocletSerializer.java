package ch.raffael.doclets.pegdown;

import org.pegdown.LinkRenderer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.VerbatimNode;


/**
 * Customises the HTML rendering.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DocletSerializer extends ToHtmlSerializer {

    private final Options options;

    public DocletSerializer(Options options, LinkRenderer linkRenderer) {
        super(linkRenderer);
        this.options = options;
    }

    /**
     * Overrides the default implementation to set the language to "no-highlight" no
     * language is specified. If highlighting is disabled or auto-highlighting is enabled,
     * this method just calls the default implementation.
     *
     * @param node    The AST node.
     */
    @Override
    public void visit(VerbatimNode node) {
        if ( options.isHighlightEnabled() && !options.isAutoHighlightEnabled() && node.getType().isEmpty() ) {
            VerbatimNode noHighlightNode = new VerbatimNode(node.getText(), "no-highlight");
            noHighlightNode.setStartIndex(node.getStartIndex());
            noHighlightNode.setEndIndex(node.getEndIndex());
            super.visit(noHighlightNode);
        }
        else {
            super.visit(node);
        }
    }
}
