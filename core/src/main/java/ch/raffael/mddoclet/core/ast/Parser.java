package ch.raffael.mddoclet.core.ast;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

import ch.raffael.nullity.Nullable;


/**
 * @author Raffael Herzog
 */
final class Parser {

    private final String input;
    private final Lexer lexer;
    private final RootDocNode root;

    private final Deque<DocNodeList> stack = new ArrayDeque<>();

    Parser(String input, boolean includesCommentDelimiters) {
        this.input = input;
        lexer = new Lexer(input, includesCommentDelimiters);
        root = RootDocNode.create(TextRange.ofAll(input));
        stack.push(root.getContent());
    }

    RootDocNode parse() {
        for(DocNode node; (node=lexer.next())!=null;) {
            switch (node.getType()) {
            case TEXT:
            case WHITESPACE:
            case NEWLINE:
            case LEAD:
                content().add(node);
                break;
            case COMMENT_START:
                expect(content().getOwner(), "on stack", DocNode.Type.ROOT);
                content().add(node);
                break;
            case COMMENT_END:
                cancelAllTags(node.getTextRange().getStart());
                expect(content().getOwner(), "on stack", DocNode.Type.ROOT);
                content().add(node);
                break;
            case INLINE_TAG_START_MARKER:
                startTag(node, TagDocNode::createInlineTag);
                break;
            case INLINE_TAG_END_MARKER:
                closeInlineTag(node);
                break;
            case BLOCK_TAG_START_MARKER:
                cancelAllTags(node.getTextRange().getStart());
                startTag(node, TagDocNode::createBlockTag);
                break;
            default:
                throw unexpectedNode(node);
            }
        }
        if (content().getOwner().getType().isTag()) {
            // we know that there is at least one children, we added it when we pushed the tag
            cancelAllTags(root.getContent().get(root.getContent().size() - 1).getTextRange().getEnd());
        }
        //noinspection ObjectEquality
        assert content().getOwner() == root && stack.size() == 1;
        return root;
    }

    private void startTag(DocNode node, Function<TextRange, TagDocNode> constructor) {
        TagDocNode tagNode = constructor.apply(node.getTextRange())
                .withStartMarker(node)
                .withName(expect(lexer.next(), DocNode.Type.TEXT));
        content().add(tagNode);
        stack.push(tagNode.getContent());
    }

    private void closeInlineTag(DocNode node) {
        TagDocNode tagNode = (TagDocNode)expect(stack.pop().getOwner(), "on stack", DocNode.Type.INLINE_TAG);
        tagNode.setEndMarker(node);
        tagNode.setTextRange(TextRange.ofStartAndEnd(input, tagNode.getTextRange().getStart(), node.getTextRange().getEnd()));
    }

    private void cancelAllTags(int end) {
        while (content().getOwner().getType().isTag()) {
            TagDocNode tagNode = (TagDocNode)stack.pop().getOwner();
            tagNode.setTextRange(TextRange.ofStartAndEnd(input, tagNode.getTextRange().getStart(), end));
        }
    }

    private DocNodeList content() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Empty node stack");
        }
        return stack.peek();
    }

    private <T extends DocNode> T expect(@Nullable T node, DocNode.Type type) {
        return expect(node, null, type);
    }

    private <T extends DocNode> T expect(@Nullable T node, @Nullable String location, DocNode.Type type) {
        if (node == null || node.getType() != type) {
            throw unexpectedNode(node, location, type);
        }
        return node;
    }

    private IllegalStateException unexpectedNode(@Nullable DocNode node) {
        return unexpectedNode(node, null, null);
    }

    private IllegalStateException unexpectedNode(@Nullable DocNode node, DocNode.Type type) {
        return unexpectedNode(node, null, type);
    }

    private IllegalStateException unexpectedNode(@Nullable DocNode node, @Nullable String location, @Nullable DocNode.Type type) {
        StringBuilder buf = new StringBuilder();
        buf.append("Unexpected node ").append(node);
        if (location != null) buf.append(' ').append(location);
        if (type != null) buf.append(", expected ").append(type);
        return new IllegalStateException(buf.toString());
    }

}
