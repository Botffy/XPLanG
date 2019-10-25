package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

class ConstantParser {
    static VariableDeclaration parse(Parser parser) throws ParseError {
        Token startToken = parser.accept(Symbol.CONSTANT);
        Type type = TypenameParser.parse(parser);
        Token identifier = parser.accept(Symbol.IDENTIFIER);
        parser.accept(Symbol.OPERATOR_EQ);
        Expression expression = parser.parseExpression();


        Location location = Location.between(startToken.location(), expression.getLocation());
        if (!(type instanceof Scalar)) {
            throw new ParseError(location, ErrorCode.CONSTANT_CAN_ONLY_BE_SCALAR, type);
        }

        RValue value = TypeChecker.in(parser.context()).checking(expression).expecting(type).build().resolve();

        if (!value.isStatic()) {
            throw new ParseError(value.location(), ErrorCode.CONSTANT_MUST_BE_STATIC);
        }

        return new VariableDeclaration(location, identifier.lexeme(), type, value);
    }
}
