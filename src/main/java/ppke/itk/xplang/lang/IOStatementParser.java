package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Signature;

/**
 * {@code Statement = IN | OUT Colon Expression }
 */
public class IOStatementParser {
    private final static Translator translator = Translator.getInstance("Plang");

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
        } else if (act.equals(parser.symbol(PlangSymbol.IN))) {
            Location startLoc = parser.advance().location();
            parser.accept(parser.symbol(PlangSymbol.COLON));

            LValue lValue = LValueParser.parse(parser);
            Location endLoc = lValue.location();
            Location location = Location.between(startLoc, endLoc);

            Signature reading = new Signature(SpecialName.READ_INPUT, lValue.getType());
            FunctionDeclaration function = parser.context().lookupFunction(reading).orElseThrow(() -> new TypeError(
                translator.translate("plang.cannot_read", lValue.getType()),
                location
            ));

            RValue rValue = new FunctionCall(location, function, new InputStreamVal(location));

            return new Assignment(location, lValue, rValue);
        }

        throw new IllegalStateException();
    }
}
