package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.ErrorRaising;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

class ErrorStatementParser {
    private final static Translator translator = Translator.getInstance("Plang");

    static ErrorRaising parse(Parser parser) throws ParseError {
        Location startLoc = parser.accept(parser.symbol(PlangSymbol.ERROR)).location();
        parser.accept(parser.symbol(PlangSymbol.COLON));

        Expression messageExpression = parser.parseExpression();
        Location endLoc = messageExpression.getLocation();
        RValue message = TypeChecker.in(parser.context())
            .checking(messageExpression)
            .expecting(Archetype.STRING_TYPE)
            .withCustomErrorMessage(
                node -> new TypeError(translator.translate("plang.error_argument_must_be_string"), node.location())
            )
            .build()
            .resolve();

        Location location = Location.between(startLoc, endLoc);

        return new ErrorRaising(location, message);
    }
}
