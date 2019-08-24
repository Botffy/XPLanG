package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Assignment;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;

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
        Expression callExpression = new FunctionExpression(
            SpecialName.OPEN_FILE,
            fileName.getLocation(),
            parser.context().findFunctionsFor(SpecialName.OPEN_FILE),
            List.of(fileName)
        );

        RValue call = TypeChecker.in(parser.context()).checking(callExpression).build().resolve();
        Location endLocation = call.location();
        return new Assignment(Location.between(startLocation, endLocation), var, call);
    }
}
