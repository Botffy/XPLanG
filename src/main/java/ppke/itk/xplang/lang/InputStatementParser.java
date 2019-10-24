package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * {@code InputStatement = IN [ LValue ] COLON LValue { COMMA LValue } }
 */
public class InputStatementParser {
    public static Input parse(Parser parser) throws ParseError {
        Token in = parser.accept(Symbol.IN);
        Location startLoc = in.location();

        RValue inputStream = new StandardInput();
        if (parser.actual().symbol().equals(Symbol.IDENTIFIER)) {
            inputStream = LValueParser.parse(parser).toRValue();
        }

        parser.accept(Symbol.COLON);

        List<Assignment> assignments = new ArrayList<>();
        LValue lValue = LValueParser.parse(parser);
        Location endLoc = lValue.location();
        assignments.addAll(getAssignments(parser, lValue, inputStream));

        while (parser.actual().symbol().equals(Symbol.COMMA)) {
            parser.advance();
            lValue = LValueParser.parse(parser);
            endLoc = lValue.location();
            assignments.addAll(getAssignments(parser, lValue, inputStream));
        }

        Location location = Location.between(startLoc, endLoc);
        return new Input(location, assignments);
    }

    private static List<Assignment> getAssignments(Parser parser, LValue lValue, RValue inputStream) throws ParseError {
        Type type = lValue.getType();
        if (Archetype.ANY_ARRAY.accepts(type)) {
            List<Assignment> assignments = new ArrayList<>();
            for (int i = 0; i < type.size(); ++i) {
                IntegerLiteral index = new IntegerLiteral(Location.NONE, type.indexType(), i);
                ElementRef ref = new ElementRef(
                    Location.NONE,
                    lValue.toRValue(),
                    index,
                    lValue.getType().elementType()
                );

                assignments.addAll(getAssignments(parser, ref, inputStream));
            }

            return assignments;
        }

        // FIXME: this is very naive. Maybe there should be a full function resolution here, but I'm not really sure it's worth it
        Signature reading = new Signature(SpecialName.READ_INPUT, lValue.getType());
        FunctionDeclaration function = parser.context().lookupFunction(reading).orElseThrow(() -> new ParseError(
            lValue.location(), ErrorCode.TYPE_MISMATCH_NOT_READABLE, lValue.getType()
        ));

        RValue rValue = function.call(lValue.location(), singletonList(inputStream));
        return singletonList(new Assignment(lValue.location(), lValue, rValue));
    }
}
