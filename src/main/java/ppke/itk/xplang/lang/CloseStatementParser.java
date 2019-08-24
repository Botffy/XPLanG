package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Assignment;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.List;

/** {@code CloseStatement = CLOSE LValue */
public class CloseStatementParser {
    public static Statement parse(Parser parser) throws ParseError {
        // TODO jobb hibakezelés

        Token token = parser.accept(parser.symbol(PlangSymbol.CLOSE));
        Location startLocation = token.location();

        LValue ref = LValueParser.parse(parser);
        RValue var = ref.toRValue();
        Location endLocation = var.location();

        Expression callExpression = new FunctionExpression(
            SpecialName.CLOSE_FILE,
            var.location(),
            parser.context().findFunctionsFor(SpecialName.CLOSE_FILE),
            List.of(new ValueExpression(var))
        );

        RValue call = TypeChecker.in(parser.context()).checking(callExpression).build().resolve();

        return new Assignment(Location.between(startLocation, endLocation), ref, call);
    }
}
