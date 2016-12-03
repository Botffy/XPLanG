package ppke.itk.xplang.parser;

/**
 * An error encountered in compilation time.
 */
abstract public class ParseError extends Exception {
    /** The line on which we encountered this error */
    public final int line;

    /** The column in which we encountered this error */
    public final int column;

    ParseError(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }
}
