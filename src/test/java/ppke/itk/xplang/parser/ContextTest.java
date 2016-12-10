package ppke.itk.xplang.parser;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.Scope;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.type.Scalar;

import java.util.HashSet;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

public class ContextTest {
    private Context context;
    private Symbol dSymbol;

    @Before
    public void setUp() {
        this.context = new Context();
        this.dSymbol = Symbol.create().named("VAR").matchingLiteral("dummy").register(context);
    }

    @Test
    public void variableDeclarationsInScope() throws NameClashError {
        context.openScope();
        context.declareVariable(new Token(dSymbol, "egyes", 1, 1));
        context.declareVariable(new Token(dSymbol, "kettes", 2, 1));
        context.declareVariable(new Token(dSymbol, "hármas", 3, 1));

        Scope scope = context.closeScope();

        assertEquals(
            new HashSet<>(asList("egyes", "kettes", "hármas")),
            scope.variables().stream().map(VariableDeclaration::getName).collect(toSet())
        );
    }

    @Test
    public void variableNameClash() throws NameClashError {
        context.openScope();
        context.declareVariable(new Token(dSymbol, "egyes", 1, 1));
        Throwable throwable = exceptionThrownBy(() ->
            context.declareVariable(new Token(dSymbol, "egyes", 2, 1))
        );
        assertEquals(NameClashError.class, throwable.getClass());
    }

    @Test
    public void variableTypeNameClash() throws NameClashError {
        context.openScope();
        context.declareType("Egész", new Scalar("ExampleType"));
        Throwable throwable = exceptionThrownBy(() ->
            context.declareVariable(new Token(dSymbol, "Egész", 2, 1))
        );
        assertEquals(NameClashError.class, throwable.getClass());
    }
}
