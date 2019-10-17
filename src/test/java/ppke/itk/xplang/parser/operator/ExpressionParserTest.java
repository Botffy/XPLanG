package ppke.itk.xplang.parser.operator;

import com.github.stefanbirkner.fishbowl.Fishbowl;
import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.FunctionCall;
import ppke.itk.xplang.ast.IntegerLiteral;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Root;
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
    private final static Symbol NUMBER = Symbol.create().named("Number").matching("\\d+").build();
    private final static Symbol PLUS = Symbol.create().named("Plus").matchingLiteral("+").build();
    private final static Symbol TIMES = Symbol.create().named("Times").matchingLiteral("*").build();
    private final static Symbol IDENTIFIER = Symbol.create().named("Identifier").matching("[a-zA-Z][a-zA-Z0-9]*").build();
    private final static Symbol PAREN_OPEN = Symbol.create().named("ParenOpen").matching("\\(").build();
    private final static Symbol PAREN_CLOSE = Symbol.create().named("ParenClose").matching("\\)").build();
    private final static Symbol COMMA = Symbol.create().named("Comma").matching(",").build();
    private final static Symbol WHITESPACE = Symbol.create().named("Whitespace").matching("\\s+").notSignificant().build();

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
            ctx.openScope();
            ctx.register(NUMBER);
            ctx.register(PLUS);
            ctx.register(TIMES);
            ctx.register(PAREN_OPEN);
            ctx.register(PAREN_CLOSE);
            ctx.register(COMMA);
            ctx.register(IDENTIFIER);
            ctx.register(WHITESPACE);

            try {
                ctx.declareType(name("Int"), Archetype.INTEGER_TYPE);

                ctx.createBuiltin(name("plus"), Instruction.ISUM, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
                ctx.createBuiltin(name("times"), Instruction.IMUL, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE, Archetype.INTEGER_TYPE);
                ctx.createBuiltin(name("id"), Instruction.ID, Archetype.ANY, Archetype.ANY);
                ctx.createBuiltin(name("f"), Instruction.ID, Archetype.ANY, Archetype.ANY, Archetype.ANY);
            } catch(ParseError nameClashError) {
                throw new IllegalStateException(nameClashError);
            }

            ctx.infix(PLUS, new InfixBinary(name("plus"), Operator.Precedence.SUM));
            ctx.infix(TIMES, new InfixBinary(name("times"), Operator.Precedence.PRODUCT));
            ctx.prefix(NUMBER, new LiteralOperator<>(IntegerLiteral::new, Archetype.INTEGER_TYPE, Integer::valueOf));
            ctx.prefix(IDENTIFIER, new IdentifierOperator(ExpressionParserTest::name, new IdentifierOperator.FunctionSymbols(
                PAREN_OPEN, COMMA, PAREN_CLOSE
            )));
        }

        @Override protected Root start(Parser parser) throws ParseError {
            return null;
        }
    };

    private Parser parser;

    @Before public void setUp() {
        parser = new Parser();

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
        parser.context().declareVariable(name("a"), token(IDENTIFIER, "a"), Archetype.INTEGER_TYPE);

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
