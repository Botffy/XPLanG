package ppke.itk.xplang.interpreter;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.assertEquals;
import static ppke.itk.xplang.interpreter.ValueUtils.nullValue;

public class MemoryTest {
    private Memory memory;

    @Before
    public void setUp() {
        this.memory = new Memory();
    }

    @Test
    public void allocateNull() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "label", Archetype.STRING_TYPE);

        memory.allocate(var, "label", nullValue());
        Value value = memory.getValue(var);
        assertEquals("Memory should be initialised with a NULL value", nullValue(), value);
    }

    @Test
    public void putTwice() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "label", Archetype.STRING_TYPE);

        memory.allocate(var, "label1", nullValue());
        Throwable exception = exceptionThrownBy(() -> memory.allocate(var, "label2", nullValue()));
        assertEquals("Allocating to the same variable twice should throw an IllegalStateException",
            IllegalStateException.class, exception.getClass()
        );
    }

    @Test
    public void putUnallocated() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "label", Archetype.INTEGER_TYPE);
        MemoryReference ref = new MemoryReference(memory, var);
        Throwable exception = exceptionThrownBy(() -> ref.assign(new IntegerValue(5)));
        assertEquals("Trying to set to an unallocated address should throw an IllegalStateException",
            IllegalStateException.class, exception.getClass()
        );
    }

    @Test
    public void putGet() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "label", Archetype.INTEGER_TYPE);
        Value put = new IntegerValue(5);
        memory.allocate(var, "label", nullValue());
        memory.getReference(var).assign(put);
        Value got = memory.getValue(var);
        assertEquals(put, got);
    }

    @Test
    public void closeGlobalScope() {
        Throwable exception = exceptionThrownBy(() -> memory.closeFrame());
        assertEquals("Trying to close the global scope should be an illegal state",
            IllegalStateException.class, exception.getClass()
        );
    }

    @Test
    public void variableInTwoScopes() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "var", Archetype.INTEGER_TYPE);
        memory.addFrame();
        memory.allocate(var, "var", nullValue());

        memory.addFrame();
        memory.allocate(var, "var", new IntegerValue(1337));

        assertEquals(new IntegerValue(1337), memory.getValue(var));
    }

    @Test
    public void closingScope() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "var", Archetype.INTEGER_TYPE);
        memory.addFrame();
        memory.allocate(var, "var", new IntegerValue(256));

        memory.addFrame();
        memory.allocate(var, "var", new IntegerValue(1337));

        memory.closeFrame();

        assertEquals(new IntegerValue(256), memory.getValue(var));
    }


    @Test
    public void closingScopeAfterAssignment() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "var", Archetype.INTEGER_TYPE);
        memory.addFrame();
        memory.allocate(var, "var", new IntegerValue(256));

        memory.addFrame();
        memory.allocate(var, "var", new IntegerValue(1337));
        memory.setValue(var, new IntegerValue(8776));

        memory.closeFrame();

        assertEquals(new IntegerValue(256), memory.getValue(var));
    }

    @Test
    public void fallbackToGlobalScope() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "var", Archetype.INTEGER_TYPE);
        memory.allocate(var, "var", new IntegerValue(256));

        memory.addFrame();
        assertEquals(new IntegerValue(256), memory.getValue(var));
    }


    @Test
    public void doNotLookInIntermediateFrames() {
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "var", Archetype.INTEGER_TYPE);
        memory.addFrame();
        memory.allocate(var, "var", new IntegerValue(256));
        memory.addFrame();
        Throwable exception = exceptionThrownBy(() -> memory.getValue(var));
        assertEquals("Trying to close the global scope should be an illegal state",
            IllegalStateException.class, exception.getClass()
        );
    }
}
