package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Assignment;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Type;

import java.util.List;

/** {@code OpenStatement = OPEN LValue COLON Expression */
public class OpenStatementParser {
    private final static Translator translator = Translator.getInstance("Plang");

    public static Statement parse(Parser parser) throws ParseError {
        // TODO: jobb hibakezelés

        Token token = parser.accept(parser.symbol(PlangSymbol.OPEN));
        Location startLocation = token.location();
        LValue var = LValueParser.parse(parser);
        parser.accept(parser.symbol(PlangSymbol.COLON));
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
            throw new TypeError(
                translator.translate("plang.open_only_streams", Archetype.INSTREAM_TYPE, Archetype.OUTSTREAM_TYPE, varType),
                location
            );
        }

        Expression callExpression = new FunctionExpression(
            openFunctionName,
            location,
            parser.context().findFunctionsFor(openFunctionName),
            List.of(fileName)
        );

        RValue call = TypeChecker.in(parser.context()).checking(callExpression).build().resolve();
        return new Assignment(location, var, call);
    }
}
