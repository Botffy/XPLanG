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

/** {@code OpenStatement = OPEN LValue COLON Expression } */
public class OpenStatementParser {
    public static Statement parse(Parser parser) throws ParseError {
        // TODO: jobb hibakezelés

        Token token = parser.accept(Symbol.OPEN);
        Location startLocation = token.location();
        LValue var = LValueParser.parse(parser);
        parser.accept(Symbol.COLON);
        Expression fileName = parser.parseExpression();
        Location endLocation = fileName.getLocation();
        Location location = Location.between(startLocation, endLocation);

        Type varType = var.getType();
        Name openFunctionName = null;
        if (Archetype.INSTREAM_TYPE.accepts(varType)) {
            openFunctionName = SpecialName.OPEN_INPUT_FILE;
        } else if (Archetype.OUTSTREAM_TYPE.accepts(varType)) {
            openFunctionName = SpecialName.OPEN_OUTPUT_FILE;
        } else {
            throw new ParseError(var.location(), ErrorCode.TYPE_MISMATCH_OPEN_STREAM, var.getType());
        }

        Expression callExpression = new FunctionExpression(
            openFunctionName,
            location,
            parser.context().findFunctionsFor(openFunctionName),
            singletonList(fileName)
        );

        RValue call = TypeChecker.in(parser.context()).checking(callExpression).build().resolve();
        return new Assignment(location, var, call);
    }
}
