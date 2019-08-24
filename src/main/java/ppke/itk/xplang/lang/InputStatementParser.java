package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Signature;

import java.util.List;

/**
 * {@code InputStatement = IN COLON LValue }
 */
public class InputStatementParser {
    private final static Translator translator = Translator.getInstance("Plang");

    public static Input parse(Parser parser) throws ParseError {
        Token in = parser.accept(parser.symbol(PlangSymbol.IN));

        Location startLoc = in.location();
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
        Assignment assignment = new Assignment(location, lValue, rValue);

        return new Input(location, List.of(assignment));
    }
}
