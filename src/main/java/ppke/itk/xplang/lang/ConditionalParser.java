package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Conditional;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;

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
        Location startLoc = parser.accept(parser.symbol(PlangSymbol.IF)).location();

        Conditional.Branch branch = parseBranch(parser);
        List<Conditional.Branch> branches = new ArrayList<>();
        while (parser.actual().symbol().equals(parser.symbol(PlangSymbol.ELSIF))) {
            parser.advance();
            branches.add(parseBranch(parser));
        }

        Sequence elseBranch = null;
        if(parser.actual().symbol().equals(parser.symbol(PlangSymbol.ELSE))) {
            parser.advance();
            elseBranch = SequenceParser.parse(parser, parser.symbol(PlangSymbol.ENDIF));
        } else {
            elseBranch = Sequence.emptySequence();
        }

        Location endLoc = parser.accept(parser.symbol(PlangSymbol.ENDIF)).location();
        Location location = new Location(startLoc.start, endLoc.end);
        return new Conditional(location, branch, branches, elseBranch);
    }

    private static Conditional.Branch parseBranch(Parser parser) throws ParseError {
        RValue condition = ConditionParser.parse(parser);
        parser.accept(parser.symbol(PlangSymbol.THEN));
        Sequence sequence = SequenceParser.parse(
            parser, parser.symbol(PlangSymbol.ENDIF), parser.symbol(PlangSymbol.ELSIF), parser.symbol(PlangSymbol.ELSE)
        );
        return new Conditional.Branch(condition, sequence);
    }
}
