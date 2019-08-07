package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Conditional;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

import java.util.Collections;

/**
 * {@code Conditional = IF RValue THEN Sequence ELSE Sequence ENDIF}
 */
final class ConditionalParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private ConditionalParser() { /* empty private ctor */ }

    static Conditional parse(Parser parser) throws ParseError {
        log.debug("Conditional");
        Location startLoc = parser.accept(parser.symbol(PlangSymbol.IF)).location();

        Expression conditionExpression = parser.parseExpression();
        RValue condition = TypeChecker.in(parser.context())
            .checking(conditionExpression)
            .expecting(Archetype.BOOLEAN_TYPE)
            .withCustomErrorMessage(
                node -> new TypeError(translator.translate("plang.conditional_must_be_boolean"), node.location())
            )
            .build()
            .resolve();

        parser.accept(parser.symbol(PlangSymbol.THEN));
        Sequence ifBranch = SequenceParser.parse(
            parser, parser.symbol(PlangSymbol.ENDIF), parser.symbol(PlangSymbol.ELSE)
        );

        Sequence elseBranch = null;
        if(parser.actual().symbol().equals(parser.symbol(PlangSymbol.ELSE))) {
            parser.advance();
            elseBranch = SequenceParser.parse(parser, parser.symbol(PlangSymbol.ENDIF));
        } else {
            elseBranch = new Sequence(ifBranch.location().end.toUnaryLocation(), Collections.emptyList());
        }

        Location endLoc = parser.accept(parser.symbol(PlangSymbol.ENDIF)).location();
        return new Conditional(new Location(startLoc.start, endLoc.end), condition, ifBranch, elseBranch);
    }
}
