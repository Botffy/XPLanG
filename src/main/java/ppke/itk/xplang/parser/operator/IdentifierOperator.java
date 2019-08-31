package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.singletonList;

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
        Location location = parser.actual().location();
        Name name = nameCreator.apply(token.lexeme());

        if (parser.context().isVariable(name)) {
            RValue Result = parser.context().getVariableValue(name, token);
            return new ValueExpression(Result);
        }

        if (parser.context().isType(name)) {
            Expression rhs = parser.parse(getPrecedence());
            return new FunctionExpression(
                SpecialName.TYPE_CONVERSION,
                location,
                parser.context().findFunctionsFor(SpecialName.TYPE_CONVERSION),
                singletonList(rhs)
            );
        }

        if (parser.context().isFunction(name)) {
            List<Expression> args = new ArrayList<>();
            args.add(parser.parse(getPrecedence()));
            // TODO more than one parameters :)

            return new FunctionExpression(
                name,
                parser.actual().location(),
                parser.context().findFunctionsFor(name),
                args
            );
        }

        throw new NameError(token);
    }

    @Override
    public int getPrecedence() {
        return Precedence.UNARY_PREFIX;
    }
}
