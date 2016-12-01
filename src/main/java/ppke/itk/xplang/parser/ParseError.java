package ppke.itk.xplang.parser;

/**
 * Ancestor of all errors that may be thrown during parsing.
 */
abstract public class ParseError extends Exception {
    public final int line;
    public final int column;

    ParseError(String message, int line, int column) {
        super(String.format("%2$d:%3$d: %1$s", message, line, column));
        this.line = line;
        this.column = column;
    }
}
