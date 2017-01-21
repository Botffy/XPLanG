package ppke.itk.xplang.parser.operator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.FunctionCall;
import ppke.itk.xplang.ast.IntegerLiteral;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.function.Instruction;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;

import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.instanceOf;

public class ExpressionParserTest {
    private final static Symbol NUMBER = Symbol.create().named("Number").matching("\\d+").build();
    private final static Symbol PLUS = Symbol.create().named("Plus").matchingLiteral("+").build();
    private final static Symbol TIMES = Symbol.create().named("Times").matchingLiteral("*").build();

    private final static class TestName extends Name {
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

            try {
                ctx.createBuiltin(
                    name("plus"),
                    Instruction.ISUM, Scalar.INTEGER_TYPE, Scalar.INTEGER_TYPE, Scalar.INTEGER_TYPE
                );
                ctx.createBuiltin(
                    name("times"),
                    Instruction.IMUL, Scalar.INTEGER_TYPE, Scalar.INTEGER_TYPE, Scalar.INTEGER_TYPE
                );
            } catch(NameClashError nameClashError) {
                throw new IllegalStateException(nameClashError);
            }

            ctx.infix(PLUS, new InfixBinary(name("plus"), Operator.Precedence.SUM));
            ctx.infix(TIMES, new InfixBinary(name("times"), Operator.Precedence.PRODUCT));
            ctx.prefix(NUMBER, new LiteralOperator<>(IntegerLiteral::new, Integer::valueOf));
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
        RValue astNode = exp.toASTNode();

        Assert.assertThat("The root of the parse tree is a function call.",
            astNode, instanceOf(FunctionCall.class)
        );
    }

    @Test public void shouldParseSingleLiterals() throws ParseError {
        Reader reader = new StringReader("6");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression exp = ep.parse(Operator.Precedence.CONTAINING);
        RValue astNode = exp.toASTNode();

        Assert.assertEquals("The root of the parse tree should be the integer literal 6",
            Integer.valueOf(6), ((IntegerLiteral) astNode).getValue()
        );
    }

    @Test public void shouldHonorAssociativity() throws ParseError {
        Reader reader = new StringReader("6*3+2");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        Expression exp = ep.parse(Operator.Precedence.CONTAINING);
        RValue astNode = exp.toASTNode();

        Assert.assertEquals(
            "The root should be the plus operator",
            "plus", ((FunctionCall) astNode).getDeclaration().signature().getName()
        );
    }

    private static TestName name(String name) {
        return new TestName(name);
    }
}
