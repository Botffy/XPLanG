package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Expression;
import ppke.itk.xplang.parser.FunctionExpression;
import ppke.itk.xplang.parser.Name;
import ppke.itk.xplang.parser.ParseError;

import java.util.Arrays;

public class InfixBinary implements Operator.Infix {
    private final Name functionName;
    private final int precedence;
    private final Associativity associativity;

    public InfixBinary(Name functionName, int precedence, Associativity associativity) {
        this.functionName = functionName;
        this.precedence = precedence;
        this.associativity = associativity;
    }

    public InfixBinary(Name functionName, int precedence) {
        this(functionName, precedence, Associativity.LEFT);
    }

    @Override public Expression parseInfix(Expression left, ExpressionParser parser) throws ParseError {
        Expression right = parser.parse(associativity == Associativity.LEFT ? precedence : precedence - 1);
        return new FunctionExpression(
            functionName,
            Location.between(left.getLocation(), right.getLocation()),
            parser.context().findFunctionsFor(functionName),
            Arrays.asList(left, right)
        );
    }

    @Override public int getPrecedence() {
        return precedence;
    }
}
