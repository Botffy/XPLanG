package ppke.itk.xplang.parser;

import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * An error encountered at compilation time.
 */
public class ParseError extends Exception {
    private final Location location;
    private final ErrorCode errorCode;
    private final List<Object> params;

    public ParseError(Location location, ErrorCode errorCode, List<Object> params) {
        super(format("%s%s", errorCode, params));
        this.location = location;
        this.errorCode = errorCode;
        this.params = params;
    }

    public ParseError(Location location, ErrorCode errorCode, Object... params) {
        this(location, errorCode, asList(params));
    }

    /** The location of the error */
    public Location getLocation() {
        return location;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public List<Object> getParams() {
        return Collections.unmodifiableList(params);
    }

    public CompilerMessage toErrorMessage() {
        return CompilerMessage.error(this.getLocation(), this.getErrorCode(), this.getParams());
    }
}
