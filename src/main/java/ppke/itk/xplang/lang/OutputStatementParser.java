package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Output;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

/**
 * {@code OutputStatement = OUT COLON Expression }
 */
public class OutputStatementParser {
    public static Statement parse(Parser parser) throws ParseError {
        Token in = parser.accept(parser.symbol(PlangSymbol.OUT));

        Location startLoc = in.location();
        parser.accept(parser.symbol(PlangSymbol.COLON));
        Expression expression = parser.parseExpression();

        RValue out = TypeChecker.in(parser.context())
            .checking(expression)
            .build()
            .resolve();
        Location endLoc = out.location();

        return new Output(Location.between(startLoc, endLoc), out);
    }
}
