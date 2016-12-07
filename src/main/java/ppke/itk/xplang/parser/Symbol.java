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
        public final static int IDENTIFIER = 0;
        public final static int LITERAL = 5;
        public final static int KEYWORD = 10;
        public final static int DEFAULT = 10;

        private Precedence() {
            // private ctor to hide default one.
        }
    }

    /** Symbol denoting a lexer error. */
    public static final Symbol LEXER_ERROR = new Symbol("LEXER_ERROR", null);

    /** Symbol denoting the end of the input stream. */
    public static final Symbol EOF = new Symbol("EOF", null);

    /** Cross-platform newline pattern. */
    public static final String EOLPattern = "\\r\\n?|\\n";

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

    public static Builder create() {
        return new Builder();
    }

    /**
     * A comfy builder.
     */
    public static class Builder {
        private boolean caseInsensitive = false;
        private boolean isLiteralPattern = false;
        private String name = null;
        private String pattern = null;
        private int precedence = Precedence.DEFAULT;
        private boolean significant = true;

        private Builder() {
            // private ctor to hide the default one.
            // Use Symbol.create()
        }

        public Builder named(String name) {
            this.name = name;
            return this;
        }

        public Builder matching(String pattern) {
            this.pattern = pattern;
            this.isLiteralPattern = false;
            return this;
        }

        public Builder matchingLiteral(String pattern) {
            this.pattern = pattern;
            this.isLiteralPattern = true;
            return this;
        }

        public Builder withPrecedence(int precedence) {
            this.precedence = precedence;
            return this;
        }

        public Builder notSignificant() {
            this.significant = false;
            return this;
        }

        public Builder caseInsensitive() {
            this.caseInsensitive = true;
            return this;
        }

        public Builder caseSensitive() {
            this.caseInsensitive = false;
            return this;
        }

        public Symbol register(Context context) {
            int regexFlags = Pattern.UNICODE_CASE | Pattern.DOTALL;
            if(caseInsensitive) {
                regexFlags |= Pattern.CASE_INSENSITIVE;
            }
            if(isLiteralPattern) {
                regexFlags |= Pattern.LITERAL;
            }
            Symbol symbol = new Symbol(
                this.name,
                Pattern.compile(this.pattern, regexFlags),
                this.precedence,
                this.significant
            );
            context.register(symbol);
            return symbol;
        }
    }
}
