package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Expression;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Symbol;

public class Grouping implements Operator.Prefix {
    private final Symbol closingPair;

    public Grouping(Symbol closingPair) {
        this.closingPair = closingPair;
    }

    @Override
    public int getPrecedence() {
        return Precedence.GROUPING;
    }

    @Override
    public Expression parsePrefix(ExpressionParser parser) throws ParseError {
        Location loc = parser.actual().location();
        Expression exp = parser.parse(Precedence.CONTAINING);
        parser.accept(closingPair);
        return exp;
    }
}
