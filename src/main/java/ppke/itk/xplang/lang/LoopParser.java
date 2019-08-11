package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Loop;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.common.CursorPosition;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

/**
 * {@code Loop = CIKLUS (AMÍG Expression Sequence CIKLUS_VÉGE) | (Sequence AMÍG Expression) }
 */
public class LoopParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private LoopParser() { /* empty private ctor */ }

    static Loop parse(Parser parser) throws ParseError {
        log.debug("Loop");

        Location startLoc = parser.accept(parser.symbol(PlangSymbol.LOOP)).location();
        CursorPosition endPos;

        Symbol act = parser.actual().symbol();
        RValue condition;
        Sequence sequence;
        Loop.Type type;
        if (act.equals(parser.symbol(PlangSymbol.WHILE))) {
            type = Loop.Type.TEST_FIRST;
            parser.accept(parser.symbol(PlangSymbol.WHILE));
            condition = parseCondition(parser);
            sequence = SequenceParser.parse(parser, parser.symbol(PlangSymbol.END_LOOP));
            endPos = parser.accept(parser.symbol(PlangSymbol.END_LOOP)).location().end;
        } else {
            type = Loop.Type.TEST_LAST;
            sequence = SequenceParser.parse(parser, parser.symbol(PlangSymbol.WHILE));
            parser.accept(parser.symbol(PlangSymbol.WHILE));
            condition = parseCondition(parser);
            endPos = condition.location().end;
        }

        Location loc = new Location(startLoc.start, endPos);
        return new Loop(loc, condition, sequence, type);
    }

    private static RValue parseCondition(Parser parser) throws ParseError, SemanticError {
        Expression conditionExpression = parser.parseExpression();
        return TypeChecker.in(parser.context())
            .checking(conditionExpression)
            .expecting(Archetype.BOOLEAN_TYPE)
            .withCustomErrorMessage(
                node -> new TypeError(translator.translate("plang.conditional_must_be_boolean"), node.location())
            )
            .build()
            .resolve();
    }
}
