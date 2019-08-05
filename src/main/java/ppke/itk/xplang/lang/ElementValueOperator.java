package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.ElementVal;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Symbol;
import ppke.itk.xplang.parser.operator.Expression;
import ppke.itk.xplang.parser.operator.ExpressionParser;
import ppke.itk.xplang.parser.operator.Operator;
import ppke.itk.xplang.parser.operator.Value;

public class ElementValueOperator implements Operator.Infix {
    private final Symbol closingBracket;

    public ElementValueOperator(Symbol closingBracket) {
        this.closingBracket = closingBracket;
    }

    @Override
    public Expression parseInfix(Expression left, ExpressionParser parser) throws ParseError {
        // left holds the addressable, right is the address
        Location start = parser.actual().location();
        Expression right = parser.parse();
        parser.accept(closingBracket, null);
        Location end = parser.actual().location();

        RValue addressable = left.toASTNode();
        RValue address = right.toASTNode();
        Location location = Location.between(start, end);

        return new Value(new ElementVal(location, addressable, address));
    }

    @Override
    public int getPrecedence() {
        return Precedence.FUNCTION;
    }
}
