package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.Output;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.StandardOutput;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code OutputStatement = OUT COLON Expression { COMMA Expression} }
 */
public class OutputStatementParser {
    public static Statement parse(Parser parser) throws ParseError {
        Token in = parser.accept(Symbol.OUT);
        Location startLoc = in.location();

        RValue outputStream = new StandardOutput();
        if (parser.actual().symbol().equals(Symbol.IDENTIFIER)) {
            outputStream = LValueParser.parse(parser).toRValue();
        }

        parser.accept(Symbol.COLON);

        List<RValue> values = new ArrayList<>();

        RValue value = parseOutputValue(parser);
        values.add(value);
        Location endLoc = value.location();

        while (parser.actual().symbol().equals(Symbol.COMMA)) {
            parser.advance();
            value = parseOutputValue(parser);
            values.add(value);
            endLoc = value.location();
        }

        if (!Archetype.OUTSTREAM_TYPE.accepts(outputStream.getType())) {
            throw new ParseError(
                outputStream.location(),
                ErrorCode.TYPE_MISMATCH_NOT_OUTPUTSTREAM, Archetype.OUTSTREAM_TYPE, outputStream.getType()
            );
        }

        return new Output(Location.between(startLoc, endLoc), outputStream, values);
    }

    private static RValue parseOutputValue(Parser parser) throws ParseError {
        Expression expression = parser.parseExpression();
        return TypeChecker.in(parser.context())
            .checking(expression)
            .build()
            .resolve();
    }
}
