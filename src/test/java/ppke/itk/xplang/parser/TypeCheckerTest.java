package ppke.itk.xplang.parser;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.*;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ppke.itk.xplang.parser.MockName.name;

public class TypeCheckerTest {
    private final static Type a = new Scalar("A");
    private final static Type b = new Scalar("B");

    private Context context;

    @Before
    public void setUp() {
        this.context = new Context();
    }

    @Test
    public void resolvesValueExpressions() throws Exception {
        RValue value = new MockRValue(a);
        Expression expression = new ValueExpression(value);

        TypeChecker typeChecker = TypeChecker.in(context).checking(expression).build();
        RValue resolved = typeChecker.resolve();
        assertEquals(value, resolved);
    }

    @Test
    public void throwsIfTypeMismatch() throws Exception {
        RValue value = new MockRValue(a);
        Expression expression = new ValueExpression(value);

        TypeChecker typeChecker = TypeChecker.in(context).checking(expression).expecting(b).build();
        Throwable error = exceptionThrownBy(() -> typeChecker.resolve());
        assertThat(error, instanceOf(ParseError.class));
    }

    @Test
    public void resolvesFunctions() throws Exception {
        Signature f1 = new Signature(name("f"), a, a, a);
        Signature f2 = new Signature(name("f"), b, b, b);
        Set<FunctionDeclaration> candidates = new HashSet<>(asList(
            new MockFunctionDeclaration(f1),
            new MockFunctionDeclaration(f2)
        ));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        FunctionExpression expression = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(expression).build();
        RValue call = typeChecker.resolve();
        assertThat(call, instanceOf(FunctionCall.class));
        assertEquals(f1, ((FunctionCall) call).getDeclaration().signature());
    }

    @Test
    public void resolvesTrees() throws Exception {
        Signature f1 = new Signature(name("f"), a, a, a);
        Signature f2 = new Signature(name("f"), b, b, b);
        Set<FunctionDeclaration> candidates = new HashSet<>(asList(
            new MockFunctionDeclaration(f1),
            new MockFunctionDeclaration(f2)
        ));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        ValueExpression op3 = new ValueExpression(new MockRValue(a));
        FunctionExpression child = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));
        FunctionExpression root = new FunctionExpression(name("f"), Location.NONE, candidates, asList(child, op3));

        TypeChecker typeChecker = TypeChecker.in(context).checking(root).build();
        RValue call = typeChecker.resolve();
        assertThat(call, instanceOf(FunctionCall.class));
        FunctionCall funCall = (FunctionCall) call;
        assertEquals(f1, (funCall.getDeclaration().signature()));
        assertThat(funCall.arguments().get(0), instanceOf(FunctionCall.class));

        (new ASTPrinter()).visit(call);
    }

    @Test
    public void handleDifferentArityOverloads() throws Exception {
        Signature f1 = new Signature(name("f"), a, a);
        Signature f2 = new Signature(name("f"), a, a, a);
        Set<FunctionDeclaration> candidates = new HashSet<>(asList(
            new MockFunctionDeclaration(f1),
            new MockFunctionDeclaration(f2)
        ));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        FunctionExpression fCall = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).build();

        RValue call = typeChecker.resolve();
        (new ASTPrinter()).visit(call);

        assertThat(call, instanceOf(FunctionCall.class));
        FunctionCall funCall = (FunctionCall) call;
        assertEquals(f2, (funCall.getDeclaration().signature()));
    }

    @Test
    public void noViableFunction() throws Exception {
        Signature f1 = new Signature(name("f"), a, a, a);
        Signature f2 = new Signature(name("f"), b, b, b);
        Set<FunctionDeclaration> candidates = new HashSet<>(asList(
            new MockFunctionDeclaration(f1),
            new MockFunctionDeclaration(f2)
        ));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(b));
        FunctionExpression plus = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(plus).build();

        Throwable error = exceptionThrownBy(() -> typeChecker.resolve());
        error.printStackTrace();
        assertThat(error, instanceOf(ParseError.class));
        assertEquals(ErrorCode.NO_VIABLE_FUNCTIONS, ((ParseError) error).getErrorCode());
        assertThat(error.getMessage(), containsString("f: [A, A] -> A"));
        assertThat(error.getMessage(), containsString("f: [B, B] -> B"));
    }

    @Test
    public void topLevelCoercion() throws Exception {
        Signature coercion = new Signature(SpecialName.IMPLICIT_COERCION, b, a);
        context.registerFunction(new MockFunctionDeclaration(coercion));

        Signature f = new Signature(name("f"), a, a, a);
        Set<FunctionDeclaration> candidates = new HashSet<>();
        candidates.add(new MockFunctionDeclaration(f));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        FunctionExpression fCall = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).expecting(b).build();
        RValue call = typeChecker.resolve();
        (new ASTPrinter()).visit(call);

        assertThat(call, instanceOf(FunctionCall.class));
        FunctionCall funCall = (FunctionCall) call;
        assertEquals(coercion, funCall.getDeclaration().signature());
    }

    @Test
    public void argumentCoercion() throws Exception {
        Signature coercion = new Signature(SpecialName.IMPLICIT_COERCION, a, b);
        context.registerFunction(new MockFunctionDeclaration(coercion));

        Signature f = new Signature(name("f"), a, a, a);
        Set<FunctionDeclaration> candidates = new HashSet<>();
        candidates.add(new MockFunctionDeclaration(f));

        ValueExpression op1 = new ValueExpression(new MockRValue(b));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        FunctionExpression fCall = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));

        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).expecting(a).build();
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

    /*
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
        Signature coercion = new Signature(SpecialName.IMPLICIT_COERCION, b, a);
        context.registerFunction(new MockFunctionDeclaration(coercion));

        Signature f1 = new Signature(name("f"), a, a, a);
        Signature f2 = new Signature(name("f"), a, a, b);
        Set<FunctionDeclaration> candidates = new HashSet<>(asList(
            new MockFunctionDeclaration(f1),
            new MockFunctionDeclaration(f2)
        ));

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
        Set<FunctionDeclaration> candidates = new HashSet<>(asList(
            new MockFunctionDeclaration(f1),
            new MockFunctionDeclaration(f2)
        ));

        ValueExpression op1 = new ValueExpression(new MockRValue(a));
        ValueExpression op2 = new ValueExpression(new MockRValue(a));
        FunctionExpression fCall = new FunctionExpression(name("f"), Location.NONE, candidates, asList(op1, op2));
        TypeChecker typeChecker = TypeChecker.in(context).checking(fCall).expecting(a).build();

        Throwable error = exceptionThrownBy(() -> typeChecker.resolve());
        error.printStackTrace();
        assertThat(error, instanceOf(ParseError.class));
        assertEquals(ErrorCode.FUNCTION_AMBIGUOUS, ((ParseError) error).getErrorCode());
        assertThat(error.getMessage(), containsString("[A, A]"));
        assertThat(error.getMessage(), containsString("[A, B] -> A"));
        assertThat(error.getMessage(), containsString("[B, A] -> A"));
    }

    private static class MockFunctionDeclaration extends FunctionDeclaration {
        public MockFunctionDeclaration(Signature signature) {
            super(Location.NONE, signature);
        }

        @Override
        public void accept(ASTVisitor visitor) { }

        @Override
        public boolean isDefined() {
            return true;
        }
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
