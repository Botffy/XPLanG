package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code InputStatement = IN [ LValue ] COLON LValue { COMMA LValue } }
 */
public class InputStatementParser {
    private final static Translator translator = Translator.getInstance("Plang");

    public static Input parse(Parser parser) throws ParseError {
        Token in = parser.accept(parser.symbol(PlangSymbol.IN));
        Location startLoc = in.location();

        RValue inputStream = new StandardInput();
        if (parser.actual().symbol().equals(parser.symbol(PlangSymbol.IDENTIFIER))) {
            inputStream = LValueParser.parse(parser).toRValue();
        }

        parser.accept(parser.symbol(PlangSymbol.COLON));

        List<Assignment> assignments = new ArrayList<>();
        LValue lValue = LValueParser.parse(parser);
        Location endLoc = lValue.location();
        assignments.addAll(getAssignments(parser, lValue, inputStream));

        while (parser.actual().symbol().equals(parser.symbol(PlangSymbol.COMMA))) {
            parser.advance();
            lValue = LValueParser.parse(parser);
            endLoc = lValue.location();
            assignments.addAll(getAssignments(parser, lValue, inputStream));
        }

        Location location = Location.between(startLoc, endLoc);
        return new Input(location, assignments);
    }

    private static List<Assignment> getAssignments(Parser parser, LValue lValue, RValue inputStream) throws TypeError {
        Type type = lValue.getType();
        if (Archetype.ANY_ARRAY.accepts(type)) {
            List<Assignment> assignments = new ArrayList<>();
            for (int i = 0; i < type.size(); ++i) {
                IntegerLiteral index = new IntegerLiteral(Location.NONE, type.indexType(), i);
                ElementRef ref = new ElementRef(
                    Location.NONE,
                    lValue.toRValue(),
                    index
                );

                assignments.addAll(getAssignments(parser, ref, inputStream));
            }

            return assignments;
        }

        Signature reading = new Signature(SpecialName.READ_INPUT, lValue.getType());
        FunctionDeclaration function = parser.context().lookupFunction(reading).orElseThrow(() -> new TypeError(
            translator.translate("plang.cannot_read", lValue.getType()),
            lValue.location()
        ));

        RValue rValue = new FunctionCall(lValue.location(), function, inputStream);
        return List.of(new Assignment(lValue.location(), lValue, rValue));
    }
}
