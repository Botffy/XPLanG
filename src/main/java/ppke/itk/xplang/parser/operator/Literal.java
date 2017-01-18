package ppke.itk.xplang.parser.operator;

import java.util.function.Function;

public class Literal<T> implements Operator.Prefix {
    private final Function<String, T> evaluation;

    public Literal(Function<String, T> evaluation) {
        this.evaluation = evaluation;
    }

    @Override public void parsePrefix(ExpressionParser parser) {
        System.out.println(evaluation.apply(parser.actual().lexeme()));
    }

    /**
     * A literal has the lowest possible precedence.
     */
    @Override public int getPrecedence() {
        return Precedence.NONE;
    }
}
