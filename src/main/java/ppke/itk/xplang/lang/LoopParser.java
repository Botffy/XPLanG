package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Loop;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.common.CursorPosition;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

/**
 * {@code Loop = CIKLUS (AMÍG Condition Sequence CIKLUS_VÉGE) | (Sequence AMÍG Condition) }
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
            condition = ConditionParser.parse(parser);
            sequence = SequenceParser.parse(parser, parser.symbol(PlangSymbol.END_LOOP));
            endPos = parser.accept(parser.symbol(PlangSymbol.END_LOOP)).location().end;
        } else {
            type = Loop.Type.TEST_LAST;
            sequence = SequenceParser.parse(parser, parser.symbol(PlangSymbol.WHILE));
            parser.accept(parser.symbol(PlangSymbol.WHILE));
            condition = ConditionParser.parse(parser);
            endPos = condition.location().end;
        }

        Location loc = new Location(startLoc.start, endPos);
        return new Loop(loc, condition, sequence, type);
    }
}
