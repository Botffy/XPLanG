package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Assignment;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.TypeError;

/**
 * {@code Assignment = LValue ASSIGNMENT RValue}
 */
final class AssignmentParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private AssignmentParser() { /* empty private ctor */ }

    static Assignment parse(Parser parser) throws ParseError {
        log.debug("Assignment");
        LValue lhs = LValueParser.parse(parser);
        parser.accept(PlangSymbol.ASSIGNMENT.symbol());
        Location loc = parser.actual().location(); // FIXME this should be queried from RValue.
        RValue rhs = RValueParser.parse(parser);
        if(!lhs.getType().accepts(rhs.getType())) {
            throw new TypeError(translator.translate("plang.assignments_must_match"), loc);
        }
        return new Assignment(
            lhs,
            rhs
        );
    }
}
