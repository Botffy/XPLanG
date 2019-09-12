package ppke.itk.xplang.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocationTest {
    @Test
    public void contains() {
        Location location = new Location(1, 1, 1, 10);
        assertTrue(location.contains(new CursorPosition(1, 1)));
        assertTrue(location.contains(new CursorPosition(1, 10)));
        assertTrue(location.contains(new CursorPosition(1, 4)));
        assertFalse(location.contains(new CursorPosition(2, 4)));
        assertFalse(location.contains(new CursorPosition(1, 11)));
    }

    @Test
    public void multiLineLocationContains() {
        Location location = new Location(1, 1, 3, 10);
        assertTrue(location.contains(new CursorPosition(2, 4)));
        assertTrue(location.contains(new CursorPosition(1, 11)));
    }
}
