package ppke.itk.xplang.common;

import java.util.Objects;

/**
 * Cursor position in a text.
 */
public final class CursorPosition implements Comparable<CursorPosition> {
    public final int line;
    public final int column;

    public CursorPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override public String toString() {
        return String.format("%s:%s", line, column);
    }

    @Override public boolean equals(Object object) {
        if(!(object instanceof CursorPosition)) return false;
        CursorPosition that = (CursorPosition) object;
        return this.line == that.line && this.column == that.column;
    }

    @Override public int hashCode() {
        return Objects.hash(line, column);
    }

    @Override public int compareTo(CursorPosition that) {
        int result = this.line - that.line;
        return result == 0? this.column - that.column : result;
    }

    public Location toUnaryLocation() {
        return new Location(this, this);
    }
}
