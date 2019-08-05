package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Name;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Symbol;

import static java.util.Collections.singletonList;

public class CircumfixOperator implements Operator.Prefix {
    private final Symbol closingPair;
    private final Name functionName;

    public CircumfixOperator(Symbol closingPair, Name functionName) {
        this.closingPair = closingPair;
        this.functionName = functionName;
    }

    @Override
    public Expression parsePrefix(ExpressionParser parser) throws ParseError {
        Location loc = parser.actual().location();
        Expression right = parser.parse(getPrecedence());
        parser.accept(closingPair, null);

        return new Function(
            loc,
            parser.context().lookupFunction(functionName).getDeclarations(),
            singletonList(right)
        );
    }

    @Override
    public int getPrecedence() {
        return Precedence.UNARY_PREFIX;
    }
}
