package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.Location;

/**
 * An error encountered in compilation time.
 */
abstract public class ParseError extends Exception {
    /** The location of the error */
    private final Location location;

    ParseError(String message, int line, int column) {
        super(message);
        this.location = new Location(line, column);
    }

    ParseError(String message, Location location) {
        super(message);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public CompilerMessage toErrorMessage() {
        return CompilerMessage.error(this.getMessage(), this.getLocation());
    }
}
