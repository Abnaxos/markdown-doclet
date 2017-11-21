package ch.raffael.mddoclet.core.ast;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.raffael.nullity.Nullable;


/**
 * @author Raffael Herzog
 */
final class Lexer {

    private static final String WS = "([\\s&&[^\\n\\r]])";
    private static final Pattern COMMENT_START = Pattern.compile("\\s*/\\*\\*");
    private static final Pattern NEWLINE = Pattern.compile("\\n|\\r\\n?");
    private static final Pattern LEAD = Pattern.compile(WS + "*\\*" + WS + "?");
    private static final Pattern TEXT = Pattern.compile("[^\\n\\r]+");

    private final String input;
    private final Matcher matcher;

    private boolean startOfComment;
    private boolean startOfLine;
    @Nullable
    private DocNode commentEndDocNode = null;

    Lexer(String input, boolean hasCommentDelimiters) {
        this.input = input;
        matcher = LEAD.matcher(input);
        startOfLine = true;
        startOfComment = hasCommentDelimiters;
    }

    List<DocNode> toList() {
        List<DocNode> nodes = new ArrayList<>();
        for(DocNode node; (node=next())!=null;) {
            nodes.add(node);
        }
        return nodes;
    }

    @Nullable
    DocNode next() {
        if (matcher.regionStart() >= matcher.regionEnd()) {
            DocNode docNode = commentEndDocNode;
            commentEndDocNode = null;
            return docNode;
        }
        if (startOfComment) {
            startOfComment = false;
            DocNode startDocNode = null;
            int commentEndPos = 0;
            if (matcher.usePattern(COMMENT_START).lookingAt()) {
                startOfLine = false;
                commentEndPos = matcher.end();
                startDocNode = createNode(DocNode.Type.COMMENT_START);
            }
            commentEndPos = input.indexOf("*/", commentEndPos);
            if (commentEndPos >= 0) {
                matcher.region(matcher.regionStart(), commentEndPos);
                commentEndDocNode = new DocNode(DocNode.Type.COMMENT_END, TextRange.ofStartAndEnd(input, matcher.regionEnd(), input.length()));
            }
            if (startDocNode != null) {
                return startDocNode;
            }
        }
        if (startOfLine) {
            startOfLine = false;
            if (matcher.usePattern(LEAD).lookingAt()) {
                return createNode(DocNode.Type.LEAD);
            }
        }
        if (matcher.usePattern(NEWLINE).lookingAt()) {
            startOfLine = true;
            return createNode(DocNode.Type.NEWLINE);
        } else if (matcher.usePattern(TEXT).lookingAt()) {
            return createNode(DocNode.Type.TEXT);
        } else {
            // This cannot happen. Theoretically.
            throw new AssertionError("lookingAt() false for both NEWLINE and TEXT, which is impossible");
        }
    }

    private DocNode createNode(DocNode.Type type) {
        DocNode docNode = new DocNode(type, TextRange.ofStartAndEnd(input, matcher.start(), matcher.end()));
        matcher.region(matcher.end(), matcher.regionEnd());
        return docNode;
    }

}
