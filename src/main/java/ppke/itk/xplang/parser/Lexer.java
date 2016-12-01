package ppke.itk.xplang.parser;

import java.io.Reader;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple tokenizer.
 *
 * Given a {@link java.io.Reader} and a set of {@link Symbol}s, it attempts to break down the received text into a token
 * stream.
 */
class Lexer {
    private final Iterable<Symbol> symbols;
    private final Scanner input;

    private String line = ""; // buffer
    private int lineno = 0; // we are at this line of the source
    private int cursor = 0; // we are at this column in the current line;

    private final Matcher matcher = Pattern.compile("dummy").matcher(line).useTransparentBounds(true).useAnchoringBounds(false);

    /**
     * Create a new Lexer
     * @param source The source text to be tokenize.
     * @param symbols An ordered set of the grammar's symbols.
     */
    Lexer(Reader source, Iterable<Symbol> symbols) {
        this.input = new Scanner(source);
        this.symbols = symbols;

        // initialize buffer
        if(input.hasNextLine()) {
            line = input.nextLine();
            matcher.reset(line);
            ++lineno;
            cursor = 0;
        }
    }

    /**
     * Progresses in the {@code source} input and hands up the next matched Token.
     *
     * The rules of matching are similar to those of the GNU flex: it tries to match every rule, and hands up the
     * longest match. If two matches are of the same length, we choose the {@link Symbol} with the higher precedence.
     * And when even the precedence is the same, we choose the symbol declared earlier (occuring earlier in the
     * {@code symbols} list).
     *
     * An important difference to the GNU flex behaviour is that the newline cannot be matched: Lexer automatically
     * catches the EOL and loads a new line into its inner buffer. That's not perfect.
     */
    public Token next() {
        Symbol match_symbol;
        String match_lexeme;
        do {
            // we've read the entire buffer?
            if(line.length() <= cursor) {
                // if no lines left, just hand up an EOF.
                if(!input.hasNextLine()) return new Token(Symbol.EOF, "", lineno, cursor);

                // otherwise read another line into the buffer and hand up an EOL
                line = input.nextLine();
                matcher.reset(line);
                int oldline = lineno;
                int oldcurs = cursor;
                ++lineno;
                cursor = 0;
                return new Token(Symbol.EOL, "", oldline, oldcurs);
            }
            match_symbol = null;
            match_lexeme = "";
            matcher.region(cursor, line.length());
            for(final Symbol s : symbols) {
                // if there is a match,
                //   or there's no earlier match
                //   or there is, but the new match is properly longer than any previous matches,
                //   or they're of the same length but this has a higher precedence
                // then use that.
                if(matcher.usePattern(s.getPattern()).lookingAt() && (
                    match_symbol == null ||
                    matcher.group().length() > match_lexeme.length() ||
                    (matcher.group().length() == match_lexeme.length() && s.getPrecedence() > match_symbol.getPrecedence())
                ) ) {
                    match_symbol = s;
                    match_lexeme = matcher.group();
                }
            }

            // any results?
            if(match_symbol != null) {
                cursor += match_lexeme.length();
            } else {
                int bakCursor = cursor;
                cursor = line.length();	// skip to EOL and recover;
                return new Token( Symbol.LEXER_ERROR, line.substring(bakCursor), lineno, bakCursor );
            }
        } while(!match_symbol.isSignificant()); // if the match isn't significant, continue.

        return new Token(match_symbol, match_lexeme, lineno, cursor-match_lexeme.length());
    }
}
