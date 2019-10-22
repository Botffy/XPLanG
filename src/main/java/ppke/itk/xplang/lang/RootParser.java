package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code Root = [Function] Program}
 */
class RootParser {
    private static final Logger log = LoggerFactory.getLogger("Root.Parser");

    private RootParser() { /* empty private ctor */ }

    private static Map<Symbol, Parselet> parselets = new HashMap<>();
    static {
        parselets.put(Symbol.FUNCTION, RootParser::parseFunctionDeclaration);
        parselets.put(Symbol.FORWARD_DECLARATION, RootParser::parseForwardDeclaration);
        parselets.put(Symbol.PROGRAM, RootParser::parseProgram);
    }

    static Root parse(Parser parser) throws ParseError {
        log.debug("Root");
        Location startLoc = parser.actual().location();
        State state = new State();

        while (!parser.actual().symbol().equals(Symbol.EOF)) {
            Symbol symbol = parser.actual().symbol();

            if (!parselets.containsKey(symbol)) {
                throw new ParseError(parser.actual().location(), ErrorCode.EXPECTED_ROOT_ELEMENT, symbol);
            }

            parselets.get(symbol).parse(parser, state);
        }

        if (state.program == null) {
            throw new ParseError(parser.actual().location(), ErrorCode.MISSING_ENTRY_POINT);
        }

        Location endLoc = parser.actual().location();
        return new Root(Location.between(startLoc, endLoc), state.program);
    }

    private static void parseFunctionDeclaration(Parser parser, State state) throws ParseError {
        FunctionParser.parse(parser);
    }

    private static void parseForwardDeclaration(Parser parser, State state) throws LexerError {
        try {
            FunctionParser.parseForwardDeclaration(parser);
        } catch (ParseError error) {
            parser.recordError(error.toErrorMessage());
            parser.skipToNext(Symbol.EOL);
        }
    }

    private static void parseProgram(Parser parser, State state) throws ParseError {
        if (state.program != null) {
            throw new ParseError(parser.actual().location(), ErrorCode.ENTRY_POINT_ALREADY_DEFINED);
        }
        state.program = ProgramParser.parse(parser);
    }


    private static class State {
        Program program = null;
    }

    @FunctionalInterface
    private interface Parselet {
        void parse(Parser parser, State state) throws ParseError;
    }
}
