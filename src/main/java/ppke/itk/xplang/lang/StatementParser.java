package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;
import ppke.itk.xplang.parser.SyntaxError;

import static java.util.Arrays.asList;

/**
 * {@code Statement = Assignment | Conditional}
 */
final class StatementParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private StatementParser() { /* empty private ctor */ }

    static Statement parse(Parser parser) throws ParseError {
        log.debug("Statement");
        Symbol act = parser.actual().symbol();
        if(act.equals(PlangSymbol.IDENTIFIER.symbol())) {
            return AssignmentParser.parse(parser);
        } else if(act.equals(PlangSymbol.IF.symbol())) {
            return ConditionalParser.parse(parser);
        }

        throw new SyntaxError(asList(
            PlangSymbol.IDENTIFIER.symbol(),
            PlangSymbol.IF.symbol()
        ), act, parser.actual());
    }
}
