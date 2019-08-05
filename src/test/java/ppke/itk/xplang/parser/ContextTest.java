package ppke.itk.xplang.parser;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.Scope;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.common.CursorPosition;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

import java.util.HashSet;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContextTest {
    private final static Location LOCATION = new CursorPosition(1,1).toUnaryLocation();

    private Context context;
    private Symbol dSymbol;
    private Type dType = new Scalar("Dummy");

    @Before
    public void setUp() {
        this.context = new Context();
        this.dSymbol = Symbol.create().named("VAR").matchingLiteral("dummy").build();
        context.register(dSymbol);
    }

    @Test
    public void variableDeclarationsInScope() throws NameClashError {
        context.openScope();
        context.declareVariable(name("egyes"), new Token(dSymbol, "egyes", LOCATION), dType);
        context.declareVariable(name("kettes"), new Token(dSymbol, "kettes", LOCATION), dType);
        context.declareVariable(name("hármas"), new Token(dSymbol, "hármas", LOCATION), dType);

        Scope scope = context.closeScope();

        assertEquals("Variables declared should be in the scope",
            new HashSet<>(asList("egyes", "kettes", "hármas")),
            scope.variables().stream().map(VariableDeclaration::getName).collect(toSet())
        );
    }

    @Test
    public void variableNameClash() throws NameClashError {
        context.openScope();
        context.declareVariable(name("egyes"), new Token(dSymbol, "egyes", LOCATION), dType);
        Throwable throwable = exceptionThrownBy(() ->
            context.declareVariable(name("egyes"), new Token(dSymbol, "egyes", LOCATION), dType)
        );
        assertEquals("Registering two variables by the same name should cause a name clash",
            NameClashError.class, throwable.getClass()
        );
    }

    @Test
    public void variableTypeNameClash() throws NameClashError {
        context.openScope();
        context.declareType(name("Egész"), new Scalar("ExampleType"));
        Throwable throwable = exceptionThrownBy(() ->
            context.declareVariable(name("Egész"), new Token(dSymbol, "Egész", LOCATION), dType)
        );
        assertEquals("Registering a variable by a name taken by a type should cause a name clash",
            NameClashError.class, throwable.getClass()
        );
    }

    @Test
    public void variableDeclarationsShouldHoldCanonicalName() throws ParseError {
        context.openScope();
        String lexeme = "HeLLoEs";
        Token token = new Token(dSymbol, lexeme, LOCATION);
        context.declareVariable(lowerCaseName(lexeme), token, dType);
        VariableDeclaration decl = context.getVariableReference(lowerCaseName(lexeme), token).getVariable();
        assertEquals(decl.getName(), lowerCaseName(lexeme).toString());
    }

    @Test
    public void lookupFailureNoSuchVariable() throws Exception {
        Throwable throwable = exceptionThrownBy(
            () -> context.getVariableValue(name("nonexistent"), new Token(dSymbol, "nonexistent", LOCATION))
        );

        assertTrue(throwable instanceof NoSuchVariableException);
        throwable.printStackTrace();
    }

    @Test
    public void lookupFailureIsNotVariable() throws Exception {
        context.openScope();
        context.declareType(name("Egész"), new Scalar("ExampleType"));

        Throwable throwable = exceptionThrownBy(
            () -> context.getVariableValue(name("Egész"), new Token(dSymbol, "Egész", LOCATION))
        );

        assertTrue(throwable instanceof NotVariableException);
        throwable.printStackTrace();
    }

    private static class TestName extends Name {
        private final String value;

        TestName(String name) {
            this.value = name;
        }

        @Override public boolean equals(Object obj) {
            return obj instanceof TestName && this.value.equals(((TestName) obj).value);
        }

        @Override public int hashCode() {
            return value.hashCode();
        }

        @Override public String toString() {
            return value;
        }
    }

    private static class LowerCaseName extends TestName {
        LowerCaseName(String name) {
            super(name.toLowerCase());
        }
    }

    private static TestName name(String str) {
        return new TestName(str);
    };

    private static LowerCaseName lowerCaseName(String str) {
        return new LowerCaseName(str);
    }
}
