package ppke.itk.xplang.interpreter;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.MockName;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StaticEvaluatorTest {
    private StaticEvaluator evaluator;

    @Before
    public void setUp() {
        evaluator = new StaticEvaluator();
    }

    @Test
    public void literalsAreEasy() {
        RValue val = new IntegerLiteral(Location.NONE, Archetype.INTEGER_TYPE, 1337);
        Integer eval = evaluator.eval(val, new Scope(emptyList()), Integer.class);
        assertEquals(1337, eval.intValue());
    }

    @Test
    public void usingGlobals() {
        VariableDeclaration global = getGlobal();
        Scope scope = new Scope(singletonList(global));
        RValue val = new VarVal(Location.NONE, global, true);
        Integer eval = evaluator.eval(val, scope, Integer.class);
        assertEquals(256128, eval.intValue());
    }


    @Test
    public void expressions() {
        VariableDeclaration global = getGlobal();
        Scope scope = new Scope(singletonList(global));
        RValue left = new IntegerLiteral(Location.NONE, Archetype.INTEGER_TYPE, 1337);;
        RValue right = new VarVal(Location.NONE, global, true);
        RValue val = getFunction(Instruction.ISUM).call(Location.NONE, asList(left, right));
        Integer eval = evaluator.eval(val, scope, Integer.class);
        assertEquals(256128 + 1337, eval.intValue());
    }

    @Test
    public void badExpectations() {
        RValue val = new IntegerLiteral(Location.NONE, Archetype.INTEGER_TYPE, 1337);
        Throwable error = exceptionThrownBy(() -> evaluator.eval(val, new Scope(emptyList()), String.class));
        error.printStackTrace();
        assertThat(error, instanceOf(IllegalStateException.class));
    }

    private VariableDeclaration getGlobal() {
        return new VariableDeclaration(Location.NONE, "global", Archetype.INTEGER_TYPE,
            new IntegerLiteral(Location.NONE, Archetype.INTEGER_TYPE, 256128)
        );
    }

    private FunctionDeclaration getFunction(Instruction instruction) {
        Signature signature = new Signature(new MockName(instruction.name()), instruction.returnType(), instruction.operands());
        return new BuiltinFunction(Location.NONE, signature, instruction);
    }

}
