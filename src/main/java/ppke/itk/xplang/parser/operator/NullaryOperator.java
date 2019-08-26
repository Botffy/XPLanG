package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Expression;
import ppke.itk.xplang.parser.FunctionExpression;
import ppke.itk.xplang.parser.Name;
import ppke.itk.xplang.parser.ParseError;

import static java.util.Collections.emptyList;

public class NullaryOperator implements Operator.Prefix {
    private final Name functionName;

    public NullaryOperator(Name functionName) {
        this.functionName = functionName;
    }

    @Override
    public Expression parsePrefix(ExpressionParser parser) throws ParseError {
        Location loc = parser.actual().location();
        return new FunctionExpression(functionName, loc, parser.context().findFunctionsFor(functionName), emptyList());
    }

    @Override
    public int getPrecedence() {
        return Precedence.UNARY_PREFIX;
    }
}
