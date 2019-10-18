package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.parser.*;

import java.util.EnumMap;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

final class StatementParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private static final Map<PlangSymbol, StatementParserFunction> statementParsers = new EnumMap<>(PlangSymbol.class);
    static {
        statementParsers.put(PlangSymbol.IDENTIFIER, AssignmentParser::parse);
        statementParsers.put(PlangSymbol.IF, ConditionalParser::parse);
        statementParsers.put(PlangSymbol.LOOP, LoopParser::parse);
        statementParsers.put(PlangSymbol.IN, InputStatementParser::parse);
        statementParsers.put(PlangSymbol.OUT, OutputStatementParser::parse);
        statementParsers.put(PlangSymbol.OPEN, OpenStatementParser::parse);
        statementParsers.put(PlangSymbol.CLOSE, CloseStatementParser::parse);
        statementParsers.put(PlangSymbol.ASSERT, AssertStatementParser::parse);
    }

    private StatementParser() { /* empty private ctor */ }

    static Statement parse(Parser parser) throws ParseError {
        log.debug("Statement");
        Symbol act = parser.actual().symbol();
        PlangSymbol symbol = PlangSymbol.valueOf(act.getName());

        if (!statementParsers.containsKey(symbol)) {
            throw new ParseError(parser.actual().location(),
                ErrorCode.UNEXPECTED_SYMBOL_OF_ANY,
                statementParsers.keySet().stream().map(parser::symbol).collect(toSet()),
                act
            );
        }

        StatementParserFunction statementParser = statementParsers.get(symbol);
        return statementParser.parse(parser);
    }

    @FunctionalInterface
    private interface StatementParserFunction {
        Statement parse(Parser parser) throws ParseError;
    }
}
