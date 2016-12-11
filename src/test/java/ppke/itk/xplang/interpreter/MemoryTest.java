package ppke.itk.xplang.interpreter;

import org.junit.Before;
import org.junit.Test;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.assertEquals;

public class MemoryTest {
    private Memory memory;

    @Before
    public void setUp() {
        this.memory = new Memory();
    }

    @Test
    public void allocateNull() {
        memory.allocate("key", "label", Value.nullValue());
        Value value = memory.getComponent("key");
        assertEquals("Memory should be initialised with a NULL value", Value.nullValue(), value);
    }

    @Test
    public void putTwice() {
        memory.allocate("key", "label1", Value.nullValue());
        Throwable exception = exceptionThrownBy(() -> memory.allocate("key", "label2", Value.nullValue()));
        assertEquals("Allocating to the same key twice should throw an InterpreterError",
            InterpreterError.class, exception.getClass()
        );
    }

    @Test
    public void putUnallocated() {
        Throwable exception = exceptionThrownBy(() -> memory.setComponent("unallocated", new IntegerValue(5)));
        assertEquals("Trying to set to an unallocated address should throw an InterpreterError",
            InterpreterError.class, exception.getClass()
        );
    }

    @Test
    public void putGet() {
        Value put = new IntegerValue(5);
        memory.allocate("key", "label", Value.nullValue());
        memory.setComponent("key", put);
        Value got = memory.getComponent("key");
        assertEquals(put, got);
    }
}
