package ppke.itk.xplang.parser;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;

import java.util.HashMap;
import java.util.Map;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ppke.itk.xplang.parser.MockName.name;

public class TypeCheckerTest {
    private Context context;

    @Before
    public void setUp() {
        this.context = new Context();
    }

    @Test
    public void resolvesValueExpressions() throws Exception {
        RValue value = new IntegerLiteral(Location.NONE, 10);
        Expression expression = new ValueExpression(value);

        TypeChecker typeChecker = TypeChecker.in(context).checking(expression).build();
        RValue resolved = typeChecker.resolve();
        assertEquals(value, resolved);
    }

    @Test
    public void throwsIfTypeMismatch() throws Exception {
        RValue value = new IntegerLiteral(Location.NONE, 10);
        Expression expression = new ValueExpression(value);

        TypeChecker typeChecker = TypeChecker.in(context).checking(expression).expecting(Archetype.STRING_TYPE).build();
        Throwable error = exceptionThrownBy(() -> typeChecker.resolve());
        assertThat(error, instanceOf(TypeError.class));
    }

    @Test
    public void resolvesFunctions() throws Exception {
        Signature plusInt = new Signature(name("plus"), Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
        Signature plusString = new Signature(name("plus"), Archetype.STRING_TYPE, Archetype.STRING_TYPE, Archetype.STRING_TYPE);
        Map<Signature, FunctionDeclaration> candidates = new HashMap<>();
        candidates.put(plusInt, new MockFunctionDeclaration(plusInt));
        candidates.put(plusString, new MockFunctionDeclaration(plusString));

        ValueExpression op1 = new ValueExpression(new IntegerLiteral(Location.NONE, 1));
        ValueExpression op2 = new ValueExpression(new IntegerLiteral(Location.NONE, 2));
        FunctionExpression expression = new FunctionExpression(name("plus"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(expression).build();
        RValue call = typeChecker.resolve();
        assertThat(call, instanceOf(FunctionCall.class));
        assertEquals(plusInt, ((FunctionCall) call).getDeclaration().signature());
    }

    @Test
    public void resolvesTrees() throws Exception {
        Signature plusInt = new Signature(name("plus"), Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
        Signature plusString = new Signature(name("plus"), Archetype.STRING_TYPE, Archetype.STRING_TYPE, Archetype.STRING_TYPE);
        Map<Signature, FunctionDeclaration> candidates = new HashMap<>();
        candidates.put(plusInt, new MockFunctionDeclaration(plusInt));
        candidates.put(plusString, new MockFunctionDeclaration(plusString));

        ValueExpression op1 = new ValueExpression(new IntegerLiteral(Location.NONE, 1));
        ValueExpression op2 = new ValueExpression(new IntegerLiteral(Location.NONE, 2));
        ValueExpression op3 = new ValueExpression(new IntegerLiteral(Location.NONE, 2));
        FunctionExpression plus = new FunctionExpression(name("plus"), Location.NONE, new HashMap<>(candidates), asList(op1, op2));
        FunctionExpression root = new FunctionExpression(name("plus"), Location.NONE, new HashMap<>(candidates), asList(plus, op3));

        TypeChecker typeChecker = TypeChecker.in(context).checking(root).build();
        RValue call = typeChecker.resolve();
        assertThat(call, instanceOf(FunctionCall.class));
        FunctionCall funCall = (FunctionCall) call;
        assertEquals(plusInt, (funCall.getDeclaration().signature()));
        assertThat(funCall.arguments().get(0), instanceOf(FunctionCall.class));

        (new ASTPrinter()).visit(call);
    }

    @Test
    public void unresolvedFunctions() throws Exception {
        Signature plusInt = new Signature(name("plus"), Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
        Signature plusString = new Signature(name("plus"), Archetype.STRING_TYPE, Archetype.STRING_TYPE, Archetype.STRING_TYPE);
        Map<Signature, FunctionDeclaration> candidates = new HashMap<>();
        candidates.put(plusInt, new MockFunctionDeclaration(plusInt));
        candidates.put(plusString, new MockFunctionDeclaration(plusString));

        ValueExpression op1 = new ValueExpression(new IntegerLiteral(Location.NONE, 1));
        ValueExpression op2 = new ValueExpression(new StringLiteral(Location.NONE, "hi"));
        FunctionExpression plus = new FunctionExpression(name("plus"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(plus).build();

        Throwable error = exceptionThrownBy(() -> typeChecker.resolve());
        error.printStackTrace();
        assertThat(error, instanceOf(NoViableFunctionException.class));
    }

    private static class MockFunctionDeclaration extends FunctionDeclaration {
        public MockFunctionDeclaration(Signature signature) {
            super(Location.NONE, signature);
        }

        @Override
        public void accept(ASTVisitor visitor) { }
    }
}
