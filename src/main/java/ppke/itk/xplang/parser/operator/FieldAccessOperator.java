package ppke.itk.xplang.parser.operator;

import ppke.itk.xplang.ast.ElementVal;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.StringLiteral;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.lang.PlangName;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.RecordType;

import java.util.Optional;

import static ppke.itk.xplang.lang.PlangName.name;

public class FieldAccessOperator implements Operator.Infix {
    @Override
    public Expression parseInfix(Expression recordExpression, ExpressionParser parser) throws ParseError {
        Location start = parser.actual().location();
        Token fieldToken = parser.accept(Symbol.IDENTIFIER);
        Name fieldName = PlangName.name(fieldToken.lexeme());

        RValue addressable = TypeChecker.in(parser.context()).checking(recordExpression).build().resolve();
        if (!(addressable.getType() instanceof RecordType)) {
            throw new ParseError(addressable.location(), ErrorCode.TYPE_MISMATCH_NOT_RECORD, addressable.getType());
        }

        RecordType recordType = (RecordType) addressable.getType();
        Optional<RecordType.Field> field = recordType.getField(fieldName);
        if (!field.isPresent()) {
            throw new ParseError(
                fieldToken.location(),
                ErrorCode.TYPE_MISMATCH_NO_SUCH_FIELD_IN_RECORD,
                recordType.getLabel(),
                fieldToken.lexeme()
            );
        }

        RValue fieldValue = new StringLiteral(fieldToken.location(), Archetype.STRING_TYPE, field.get().getName().toString());
        Location location = Location.between(start, fieldToken.location());

        return new ValueExpression(new ElementVal(location, addressable, fieldValue, field.get().getType()));
    }

    @Override
    public int getPrecedence() {
        return Precedence.FUNCTION;
    }
}
