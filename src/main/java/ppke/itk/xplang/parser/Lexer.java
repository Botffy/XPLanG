package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.CursorPosition;
import ppke.itk.xplang.common.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple tokenizer.
 *
 * Given a {@link java.io.Reader} and a set of {@link Symbol}s, it attempts to break down the received text into a token
 * stream.
 */
class Lexer {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Lexer");

    /** The lexer will load this many characters from the Reader in one go. */
    private final static int CHARBUF_SIZE = 1024;
    /**
     * The lexer will try to keep at least this many characters in the buffer. This is effectively the maximum token
     * length.
     */
    private final static int MIN_BUFFER_SIZE = 128;

    private final static Pattern EOL = Pattern.compile(String.format("(%s)+", Symbol.EOL_PATTERN));
    private final static Pattern SINGLE_EOL = Pattern.compile(Symbol.EOL_PATTERN);

    private final Iterable<Symbol> symbols;
    private final Reader input;

    private CharSequence buffer = "";
    private CursorPosition prevPosition = new CursorPosition(1, 1);
    private int lineno = 1; // we are at this line of the source
    private int column = 1; // we are at this column in the current line;
    private int cursor = 0; // our location in the buffer.

    private final Matcher matcher = Pattern.compile("dummy").matcher(buffer)
        .useTransparentBounds(true)
        .useAnchoringBounds(false);

    /**
     * Create a new Lexer.
     * @param source The source text to be tokenized.
     * @param symbols An ordered set of the grammar's symbols.
     */
    Lexer(Reader source, Iterable<Symbol> symbols) {
        this.input = new BufferedReader(source);
        this.symbols = symbols;

        // initialize buffer
        try {
            readBuf();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void readBuf() throws IOException {
        char[] charBuf = new char[CHARBUF_SIZE];
        int read = input.read(charBuf, 0, CHARBUF_SIZE);
        if(read == -1) {
            return;
        }
        buffer = new StringBuilder(buffer.subSequence(cursor, buffer.length())).append(charBuf, 0, read);
        matcher.reset(buffer);
        cursor = 0;
    }

    /**
     * Progresses in the {@code source} input and hands up the next matched Token.
     *
     * The rules of matching are similar to those of the GNU flex: it tries to match every rule, and hands up the
     * longest match. If two matches are of the same length, we choose the {@link Symbol} with the higher precedence.
     * And when even the precedence is the same, we choose the symbol declared earlier (occurring earlier in the
     * {@code symbols} list).
     */
    Token next() {
        Symbol matchSymbol;
        String matchLexeme;
        do {
            // buffer maintenance
            // if we have too few characters left in the buffer, try loading it up with some more.
            if(buffer.length() - cursor < MIN_BUFFER_SIZE) {
                try {
                    readBuf();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            if(cursor >= buffer.length()) {
                return new Token(Symbol.EOF, "", new CursorPosition(lineno, column).toUnaryLocation());
            }

            matchSymbol = null;
            matchLexeme = "";
            matcher.region(cursor, buffer.length());
            for(final Symbol s : symbols) {
                // if there is a match,
                //   or there's no earlier match
                //   or there is, but the new match is properly longer than any previous matches,
                //   or they're of the same length but this has a higher precedence
                // then use that.
                if(matcher.usePattern(s.getPattern()).lookingAt()
                    && (matchSymbol == null
                        || matcher.group().length() > matchLexeme.length()
                        || (matcher.group().length() == matchLexeme.length()
                                && s.getPrecedence() > matchSymbol.getPrecedence()
                        )
                    )
                 ){
                    matchSymbol = s;
                    matchLexeme = matcher.group();
                }
            }

            // any results?
            if(matchSymbol != null) {
                advanceCursor(matchLexeme.length());
            } else {
                int bakCursor = cursor;
                skipToNextLine();
                return new Token(
                    Symbol.LEXER_ERROR,
                    buffer.subSequence(bakCursor, cursor).toString().trim(),
                    new Location(prevPosition, new CursorPosition(lineno, column))
                );
            }
        } while(!matchSymbol.isSignificant()); // if the match isn't significant, continue.

        return new Token(matchSymbol, matchLexeme, new Location(prevPosition, new CursorPosition(lineno, column)));
    }

    /**
     * Consume all characters up to and including the next sequence of newline characters.
     *
     * Used to recover from errors.
     */
    void skipToNextLine() {
        savePosition();
        Matcher matcher = EOL.matcher(buffer.subSequence(cursor, buffer.length()));
        if(matcher.find()) {
            advanceCursor(matcher.end());
        } else {
            advanceCursor(buffer.length() - cursor);
        }
    }

    /**
     * Advances the cursor by given {@code steps} characters, and advances the line counter if necessary. Use this
     * instead of the + operator.
     * @param steps The cursor should advance this many characters.
     */
    private void advanceCursor(int steps) {
        savePosition();
        int bakCursor = cursor;
        cursor += steps;
        column += steps;

        Matcher matcher = SINGLE_EOL.matcher(buffer.subSequence(bakCursor, cursor));
        while(matcher.find()) {
            ++lineno;
            column = steps - matcher.end() + 1;
        }
    }

    private void savePosition() {
        prevPosition = new CursorPosition(lineno, column);
    }
}
