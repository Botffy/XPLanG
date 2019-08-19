package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Output;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

public class IOStatementParser {
    public static Statement parse(Parser parser) throws ParseError {
        Symbol act = parser.actual().symbol();

        if (act.equals(parser.symbol(PlangSymbol.OUT))) {
            Location startLoc = parser.advance().location();
            parser.accept(parser.symbol(PlangSymbol.COLON));
            Expression expression = parser.parseExpression();

            RValue out = TypeChecker.in(parser.context())
                .checking(expression)
                .build()
                .resolve();
            Location endLoc = out.location();

            return new Output(Location.between(startLoc, endLoc), out);
        }

        throw new IllegalStateException();
    }
}
