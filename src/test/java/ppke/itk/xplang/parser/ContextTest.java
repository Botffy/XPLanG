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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ContextTest {
    private final static Location LOCATION = new CursorPosition(1,1).toUnaryLocation();

    private Context context;
    private Type dType = new Scalar("Dummy");

    @Before
    public void setUp() {
        this.context = new Context();
    }

    @Test
    public void variableDeclarationsInScope() throws ParseError {
        context.openScope();
        declare(context, name("egyes"), new Token(Symbol.IDENTIFIER, "egyes", LOCATION), dType);
        declare(context, name("kettes"), new Token(Symbol.IDENTIFIER, "kettes", LOCATION), dType);
        declare(context, name("hármas"), new Token(Symbol.IDENTIFIER, "hármas", LOCATION), dType);

        Scope scope = context.closeScope();

        assertEquals("Variables declared should be in the scope",
            new HashSet<>(asList("egyes", "kettes", "hármas")),
            scope.variables().stream().map(VariableDeclaration::getName).collect(toSet())
        );
    }

    @Test
    public void variableNameClash() throws ParseError {
        context.openScope();
        declare(context, name("egyes"), new Token(Symbol.IDENTIFIER, "egyes", LOCATION), dType);
        Throwable throwable = exceptionThrownBy(() ->
            declare(context, name("egyes"), new Token(Symbol.IDENTIFIER, "egyes", LOCATION), dType)
        );
        assertThat(throwable, instanceOf(ParseError.class));
        assertEquals(ErrorCode.NAME_CLASH, ((ParseError) throwable).getErrorCode());
   }

    @Test
    public void variableTypeNameClash() throws ParseError {
        context.openScope();
        context.declareType(name("Egész"), new Scalar("ExampleType"));
        Throwable throwable = exceptionThrownBy(() ->
            declare(context, name("Egész"), new Token(Symbol.IDENTIFIER, "Egész", LOCATION), dType)
        );
        assertThat(throwable, instanceOf(ParseError.class));
        assertEquals(ErrorCode.NAME_CLASH, ((ParseError) throwable).getErrorCode());
    }

    @Test
    public void variableDeclarationsShouldHoldCanonicalName() throws ParseError {
        context.openScope();
        String lexeme = "HeLLoEs";
        Token token = new Token(Symbol.IDENTIFIER, lexeme, LOCATION);
        declare(context, lowerCaseName(lexeme), token, dType);
        VariableDeclaration decl = context.getVariableReference(lowerCaseName(lexeme), token).getVariable();
        assertEquals(decl.getName(), lowerCaseName(lexeme).toString());
    }

    @Test
    public void lookupFailureNoSuchVariable() throws Exception {
        Throwable throwable = exceptionThrownBy(
            () -> context.getVariableValue(name("nonexistent"), new Token(Symbol.IDENTIFIER, "nonexistent", LOCATION))
        );

        assertThat(throwable, instanceOf(ParseError.class));
        assertEquals(ErrorCode.NO_SUCH_VARIABLE, ((ParseError) throwable).getErrorCode());
        throwable.printStackTrace();
    }

    @Test
    public void lookupFailureIsNotVariable() throws Exception {
        context.openScope();
        context.declareType(name("Egész"), new Scalar("ExampleType"));

        Throwable throwable = exceptionThrownBy(
            () -> context.getVariableValue(name("Egész"), new Token(Symbol.IDENTIFIER, "Egész", LOCATION))
        );

        assertThat(throwable, instanceOf(ParseError.class));
        assertEquals(ErrorCode.NOT_A_VARIABLE, ((ParseError) throwable).getErrorCode());
        throwable.printStackTrace();
    }

    private static class TestName implements Name {
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

    private void declare(Context context, Name name, Token token, Type type) throws ParseError {
        VariableDeclaration var = new VariableDeclaration(token.location(), name.toString(), type);
        context.declareVariable(name, var);
    }
}
