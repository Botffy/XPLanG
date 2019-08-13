package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.Literal;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Expression;
import ppke.itk.xplang.parser.ValueExpression;
import ppke.itk.xplang.type.Type;

import java.util.function.Function;

public class LiteralOperator<T> implements Operator.Prefix {
    private final NodeCreation<T> nodeCreation;
    private final Type type;
    private final Function<String, T> evaluation;

    public LiteralOperator(NodeCreation<T> nodeCreation, Type type, Function<String, T> evaluation) {
        this.type = type;
        this.evaluation = evaluation;
        this.nodeCreation = nodeCreation;
    }

    @Override public Expression parsePrefix(ExpressionParser parser) {
        return new ValueExpression(
            nodeCreation.apply(parser.actual().location(), type, evaluation.apply(parser.actual().lexeme()))
        );
    }

    /**
     * A literal has the lowest possible precedence.
     */
    @Override public int getPrecedence() {
        return Precedence.NONE;
    }

    @FunctionalInterface
    public interface NodeCreation<T> {
        public Literal<T> apply(Location location, Type type, T value);
    }
}
