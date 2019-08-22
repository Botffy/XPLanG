package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Output;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code OutputStatement = OUT COLON Expression { COMMA Expression} }
 */
public class OutputStatementParser {
    public static Statement parse(Parser parser) throws ParseError {
        Token in = parser.accept(parser.symbol(PlangSymbol.OUT));

        Location startLoc = in.location();
        parser.accept(parser.symbol(PlangSymbol.COLON));

        List<RValue> values = new ArrayList<>();

        RValue value = parseOutputValue(parser);
        values.add(value);
        Location endLoc = value.location();

        while (parser.actual().symbol().equals(parser.symbol(PlangSymbol.COMMA))) {
            parser.advance();
            value = parseOutputValue(parser);
            values.add(value);
            endLoc = value.location();
        }

        return new Output(Location.between(startLoc, endLoc), values);
    }

    private static RValue parseOutputValue(Parser parser) throws ParseError {
        Expression expression = parser.parseExpression();
        return TypeChecker.in(parser.context())
            .checking(expression)
            .build()
            .resolve();
    }
}
