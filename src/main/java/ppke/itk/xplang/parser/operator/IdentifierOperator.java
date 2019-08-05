package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.parser.Name;
import ppke.itk.xplang.parser.NameError;
import ppke.itk.xplang.parser.Token;

import java.util.function.Function;

/**
 * An identifier acts as a prefix operator in the Pratt parser, and may denote a variable or a function call.
 */
public class IdentifierOperator implements Operator.Prefix {
    private final Function<String, Name> nameCreator;

    public IdentifierOperator(Function<String, Name> nameCreator) {
        this.nameCreator = nameCreator;
    }

    @Override
    public Expression parsePrefix(ExpressionParser parser) throws NameError {
        Token token = parser.actual();
        Name name = nameCreator.apply(token.lexeme());
        RValue Result = parser.context().getVariableValue(name, token);
        return new Value(Result);
    }

    @Override
    public int getPrecedence() {
        return Precedence.NONE;
    }
}
