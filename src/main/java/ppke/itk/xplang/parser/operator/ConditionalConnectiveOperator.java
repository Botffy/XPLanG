package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.ConditionalConnective;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Expression;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.TypeChecker;
import ppke.itk.xplang.parser.ValueExpression;
import ppke.itk.xplang.type.Type;

public class ConditionalConnectiveOperator implements Operator.Infix {
    private final ConditionalConnective.Op operator;
    private final Type booleanType;

    public ConditionalConnectiveOperator(ConditionalConnective.Op operator, Type booleanType) {
        this.operator = operator;
        this.booleanType = booleanType;
    }

    @Override
    public Expression parseInfix(Expression leftExpression, ExpressionParser parser) throws ParseError {
        // TODO: better error messages

        Expression rightExpression = parser.parse(getPrecedence());

        RValue left = TypeChecker.in(parser.context())
            .checking(leftExpression)
            .expecting(booleanType)
            .build()
            .resolve();

        RValue right = TypeChecker.in(parser.context())
            .checking(rightExpression)
            .expecting(booleanType)
            .build()
            .resolve();

        return new ValueExpression(
            new ConditionalConnective(Location.between(left.location(), right.location()), operator, left, right)
        );
    }

    @Override
    public int getPrecedence() {
        return Precedence.LOGIC;
    }
}
