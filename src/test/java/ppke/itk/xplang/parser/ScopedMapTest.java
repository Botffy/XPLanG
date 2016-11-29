package ppke.itk.xplang.parser;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public class ScopedMapTest {
    @Test
    public void closingScopeBeforeOpening() {
        ScopedMap<String, Object> table = new ScopedMap<>();
        Throwable error = exceptionThrownBy(() -> table.closeScope());
        assertEquals(
            "Closing a scope before opening one should raise a NoScopeToClose error",
            ScopedMap.NoScopeError.class, error.getClass()
        );
    }

    @Test
    public void addGet() {
        ScopedMap<String, Object> table = new ScopedMap<>();
        table.openScope();

        assertNull("Looking up an unregistered name should return null",
            table.lookup("Latin")
        );

        Object obj = "Nomen est omen";
        try {
            table.add("Latin", obj);
        } catch(ScopedMap.NameNotFreeError e) {
            fail("Adding a name to an empty table should not cause an error");
        }
        assertSame("If I add something, I should be able to look it up",
            obj, table.lookup("Latin")
        );

        assertFalse("If I added something, that name should not be free in this scope", table.isFree("Latin"));

        Object obj2 = "Nominative determinism";
        try {
            table.add("English", obj2);
        } catch(ScopedMap.NameNotFreeError e) {
            fail("Adding a different name to the table should not be a problem");
        }
        assertSame("Registering another object with another name should not be a problem",
            obj2, table.lookup("English")
        );

        Object obj3 = "Dulce et decorum est ro patria mori";
        assertEquals("Adding another object for an existing name without creating a new scope should raise an error",
            ScopedMap.NameNotFreeError.class, exceptionThrownBy(()->table.add("Latin", obj3)).getClass()
        );

        table.openScope();
        assertTrue("The names taken earlier should be considered free after opening a new scope", table.isFree("Latin"));

        try {
            table.add("Latin", obj3);
        } catch(ScopedMap.NameNotFreeError e) {
            fail("Adding another object for the same name should be OK after opening a new scope");
        }
        assertSame("Adding another object for the same name should be OK after opening a new scope, and it should hide the previously added object",
            obj3, table.lookup("Latin")
        );

        table.closeScope();
        assertSame("After closing the last scope, I should get back the previously hidden object",
            obj, table.lookup("Latin")
        );

        table.openScope();
        assertSame("And if I open a new scope, I should still get the object that was hidden in another branch",
            obj, table.lookup("Latin")
        );
    }

    @Test public void values() {
        ScopedMap<String, Object> table = new ScopedMap<>();

        Object obj1 = "Nomen est omen";
        Object obj2 = "Nominative determinism";
        Object obj3 = "Meine liebe wasserkopf";
        Object obj4 = "Dolce et decorum est pro patria mori";

        table.openScope();
        table.add("Latin", obj1);
        table.add("English", obj2);

        assertEquals("Entries() should list added elements",
            table.entries(), Stream.of(Pair.of("Latin", obj1), Pair.of("English", obj2)).collect(toSet())
        );

        table.openScope();
        table.add("German", obj3);
        assertEquals("Entries() should list elements in all visible scopes",
            table.entries(), Stream.of(Pair.of("Latin", obj1), Pair.of("English", obj2), Pair.of("German", obj3)).collect(toSet())
        );

        table.add("Latin", obj4);
        assertEquals("Entries() should respect shadowing",
            table.entries(), Stream.of(Pair.of("Latin", obj4), Pair.of("English", obj2), Pair.of("German", obj3)).collect(toSet())
        );

        table.closeScope();
        assertEquals("Entries() should not list items in closed scopes",
            table.entries(), Stream.of(Pair.of("Latin", obj1), Pair.of("English", obj2)).collect(toSet())
        );
    }

    @Test public void currentScopeValues() {
        ScopedMap<String, Object> table = new ScopedMap<>();
        Object obj1 = "Nomen est omen";
        Object obj2 = "Nominative determinism";
        Object obj3 = "Meine liebe wasserkopf";
        Object obj4 = "Dolce et decorum est pro patria mori";

        table.openScope();
        table.add("Latin", obj1);
        table.add("English", obj2);

        assertEquals("EntriesInCurrentScope() should list added elements",
            table.entries(), Stream.of(Pair.of("Latin", obj1), Pair.of("English", obj2)).collect(Collectors.toSet())
        );

        table.openScope();
        assertEquals("EntriesInCurrentScope() should be empty after opening a new scope",
            table.entriesInCurrentScope(), emptySet()
        );

        table.add("Latin", obj4);
        assertEquals("EntriesInCurrentScope() should contain the one element put in it in the current scope.",
            table.entriesInCurrentScope(), Stream.of(Pair.of("Latin", obj4)).collect(Collectors.toSet())
        );
    }
}
