package ppke.itk.xplang.interpreter;

import org.junit.Test;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class ValueInitialisationTest {
    @Test public void ArrayValuesShouldInitialiseToSize() {
        Type type = FixArray.of(6, Scalar.BOOLEAN_TYPE);
        VariableDeclaration var = new VariableDeclaration("a", type);

        Value initialValue = Value.initialise(var);
        assertThat("Composite types should be initialised with ArrayValues",
            initialValue, instanceOf(ArrayValue.class)
        );
    }
}
