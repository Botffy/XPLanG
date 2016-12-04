package ppke.itk.xplang.common;

import java.util.Objects;

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

    @Override public boolean equals(Object object) {
        if(!(object instanceof Location)) return false;
        Location that = (Location) object;
        return this.line == that.line && this.column == that.column;
    }

    @Override public int hashCode() {
        return Objects.hash(line, column);
    }
}
