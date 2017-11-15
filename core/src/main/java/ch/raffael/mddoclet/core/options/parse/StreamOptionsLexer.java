package ch.raffael.mddoclet.core.options.parse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ch.raffael.mddoclet.core.util.Charsets;
import ch.raffael.nullity.Nullable;


/**
 * Reads options files.
 *
 * @author Raffael Herzog
 */
public class StreamOptionsLexer {

    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final CharStream stream;

    @SuppressWarnings("StringBufferField")
    private final StringBuilder word = new StringBuilder();


    public StreamOptionsLexer(Reader reader) {
        stream = new CharStream(reader);
    }

    public StreamOptionsLexer(InputStream inputStream) {
        this(inputStream, Charsets.utf8());
    }

    public StreamOptionsLexer(InputStream inputStream, Charset charset) {
        this(new InputStreamReader(inputStream, charset));
    }

    public StreamOptionsLexer(Path path) throws IOException {
        this(path, Charsets.utf8());
    }

    public StreamOptionsLexer(Path path, Charset charset) throws IOException {
        this(Files.newBufferedReader(path, charset));
    }

    public StreamOptionsLexer(File file) throws IOException {
        this(file, Charsets.utf8());
    }

    public StreamOptionsLexer(File file, Charset charset) throws IOException {
        this(file.toPath(), charset);
    }

    public List<Token> toTokenList() throws IOException {
        ArrayList<Token> tokenList = new ArrayList<>();
        Token token;
        while ((token = nextToken()) != null) {
            tokenList.add(token);
        }
        return tokenList;
    }

    public List<String> toWordList() throws IOException {
        ArrayList<String> wordList = new ArrayList<>();
        Token token;
        while ((token = nextToken()) != null) {
            wordList.add(token.getText());
        }
        return wordList;
    }

    @Nullable
    public Token nextToken() throws IOException {
        word.setLength(0);
        skipWhitespace();
        if (stream.eof()) {
            return null;
        }
        int startLine = stream.currentLine();
        if (stream.current() == '\'' || stream.current() == '"') {
            readQuotedWord();
        } else {
            readPlainWord();
        }
        return new Token(startLine, word.toString());
    }

    private void readPlainWord() throws IOException {
        word.append(stream.current());
        while (stream.next()) {
            if (Character.isWhitespace(stream.current())) {
                stream.pushback();
                break;
            }
            word.append(stream.current());
        }
    }

    private void readQuotedWord() throws IOException {
        int startLine = stream.currentLine();
        char quoteChar = stream.current();
        while (stream.next()) {
            if (stream.current() == '\\') {
                word.append(readEscape());
            } else if (stream.current() == quoteChar) {
                return;
            } else {
                word.append(stream.current());
            }
        }
        throw new ParseException(startLine, "Unterminated quoted string");
    }

    private char readEscape() throws IOException {
        if (!stream.next()) {
            throw new ParseException(stream.currentLine(), "Illegal escape sequence");
        }
        switch (stream.current()) {
        case '\\':
        case '"':
        case '\'':
            return stream.current();
        case 'r':
            return '\r';
        case 'n':
            return '\n';
        case 't':
            return '\t';
        case ' ':
            return '\u00a0';
        case 'f':
            return '\f';
        case 'u':
            return readUnicodeEscape();
        default:
            throw new ParseException(stream.currentLine(), "Illegal escape sequence");
        }
    }

    private char readUnicodeEscape() throws IOException {
        int codePoint = 0;
        for (int i = 0; i < 4; i++) {
            if (!stream.next()) {
                throw new ParseException(stream.currentLine(), "Illegal unicode escape sequence");
            }
            int digit = hexCharToInt(Character.toLowerCase(stream.current()));
            if (digit < 0) {
                throw new ParseException(stream.currentLine(), "Illegal unicode escape sequence");
            }
            codePoint = (codePoint << 4) | digit;
        }
        return (char)codePoint;
    }

    private static int hexCharToInt(char c) {
        for (int i = 0; i < HEX_CHARS.length; i++) {
            if (HEX_CHARS[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private void skipWhitespace() throws IOException {
        while (stream.next()) {
            if (!Character.isWhitespace(stream.current())) {
                if (stream.current() == '#') {
                    skipToEndOfLine();
                } else {
                    return;
                }
            }
        }
    }

    private void skipToEndOfLine() throws IOException {
        while (stream.next()) {
            if (stream.current() == '\n') {
                break;
            }
        }
    }

    private static final class CharStream {
        private final Reader reader;
        private boolean eofReached = false;

        @Nullable
        private Character currentChar;
        private int lineNumber = 1;
        private boolean hadCarriageReturn = false;
        private boolean didCountLine = false;

        @Nullable
        private Character pushbackChar = null;
        private boolean pushbackDidCountLine;

        private CharStream(Reader reader) {
            this.reader = reader;
        }

        private boolean next() throws IOException {
            if (pushbackChar == null) {
                if (eofReached) {
                    return false;
                }
                int c = reader.read();
                if (c < 0) {
                    eofReached = true;
                    return false;
                }
                didCountLine = false;
                if (c == '\n') {
                    if (hadCarriageReturn) {
                        // swallow this one: \r\n -> \n
                        hadCarriageReturn = false;
                        return next();
                    }
                    lineNumber++;
                    didCountLine = true;
                }
                hadCarriageReturn = false;
                if (c == '\r') {
                    c = '\n'; // all newlines as \n
                    lineNumber++;
                    didCountLine = true;
                    hadCarriageReturn = true;
                }
                currentChar = (char)c;
            } else {
                currentChar = this.pushbackChar;
                if (pushbackDidCountLine) {
                    lineNumber++;
                }
                pushbackChar = null;
            }
            return true;
        }

        private char current() {
            if (currentChar == null) {
                throw new IllegalStateException("No current character");
            }
            return currentChar;
        }

        private int currentLine() {
            return lineNumber;
        }

        private boolean eof() {
            return eofReached && pushbackChar == null;
        }

        private void pushback() {
            if (pushbackChar != null) {
                throw new IllegalStateException("Duplicate pushback");
            }
            pushbackDidCountLine = didCountLine;
            if (pushbackDidCountLine) {
                lineNumber--;
            }
            pushbackChar = currentChar;
            currentChar = null;
        }

    }

    public static final class Token {
        private final int line;
        private final String text;
        public Token(int line, String text) {
            this.line = line;
            this.text = text;
        }
        public int getLine() {
            return line;
        }
        public String getText() {
            return text;
        }
        @Override
        public String toString() {
            return "Token[" + line + ":" + text + "]";
        }
    }

    public static final class ParseException extends IOException {
        private final int line;
        private final String error;
        public ParseException(int line, String error) {
            super("(line " + line + ") " + error);
            this.line = line;
            this.error = error;
        }
        public int getLine() {
            return line;
        }
        public String getError() {
            return error;
        }
    }

}
