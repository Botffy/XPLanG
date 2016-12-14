package ppke.itk.xplang.interpreter;

import org.junit.Test;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class ValueInitialisationTest {
    private static final Location LOCATION = new Location(1, 1, 1, 1);

    @Test public void arrayValuesShouldInitialise() {
        Type type = FixArray.of(6, Scalar.BOOLEAN_TYPE);
        VariableDeclaration var = new VariableDeclaration(LOCATION, "a", type);

        Value initialValue = Value.initialise(var.getType());
        assertThat("Composite types should be initialised with ArrayValues",
            initialValue, instanceOf(ArrayValue.class)
        );
    }

    @Test public void multiDimensionalArraysShouldInitialiseRecursively() {
        Type type = FixArray.of(6, FixArray.of(2, Scalar.BOOLEAN_TYPE));
        VariableDeclaration var = new VariableDeclaration(LOCATION, "a", type);

        Value initialValue = Value.initialise(var.getType());
        ArrayValue value = (ArrayValue) initialValue;
        assertThat("Composite types should be initialised with ArrayValues",
            value.getComponent(new IntegerValue(0)), instanceOf(ArrayValue.class)
        );
    }
}
