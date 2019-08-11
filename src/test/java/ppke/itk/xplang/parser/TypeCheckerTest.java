package ppke.itk.xplang.parser;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

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

    @Test
    public void topLevelCoercion() throws Exception {
        Signature coercion = new Signature(SpecialName.IMPLICIT_COERCION, Archetype.REAL_TYPE, Archetype.INTEGER_TYPE);
        context.registerFunction(new MockFunctionDeclaration(coercion));

        Signature f = new Signature(name("f"), Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
        Map<Signature, FunctionDeclaration> candidates = new HashMap<>();
        candidates.put(f, new MockFunctionDeclaration(f));

        ValueExpression op1 = new ValueExpression(new IntegerLiteral(Location.NONE, 1));
        ValueExpression op2 = new ValueExpression(new IntegerLiteral(Location.NONE, 1));
        FunctionExpression fCall = new FunctionExpression(name("plus"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).expecting(Archetype.REAL_TYPE).build();
        RValue call = typeChecker.resolve();
        assertThat(call, instanceOf(FunctionCall.class));
        FunctionCall funCall = (FunctionCall) call;
        assertEquals(coercion, funCall.getDeclaration().signature());
        (new ASTPrinter()).visit(call);
    }

    @Test
    public void argumentCoercion() throws Exception {
        Signature coercion = new Signature(SpecialName.IMPLICIT_COERCION, Archetype.INTEGER_TYPE, Archetype.REAL_TYPE);
        context.registerFunction(new MockFunctionDeclaration(coercion));

        Signature f = new Signature(name("f"), Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
        Map<Signature, FunctionDeclaration> candidates = new HashMap<>();
        candidates.put(f, new MockFunctionDeclaration(f));

        ValueExpression op1 = new ValueExpression(new RealLiteral(Location.NONE, 1.5));
        ValueExpression op2 = new ValueExpression(new IntegerLiteral(Location.NONE, 1));
        FunctionExpression fCall = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).expecting(Archetype.INTEGER_TYPE).build();
        RValue call = typeChecker.resolve();
        (new ASTPrinter()).visit(call);

        assertThat(call, instanceOf(FunctionCall.class));
        FunctionCall funCall = (FunctionCall) call;
        assertEquals(f, funCall.getDeclaration().signature());

        RValue left = funCall.arguments().get(0);
        assertThat(left, instanceOf(FunctionCall.class));
        FunctionCall coercionCall = (FunctionCall) left;
        assertEquals(coercion, coercionCall.getDeclaration().signature());
    }

    /**
     * We have an overloaded functions:
     *
     *      f (a, a) -> a
     *      f (a, b) -> a
     *
     * and an implicit conversion b -> a.
     *
     * In a call f (x, y) for x y: a, both overloads are viable candidates due to coercion, but f1 is always
     * better and never worse than f2. This should not be an ambiguous call in
     */
    @Test
    public void resolveCoercionAmbiguity() throws Exception {
        Type a = new Scalar("A");
        Type b = new Scalar("B");

        Signature coercion = new Signature(SpecialName.IMPLICIT_COERCION, b, a);
        context.registerFunction(new MockFunctionDeclaration(coercion));

        Signature f1 = new Signature(name("f"), a, a, a);
        Signature f2 = new Signature(name("f"), a, a, b);
        Map<Signature, FunctionDeclaration> candidates = new HashMap<>();
        candidates.put(f1, new MockFunctionDeclaration(f1));
        candidates.put(f2, new MockFunctionDeclaration(f2));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        FunctionExpression fCall = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).expecting(a).build();
        RValue call = typeChecker.resolve();
        (new ASTPrinter()).visit(call);

        assertThat(call, instanceOf(FunctionCall.class));
        FunctionCall funCall = (FunctionCall) call;
        assertEquals(f1, funCall.getDeclaration().signature());
    }

    /**
     * We have an overloaded function f:
     *
     *      f (a, b) -> a
     *      f (b, a) -> a
     *
     * And an implicit conversion b -> a
     *
     * In a call f(x,y) for x, y? a, both overloads are viable candidates, but the call is ambiguous, because both
     * candidates are equally good, one with a (MATCHED,COERCED), the other with a (COERCED,MATCHED) resolution.
     */
    @Test
    public void ambiguityForCoercion() throws Exception {
        Type a = new Scalar("A");
        Type b = new Scalar("B");

        Signature coercion = new Signature(SpecialName.IMPLICIT_COERCION, b, a);
        context.registerFunction(new MockFunctionDeclaration(coercion));

        Signature f1 = new Signature(name("f"), a, a, b);
        Signature f2 = new Signature(name("f"), a, b, a);
        Map<Signature, FunctionDeclaration> candidates = new HashMap<>();
        candidates.put(f1, new MockFunctionDeclaration(f1));
        candidates.put(f2, new MockFunctionDeclaration(f2));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        FunctionExpression fCall = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));
        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).expecting(a).build();

        Throwable error = exceptionThrownBy(() -> typeChecker.resolve());
        error.printStackTrace();
        assertThat(error, instanceOf(FunctionAmbiguousException.class));

    }

    private static class MockFunctionDeclaration extends FunctionDeclaration {
        public MockFunctionDeclaration(Signature signature) {
            super(Location.NONE, signature);
        }

        @Override
        public void accept(ASTVisitor visitor) { }
    }

    private static class MockRValue extends RValue {
        private final Type type;

        public MockRValue(Type type) {
            super(Location.NONE);
            this.type = type;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public void accept(ASTVisitor visitor) { }

        @Override
        public String toString() {
            return String.format("Leaf[%s]", type.toString());
        }
    }
}
