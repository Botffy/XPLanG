package ppke.itk.xplang.common;

/**
 * A compiler error we would like to display to the programmer.
 */
public final class CompilerMessage {
    public enum Severity {
        WARNING,
        ERROR;
    }

    public static CompilerMessage error(String message, Location location) {
        return new CompilerMessage(Severity.ERROR, message, location);
    }

    public static CompilerMessage warning(String message, Location location) {
        return new CompilerMessage(Severity.WARNING, message, location);
    }

    private final Severity severity;
    private final String message;
    private final Location location;

    private CompilerMessage(Severity severity, String message, Location location) {
        this.severity = severity;
        this.message = message;
        this.location = location;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public Location getLocation() {
        return location;
    }

    @Override public String toString() {
        return String.format("%s %s", location, message);
    }
}