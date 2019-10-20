package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Assignment;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

import static java.util.Collections.singletonList;

/** {@code CloseStatement = CLOSE LValue } */
public class CloseStatementParser {
    public static Statement parse(Parser parser) throws ParseError {
        Token token = parser.accept(Symbol.CLOSE);
        Location startLocation = token.location();

        LValue ref = LValueParser.parse(parser);
        RValue var = ref.toRValue();
        Location endLocation = var.location();
        Location location = Location.between(startLocation, endLocation);

        Type varType = var.getType();
        Name closeFunctionName = null;
        if (Archetype.INSTREAM_TYPE.accepts(varType)) {
            closeFunctionName = SpecialName.CLOSE_INPUTSTREAM;
        } else if (Archetype.OUTSTREAM_TYPE.accepts(varType)) {
            closeFunctionName = SpecialName.CLOSE_OUTPUTSTREAM;
        } else {
            throw new ParseError(location, ErrorCode.TYPE_MISMATCH_CLOSE_STREAM, varType);
        }

        Expression callExpression = new FunctionExpression(
            closeFunctionName,
            location,
            parser.context().findFunctionsFor(closeFunctionName),
            singletonList(new ValueExpression(var))
        );

        RValue call = TypeChecker.in(parser.context()).checking(callExpression).build().resolve();

        return new Assignment(location, ref, call);
    }
}
