package ppke.itk.xplang.parser.operator;

import org.junit.Before;
import org.junit.Test;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.*;

import java.io.Reader;
import java.io.StringReader;

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

            ctx.infix(PLUS, new InfixBinary(name("plus"), Operator.Precedence.SUM));
            ctx.infix(TIMES, new InfixBinary(name("times"), Operator.Precedence.PRODUCT));
            ctx.prefix(NUMBER, new Literal<>(Integer::valueOf));
        }

        @Override protected Root start(Parser parser) throws ParseError {
            return null;
        }
    };

    private Parser parser;

    @Before public void setUp() {
        parser = new Parser();
    }

    @Test public void test() throws ParseError {
        Reader reader = new StringReader("5+6*2");
        parser.parse(reader, grammar);

        ExpressionParser ep = new ExpressionParser(parser);
        ep.parse(Operator.Precedence.CONTAINING);
    }

    private static TestName name(String name) {
        return new TestName(name);
    }
}
