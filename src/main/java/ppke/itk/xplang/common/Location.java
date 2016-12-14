package ppke.itk.xplang.common;

import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * A range of text.
 */
public final class Location {
    private static final Location NONE = new Location(-1, -1, -1, -1);
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

    @Override public String toString() {
        return String.format("[%s - %s]", start, end);
    }

    public static Location between(Location start, Location end) {
        return new Location(start.start, end.end);
    }

    public static Location definedBy(List<? extends Locatable> variables) {
        if(variables.isEmpty()) return Location.NONE;

        return between(
            variables.stream()
                .map(Locatable::location)
                .min(comparing(x -> x.start))
                .orElse(null),
            variables.stream()
                .map(Locatable::location)
                .max(comparing(x -> x.start))
                .orElse(null)
        );
    }

    public static Location definedBy(Locatable... locatable) {
        return definedBy(Arrays.asList(locatable));
    }
}
