package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.Literal;
import ppke.itk.xplang.common.Location;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LiteralOperator<T> implements Operator.Prefix {
    private final Function<String, T> evaluation;
    private final BiFunction<Location, T, Literal<T>> nodeCreation;

    public LiteralOperator(BiFunction<Location, T, Literal<T>> nodeCreation, Function<String, T> evaluation) {
        this.evaluation = evaluation;
        this.nodeCreation = nodeCreation;
    }

    @Override public Expression parsePrefix(ExpressionParser parser) {
        return new Value(
            nodeCreation.apply(parser.actual().location(), evaluation.apply(parser.actual().lexeme()))
        );
    }

    /**
     * A literal has the lowest possible precedence.
     */
    @Override public int getPrecedence() {
        return Precedence.NONE;
    }
}
