package ppke.itk.xplang.common;

/**
 * Simple value class to hold location information about a piece of text in a file.
 */
public final class Location {
    public final int line;
    public final int column;

    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override public String toString() {
        return String.format("%s:%s", line, column);
    }
}
