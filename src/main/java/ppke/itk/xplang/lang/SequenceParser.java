package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * {@code Sequence = { Statement [ COMMA ] }}
 */

final class SequenceParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private SequenceParser() { /* empty private ctor */ }

    static Sequence parse(Parser parser, Symbol... stopSymbols) throws LexerError {
        log.debug("Sequence");
        Location startLoc = parser.actual().location();
        List<Statement> statementList = new ArrayList<>();
        List<Symbol> stoppers = asList(stopSymbols);
        do {
            try {
                statementList.add(StatementParser.parse(parser));

                if (parser.actual().symbol().equals(parser.symbol(PlangSymbol.COMMA))) {
                    parser.advance();
                }

            } catch(ParseError error) {
                log.error("Parse error: ", error);
                parser.recordError(error.toErrorMessage());
                parser.skipToNextLine();
                if(parser.actual().symbol().equals(Symbol.EOF)) break;
            }
        } while(!parser.actual().symbol().equals(Symbol.EOF) && !stoppers.contains(parser.actual().symbol()));

        Location endLoc = parser.actual().location();

        return new Sequence(Location.between(startLoc, endLoc), statementList);
    }
}
