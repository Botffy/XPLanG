package ppke.itk.xplang.parser;


import ppke.itk.xplang.common.Location;

/**
 * The Token is handed up to the Parser by the {@link Lexer}. It's a simple record holding information about both the
 * matched {@link Symbol}and its realization in the source text.
 */
public class Token {
    private final Symbol symbol;
    private final String lexeme;
    private final Location location;

    Token(Symbol symbol, String lexeme, int line, int col) {
        this.symbol = symbol;
        this.lexeme = lexeme;
        this.location = new Location(line, col);
    }

    /** The terminal symbol this token is an instance of. */
    public Symbol symbol() {
        return symbol;
    }

    /** The realisation of the symbol, the way it occurs in the source */
    public String lexeme() {
        return lexeme;
    }

    public Location location() {
        return location;
    }

    /** The lexeme starts in this line of the source text. */
    public int getLine() {
        return location.line;
    }

    /** The lexeme starts at this column of the line */
    public int getCol() {
        return location.column;
    }

    @Override public String toString() {
        return String.format("%d:%d: %s %s", location.line, location.column, symbol.getName(), lexeme);
    }
}
