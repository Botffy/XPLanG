package ppke.itk.xplang.common;

import ppke.itk.xplang.parser.ErrorCode;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * A compiler error we would like to display to the programmer.
 */
public final class CompilerMessage {
    public enum Severity {
        WARNING,
        ERROR;
    }

    public static CompilerMessage error(Location location, ErrorCode errorCode, Object... params) {
        return error(location, errorCode, asList(params));
    }

    public static CompilerMessage error(Location location, ErrorCode errorCode, List<Object> params) {
        return new CompilerMessage(Severity.ERROR, location, errorCode, params);
    }

    public static CompilerMessage warning(Location location, ErrorCode errorCode, List<Object> params) {
        return new CompilerMessage(Severity.WARNING, location, errorCode, params);
    }

    private final Severity severity;
    private final Location location;
    private final ErrorCode errorCode;
    private final List<Object> params;

    private CompilerMessage(Severity severity, Location location, ErrorCode errorCode, List<Object> params) {
        this.severity = severity;
        this.location = location;
        this.errorCode = errorCode;
        this.params = params;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Location getLocation() {
        return location;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public List<Object> getParams() {
        return params;
    }

    @Deprecated
    public String getMessage() {
        return String.format("%s%s", errorCode, params);
    }

    public CursorPosition getCursorPosition() {
        return location.start;
    }

    public CursorPosition getEndPosition() {
        return location.end;
    }

    @Override public String toString() {
        return String.format("%s %s%s", location.start, getErrorCode(), getParams());
    }
}
