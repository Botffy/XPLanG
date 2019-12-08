package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Statement;
import ppke.itk.xplang.parser.*;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import static ppke.itk.xplang.lang.PlangName.name;

final class StatementParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private static final Map<Symbol, StatementParserFunction> statementParsers = new EnumMap<>(Symbol.class);
    static {
        statementParsers.put(Symbol.IDENTIFIER, StatementParser::identifier);
        statementParsers.put(Symbol.IF, ConditionalParser::parse);
        statementParsers.put(Symbol.LOOP, LoopParser::parse);
        statementParsers.put(Symbol.IN, InputStatementParser::parse);
        statementParsers.put(Symbol.OUT, OutputStatementParser::parse);
        statementParsers.put(Symbol.OPEN, OpenStatementParser::parse);
        statementParsers.put(Symbol.CLOSE, CloseStatementParser::parse);
        statementParsers.put(Symbol.ASSERT, AssertStatementParser::parse);
    }

    private StatementParser() { /* empty private ctor */ }

    static Statement parse(Parser parser) throws ParseError {
        log.debug("Statement");
        Symbol symbol = parser.actual().symbol();

        if (!statementParsers.containsKey(symbol)) {
            throw new ParseError(parser.actual().location(),
                ErrorCode.UNEXPECTED_SYMBOL_OF_ANY,
                statementParsers.keySet(),
                symbol
            );
        }

        StatementParserFunction statementParser = statementParsers.get(symbol);
        return statementParser.parse(parser);
    }

    public static Collection<? extends Symbol> getStatementSymbols() {
        return statementParsers.keySet();
    }

    private static Statement identifier(Parser parser) throws ParseError {
        // FIXME: this is a bit horrible, it screws up the error recovery. We really need a parse tree.
        Name name = name(parser.actual().lexeme());
        if (parser.context().isVariable(name)) {
            return AssignmentParser.parse(parser);
        } else {
            return ProcedureCallParser.parse(parser);
        }
    }

    @FunctionalInterface
    private interface StatementParserFunction {
        Statement parse(Parser parser) throws ParseError;
    }
}
