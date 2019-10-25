package ppke.itk.xplang.interpreter;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.IntegerLiteral;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Type;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ValueInitialisationTest {
    private static final Location LOCATION = new Location(1, 1, 1, 1);

    private Interpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new Interpreter(new MockStreamHandler());
    }

    @Test
    public void arrayValuesShouldInitialise() {
        Type type = FixArray.of(6, Archetype.BOOLEAN_TYPE).indexedBy(Archetype.INTEGER_TYPE);
        VariableDeclaration var = new VariableDeclaration(LOCATION, "a", type);

        interpreter.visit(var);
        Memory memory = interpreter.getMemory();

        assertThat("Composite types should be initialised with ArrayValues",
            memory.getValue(var), instanceOf(ArrayValue.class)
        );
    }

    @Test
    public void multiDimensionalArraysShouldInitialiseRecursively() {
        Type type = FixArray.of(6,
                FixArray.of(2, Archetype.BOOLEAN_TYPE).indexedBy(Archetype.INTEGER_TYPE))
            .indexedBy(Archetype.INTEGER_TYPE);
        VariableDeclaration var = new VariableDeclaration(LOCATION, "a", type);

        interpreter.visit(var);
        Memory memory = interpreter.getMemory();

        Value initialValue = memory.getValue(var);
        ArrayValue value = (ArrayValue) initialValue;
        assertThat("Composite types should be initialised with ArrayValues",
            value.getComponent(new IntegerValue(0)), instanceOf(ArrayValue.class)
        );
    }

    @Test
    public void specifiedInitialValue() {
        VariableDeclaration var = new VariableDeclaration(
            LOCATION, "a", Archetype.INTEGER_TYPE,
            new IntegerLiteral(LOCATION, Archetype.INTEGER_TYPE, 1337)
        );

        interpreter.visit(var);
        Memory memory = interpreter.getMemory();

        Value initialValue = memory.getValue(var);
        assertThat(initialValue, instanceOf(IntegerValue.class));
        assertEquals(1337, ((IntegerValue) initialValue).getValue());
    }
}
