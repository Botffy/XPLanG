package ppke.itk.xplang.parser;


import java.util.regex.Pattern;

/**
 * A regular expression describing a terminal symbol of the language.
 */
public class Symbol {
    /**
     * Predefined precedence levels for Symbols.  Note that the actual precedence is really just an int, these are
     * defined only for convenience.
     */
    public static final class Precedence {
        public final static int IDENTIFIER = 50;
        public final static int LITERAL = 5;
        public final static int KEYWORD = 10;
        public final static int DEFAULT = 10;

        private Precedence() {
            // private ctor to hide default one.
        }
    }

    /** Symbol denoting a lexer error */
    public static final Symbol LEXER_ERROR = new Symbol("LEXER_ERROR", null);

    /** Symbol denoting the end of the input stream. */
    public static final Symbol EOF = new Symbol("EOF", null);

    /** Symbol denoting end of line. */
    public static final Symbol EOL = new Symbol("EOL", null);

    private final String name;
    private final Pattern pattern;
    private final boolean significant;
    private final int precedence;

    /**
     * Create a Symbol.
     * @param name The name of the Symbol. Used internally and for error reporting.
     * @param pattern The regular expression describing the symbol.
     * @param precedence The precedence level of the symbol.
     * @param significant Should the lexer hand this symbol up to the parser, or should it just swallow it?
     */
    public Symbol(String name, Pattern pattern, int precedence, boolean significant) {
        this.name = name;
        this.pattern = pattern;
        this.significant = significant;
        this.precedence = precedence;
    }

    /**
     * Create a significant Symbol with default precedence level.
     */
    public Symbol(String name, Pattern pattern) {
        this(name, pattern, Precedence.DEFAULT, true);
    }

    /**
     * Create a significant Symbol.
     */
    public Symbol(String name, Pattern pattern, int precedence) {
        this(name, pattern, precedence, true);
    }

    public String getName() {
        return name;
    }

    Pattern getPattern() {
        return pattern;
    }

    boolean isSignificant() {
        return significant;
    }

    int getPrecedence() {
        return precedence;
    }

    @Override public String toString() {
        return String.format("%s[%s]", name, pattern);
    }
}
