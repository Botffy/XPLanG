package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.Location;

/**
 * The Token is handed up to the Parser by the Lexer. It's a simple record holding information about both the
 * matched {@link Symbol}and its realization in the source text.
 */
public class Token {
    private final Symbol symbol;
    private final String lexeme;
    private final Location location;

    Token(Symbol symbol, String lexeme, Location location) {
        this.symbol = symbol;
        this.lexeme = lexeme;
        this.location = location;
    }

    /** The terminal symbol this token is an instance of. */
    public Symbol symbol() {
        return symbol;
    }

    /** The realisation of the symbol, the way it occurs in the source. */
    public String lexeme() {
        return lexeme;
    }

    public Location location() {
        return location;
    }

    /** The lexeme starts in this line of the source text. */
    @Deprecated
    public int getLine() {
        return location.start.line;
    }

    /** The lexeme starts at this column of the line. */
    @Deprecated
    public int getCol() {
        return location.start.column;
    }

    @Override public String toString() {
        return String.format("%d:%d: %s %s", location.start.line, location.start.column, symbol.getName(), lexeme);
    }
}
