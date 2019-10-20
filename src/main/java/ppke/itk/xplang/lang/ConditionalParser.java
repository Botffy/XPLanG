package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Conditional;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Conditional = IF Condition THEN Sequence ELSE Sequence ENDIF}
 */
final class ConditionalParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private ConditionalParser() { /* empty private ctor */ }

    static Conditional parse(Parser parser) throws ParseError {
        log.debug("Conditional");
        Location startLoc = parser.accept(Symbol.IF).location();

        Conditional.Branch branch = parseBranch(parser);
        List<Conditional.Branch> branches = new ArrayList<>();
        while (parser.actual().symbol().equals(Symbol.ELSIF)) {
            parser.advance();
            branches.add(parseBranch(parser));
        }

        Sequence elseBranch = null;
        if(parser.actual().symbol().equals(Symbol.ELSE)) {
            parser.advance();
            elseBranch = SequenceParser.parse(parser, Symbol.ENDIF);
        } else {
            elseBranch = Sequence.emptySequence();
        }

        Location endLoc = parser.accept(Symbol.ENDIF).location();
        Location location = new Location(startLoc.start, endLoc.end);
        return new Conditional(location, branch, branches, elseBranch);
    }

    private static Conditional.Branch parseBranch(Parser parser) throws ParseError {
        RValue condition = ConditionParser.parse(parser);
        parser.accept(Symbol.THEN);
        Sequence sequence = SequenceParser.parse(
            parser, Symbol.ENDIF, Symbol.ELSIF, Symbol.ELSE
        );
        return new Conditional.Branch(condition, sequence);
    }
}
