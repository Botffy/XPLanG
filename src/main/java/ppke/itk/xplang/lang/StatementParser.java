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
 * {@code Statement = Assignment | Conditional | Loop | InputStatement | OutputStatement | OpenStatement }
 */
final class StatementParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private StatementParser() { /* empty private ctor */ }

    static Statement parse(Parser parser) throws ParseError {
        log.debug("Statement");
        Symbol act = parser.actual().symbol();
        if(act.equals(parser.symbol(PlangSymbol.IDENTIFIER))) {
            return AssignmentParser.parse(parser);
        } else if(act.equals(parser.symbol(PlangSymbol.IF))) {
            return ConditionalParser.parse(parser);
        } else if (act.equals(parser.symbol(PlangSymbol.LOOP))) {
            return LoopParser.parse(parser);
        } else if (act.equals(parser.symbol(PlangSymbol.IN))) {
            return InputStatementParser.parse(parser);
        } else if (act.equals(parser.symbol(PlangSymbol.OUT))) {
            return OutputStatementParser.parse(parser);
        } else if (act.equals(parser.symbol(PlangSymbol.OPEN))) {
            return OpenStatementParser.parse(parser);
        } else if (act.equals(parser.symbol(PlangSymbol.CLOSE))) {
            return CloseStatementParser.parse(parser);
        }

        throw new SyntaxError(asList(
            parser.symbol(PlangSymbol.IDENTIFIER),
            parser.symbol(PlangSymbol.IF),
            parser.symbol(PlangSymbol.LOOP),
            parser.symbol(PlangSymbol.OUT),
            parser.symbol(PlangSymbol.IN),
            parser.symbol(PlangSymbol.OPEN),
            parser.symbol(PlangSymbol.CLOSE)
        ), act, parser.actual());
    }
}
