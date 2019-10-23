package ppke.itk.xplang.parser.operator;

import com.github.stefanbirkner.fishbowl.Fishbowl;
import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.ErrorLog;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ppke.itk.xplang.parser.TokenMock.token;

public class ExpressionParserTest {
    private final static class TestName implements Name {
        private final String name;

        public TestName(String name) {
            this.name = name;
        }

        @Override public boolean equals(Object that) {
            return that instanceof TestName && this.name.equals(((TestName) that).name);
        }

        @Override public int hashCode() {
            return name.hashCode();
        }

        @Override public String toString() {
            return name;
        }
    }

    private final static Grammar grammar = new Grammar() {
        @Override protected void setup(Context ctx) {
            try {
                ctx.declareType(name("Int"), Archetype.INTEGER_TYPE);

                ctx.createBuiltin(name("plus"), Instruction.ISUM, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
                ctx.createBuiltin(name("times"), Instruction.IMUL, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
                ctx.createBuiltin(name("id"), Instruction.ID, Archetype.ANY, Archetype.ANY);
                ctx.createBuiltin(name("f"), Instruction.ID, Archetype.ANY, Archetype.ANY, Archetype.ANY);
            } catch(ParseError nameClashError) {
                throw new IllegalStateException(nameClashError);
            }

            ctx.infix(Symbol.OPERATOR_PLUS, new InfixBinary(name("plus"), Operator.Precedence.SUM));
            ctx.infix(Symbol.OPERATOR_TIMES, new InfixBinary(name("times"), Operator.Precedence.PRODUCT));
            ctx.prefix(Symbol.LITERAL_INT, new LiteralOperator<>(IntegerLiteral::new, Archetype.INTEGER_TYPE, Integer::valueOf));
            ctx.prefix(Symbol.IDENTIFIER, new IdentifierOperator(ExpressionParserTest::name, new IdentifierOperator.FunctionSymbols(
                Symbol.PAREN_OPEN, Symbol.COMMA, Symbol.PAREN_CLOSE
            )));
        }

        @Override protected Root start(Parser parser) throws ParseError {
            return null;
        }
    };

    private Parser parser;

    @Before public void setUp() {
        parser = new Parser(new ErrorLog());

    }

    @Test public void shouldParseInfixOps() throws ParseError {
        Reader reader = new StringReader("6+2");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression exp = ep.parse(Operator.Precedence.CONTAINING);

        assertThat("The root of the parse tree is a function call.",
            exp, instanceOf(FunctionExpression.class)
        );

        FunctionExpression function = (FunctionExpression) exp;
        assertEquals(function.getName().toString(), "plus");
    }

    @Test public void shouldParseSingleLiterals() throws ParseError {
        Reader reader = new StringReader("6");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression exp = ep.parse(Operator.Precedence.CONTAINING);
        assertThat(exp, instanceOf(ValueExpression.class));

        RValue astNode = ((ValueExpression) exp).getRValue();

        assertEquals("The root of the parse tree should be the integer literal 6",
            Integer.valueOf(6), ((IntegerLiteral) astNode).getValue()
        );
    }

    @Test public void shouldHonorAssociativity() throws ParseError {
        Reader reader = new StringReader("6*3+2");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression exp = ep.parse(Operator.Precedence.CONTAINING);

        assertThat(exp, instanceOf(FunctionExpression.class));
        assertEquals(
            "The root should be the plus operator",
            "plus", ((FunctionExpression) exp).getName().toString()
        );
    }

    @Test public void shouldRaiseParseError() throws ParseError {
        Reader reader = new StringReader("6*");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);

        Throwable exception = Fishbowl.exceptionThrownBy(
            () -> ep.parse(Operator.Precedence.CONTAINING)
        );

        exception.printStackTrace();
        assertThat("ExpressionParser should throw a ParseError when it fails",
            exception, instanceOf(ParseError.class)
        );
    }

    @Test
    public void shouldHandleVariables() throws ParseError {
        parser.context().openScope();
        VariableDeclaration var = new VariableDeclaration(Location.NONE, "a", Archetype.INTEGER_TYPE);
        parser.context().declareVariable(name("a"), var);

        Reader reader = new StringReader("a");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression res = ep.parse(Operator.Precedence.CONTAINING);
        assertThat(res, instanceOf(ValueExpression.class));
    }

    @Test
    public void shouldHandleFunctions() throws ParseError {
        Reader reader = new StringReader("id 6");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression res = ep.parse(Operator.Precedence.CONTAINING);
        assertThat(res, instanceOf(FunctionExpression.class));
    }

    @Test
    public void shouldHandleFunctionsWithParenthesis() throws ParseError {
        Reader reader = new StringReader("f(5)");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression res = ep.parse(Operator.Precedence.CONTAINING);
        assertThat(res, instanceOf(FunctionExpression.class));
    }

    @Test
    public void shouldHandleFunctionsWithMultipleParameters() throws ParseError {
        Reader reader = new StringReader("f(5, 10 + 2)");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression res = ep.parse(Operator.Precedence.CONTAINING);
        assertThat(res, instanceOf(FunctionExpression.class));
    }

    private static TestName name(String name) {
        return new TestName(name);
    }
}
