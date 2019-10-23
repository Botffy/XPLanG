package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Function;
import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.ErrorCode;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            try {
                Symbol symbol = parser.actual().symbol();

                if (!parselets.containsKey(symbol)) {
                    throw new ParseError(parser.actual().location(), ErrorCode.EXPECTED_ROOT_ELEMENT, symbol);
                }

                parselets.get(symbol).parse(parser, state);
            } catch (ParseError error) {
                log.error("Parse error: ", error);
                parser.recordError(error.toErrorMessage());
                parser.skipToNext(parselets.keySet());
            }
        }

        if (state.program == null) {
            parser.recordError(CompilerMessage.error(parser.actual().location(), ErrorCode.MISSING_ENTRY_POINT));
        }

        for (FunctionDeclaration declaration : state.declarations) {
            if (!declaration.isDefined()) {
                parser.recordError(CompilerMessage.error(
                    declaration.location(), ErrorCode.FUNCTION_DECLARED_NOT_DEFINED, declaration.signature())
                );
            }
        }

        Location endLoc = parser.actual().location();
        return new Root(Location.between(startLoc, endLoc), state.program);
    }

    private static void parseFunctionDeclaration(Parser parser, State state) throws ParseError {
        FunctionParser.parse(parser);
    }

    private static void parseForwardDeclaration(Parser parser, State state) throws ParseError {
        Function function = FunctionParser.parseForwardDeclaration(parser);
        state.declarations.add(function);
    }

    private static void parseProgram(Parser parser, State state) throws ParseError {
        Program program = ProgramParser.parse(parser);
        if (state.program != null) {
            throw new ParseError(program.location(), ErrorCode.ENTRY_POINT_ALREADY_DEFINED);
        }
        state.program = program;
    }


    private static class State {
        List<FunctionDeclaration> declarations = new ArrayList<>();
        Program program = null;
    }

    @FunctionalInterface
    private interface Parselet {
        void parse(Parser parser, State state) throws ParseError;
    }
}
