package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.ElementVal;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Slice;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Symbol;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.parser.operator.ExpressionParser;
import ppke.itk.xplang.parser.operator.Operator;
import ppke.itk.xplang.type.Archetype;

/**
 * Parse an element access or slice expression.
 * <p>
 * An ADDRESSABLE type (array or string) contains multiple elements. The ElementValueOperator can be used to access
 * these elements, or a slice (subarray, substring) of the variable.
 */
class ElementValueOperator implements Operator.Infix {
    private final Symbol closingBracket;
    private final Symbol sliceSeparator;

    /**
     * Create the operator.
     *
     * @param closingBracket The closing symbol.
     * @param sliceSeparator The optional separator between the startIndex and the endIndex of a slice expression.
     */
    ElementValueOperator(Symbol closingBracket, Symbol sliceSeparator) {
        this.closingBracket = closingBracket;
        this.sliceSeparator = sliceSeparator;
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
            parser.accept(sliceSeparator);
            endIndexExpression = parser.parse();
        }

        parser.accept(closingBracket);
        Location end = parser.actual().location();
        Location location = Location.between(start, end);

        if (endIndexExpression == null) {
            RValue addressable = TypeChecker.in(parser.context())
                .checking(addressableExpression)
                .expecting(Archetype.ADDRESSABLE)
                .withCustomErrorMessage(x -> new ParseError(x.location(), ErrorCode.TYPE_MISMATCH_NOT_ADDRESSABLE, x.getType()))
                .build()
                .resolve();

            RValue address = TypeChecker.in(parser.context())
                .checking(indexExpression)
                .expecting(addressable.getType().indexType())
                .withCustomErrorMessage(x -> new ParseError(x.location(), ErrorCode.TYPE_MISMATCH_ARRAY_INDEX, x.getType()))
                .build()
                .resolve();

            return new ValueExpression(
                new ElementVal(location, addressable, address, addressable.getType().elementType())
            );
        }

        RValue slicable = TypeChecker.in(parser.context())
            .checking(addressableExpression)
            .expecting(Archetype.STRING_TYPE) // classic PLanG only allows String types to be sliced.
            .withCustomErrorMessage(x -> new ParseError(x.location(), ErrorCode.TYPE_MISMATCH_NOT_SLICABLE, x.getType()))
            .build()
            .resolve();

        RValue startIndex = TypeChecker.in(parser.context())
            .checking(indexExpression)
            .expecting(slicable.getType().indexType())
            .withCustomErrorMessage(x -> new ParseError(x.location(), ErrorCode.TYPE_MISMATCH_SLICE_INDEX, x.getType()))
            .build()
            .resolve();

        RValue endIndex = TypeChecker.in(parser.context())
            .checking(endIndexExpression)
            .expecting(slicable.getType().indexType())
            .withCustomErrorMessage(x -> new ParseError(x.location(), ErrorCode.TYPE_MISMATCH_NOT_ADDRESSABLE, x.getType()))
            .build()
            .resolve();

        return new ValueExpression(new Slice(location, slicable, startIndex, endIndex));
    }

    @Override
    public int getPrecedence() {
        return Precedence.FUNCTION;
    }
}
