package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.parser.Name;
import ppke.itk.xplang.parser.ParseError;

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

    @Override public void parseInfix(ExpressionParser parser) throws ParseError {
        parser.parse(associativity == Associativity.LEFT ? precedence : precedence - 1);
    }

    @Override public int getPrecedence() {
        return precedence;
    }
}
