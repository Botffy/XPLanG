package ppke.itk.xplang.parser;


import java.util.regex.Pattern;

/**
 * A terminal symbol of the language.
 */
public class OldSymbol {
    /** Symbol denoting a lexer error. */
    static final OldSymbol LEXER_ERROR = new OldSymbol("LEXER_ERROR", null);

    /** Symbol denoting the end of the input stream. */
    public static final OldSymbol EOF = new OldSymbol("EOF", null);

    private final String name;
    private final Pattern pattern;
    private final boolean significant;
    private final int precedence;

    /**
     * Create a OldSymbol.
     * @param name The name of the OldSymbol. Used internally and for error reporting.
     * @param pattern The regular expression describing the symbol.
     * @param precedence The precedence level of the symbol.
     * @param significant Should the lexer hand this symbol up to the parser, or should it just swallow it?
     */
    public OldSymbol(String name, Pattern pattern, int precedence, boolean significant) {
        this.name = name;
        this.pattern = pattern;
        this.significant = significant;
        this.precedence = precedence;
    }

    /**
     * Create a significant OldSymbol with default precedence level.
     */
    public OldSymbol(String name, Pattern pattern) {
        this(name, pattern, Precedence.DEFAULT, true);
    }

    /**
     * Create a significant OldSymbol.
     */
    public OldSymbol(String name, Pattern pattern, int precedence) {
        this(name, pattern, precedence, true);
    }

    public String getName() {
        return name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getPatternAsString() {
        return pattern.toString();
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

    /**
     * Create a new OldSymbol using a {@link OldSymbol.Builder}.
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * Predefined precedence levels for OldSymbols.  Note that the actual precedence is really just an int, these are
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

    /**
     * A comfy builder.
     */
    public static final class Builder {
        private boolean caseInsensitive = false;
        private boolean isLiteralPattern = false;
        private String name = null;
        private String pattern = null;
        private int precedence = Precedence.DEFAULT;
        private boolean significant = true;

        private Builder() {
            // private ctor to hide the default one.
            // Use OldSymbol.create()
        }

        public Builder named(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the regular expression pattern the OldSymbol is to match. Make sure to escape things.
         * @see Pattern#compile(String)
         */
        public Builder matching(String pattern) {
            this.pattern = pattern;
            this.isLiteralPattern = false;
            return this;
        }

        /**
         * The OldSymbol is described by a literal string, not a regular expression.
         * @see Pattern#LITERAL
         */
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

        /**
         * Finalise the built symbol.
         */
        public OldSymbol build() {
            int regexFlags = Pattern.UNICODE_CASE | Pattern.DOTALL;
            if(caseInsensitive) {
                regexFlags |= Pattern.CASE_INSENSITIVE;
            }
            if(isLiteralPattern) {
                regexFlags |= Pattern.LITERAL;
            }
            return new OldSymbol(
                this.name,
                Pattern.compile(this.pattern, regexFlags),
                this.precedence,
                this.significant
            );
        }

        public OldSymbol register(Context ctx) {
            OldSymbol symbol = build();
            return symbol;
        }
    }
}
