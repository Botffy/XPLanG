package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.parser.Name;
import ppke.itk.xplang.parser.NameError;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Token;

import java.util.ArrayList;
import java.util.List;
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
    public Expression parsePrefix(ExpressionParser parser) throws ParseError {
        Token token = parser.actual();
        Name name = nameCreator.apply(token.lexeme());

        if (parser.context().isVariable(name)) {
            RValue Result = parser.context().getVariableValue(name, token);
            return new Value(Result);
        }

        if (parser.context().isFunction(name)) {
            List<Expression> args = new ArrayList<>();
            args.add(parser.parse());
            // TODO more than one parameters :)

            return new FunctionExpression(
                parser.actual().location(),
                parser.context().lookupFunction(name).getDeclarations(),
                args
            );
        }

        throw new NameError(token);
    }

    @Override
    public int getPrecedence() {
        return Precedence.FUNCTION;
    }
}
