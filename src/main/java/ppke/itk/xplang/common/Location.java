package ppke.itk.xplang.common;

/**
 * A range of text.
 */
public final class Location {
    public final CursorPosition start;
    public final CursorPosition end;

    public Location(int startLine, int startCol, int endLine, int endCol) {
        this(new CursorPosition(startLine, startCol), new CursorPosition(endLine, endCol));
    }

    public Location(CursorPosition start, CursorPosition end) {
        if(start.compareTo(end) > 0)
            throw new IllegalArgumentException("Starting position is later than ending position");

        this.start = start;
        this.end = end;
    }
}
