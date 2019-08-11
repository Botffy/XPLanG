package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.ElementVal;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.parser.operator.ExpressionParser;
import ppke.itk.xplang.parser.operator.Operator;
import ppke.itk.xplang.type.Archetype;

import static java.util.Arrays.asList;

public class ElementValueOperator implements Operator.Infix {
    private final Symbol closingBracket;
    private final Symbol sliceSeparator;
    private final Name sliceFunctionName;

    public ElementValueOperator(Symbol closingBracket, Symbol sliceSeparator, Name sliceFunctionName) {
        this.closingBracket = closingBracket;
        this.sliceSeparator = sliceSeparator;
        this.sliceFunctionName = sliceFunctionName;
    }

    @Override
    public Expression parseInfix(Expression addressableExpression, ExpressionParser parser) throws ParseError {
        Location start = parser.actual().location();
        Expression indexExpression = parser.parse();
        Expression endIndexExpression = null;
        // FIXME this peek is a bit crap. The problem is, parser.actual() does not actually return the token under the
        //  head, it returns the last consumed token. Maybe we should rename ExpressionParser::actual to Op() or
        //  something.
        if (parser.peek().symbol().equals(sliceSeparator)) {
            parser.accept(sliceSeparator, null);
            endIndexExpression = parser.parse();
        }

        parser.accept(closingBracket, null);
        Location end = parser.actual().location();
        Location location = Location.between(start, end);

        if (endIndexExpression == null) {
            RValue addressable = TypeChecker.in(parser.context())
                .checking(addressableExpression)
                .expecting(Archetype.ADDRESSABLE)
                .build()
                .resolve();

            RValue address = TypeChecker.in(parser.context())
                .checking(indexExpression)
                .expecting(addressable.getType().indexType())
                .build()
                .resolve();

            return new ValueExpression(new ElementVal(location, addressable, address));
        }

        return new FunctionExpression(
            sliceFunctionName,
            location,
            parser.context().findFunctionsFor(sliceFunctionName),
            asList(addressableExpression, indexExpression, endIndexExpression)
        );
    }

    @Override
    public int getPrecedence() {
        return Precedence.FUNCTION;
    }
}
