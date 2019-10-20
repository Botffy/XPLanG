package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.Symbol;
import ppke.itk.xplang.parser.ErrorCode;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;

/**
 * {@code Root = [Function] Program}
 */
class RootParser {
    private static final Logger log = LoggerFactory.getLogger(RootParser.class);

    private RootParser() { /* empty private ctor */ }

    static Root parse(Parser parser) throws ParseError {
        log.debug("Root");
        Location startLoc = parser.actual().location();
        Program program = null;

        while (!parser.actual().symbol().equals(Symbol.EOF)) {
            if (parser.actual().symbol().equals(Symbol.FUNCTION)) {
                FunctionParser.parse(parser);
                // TODO skip to end of function if error
            } else if (parser.actual().symbol().equals(Symbol.FORWARD_DECLARATION)) {
                FunctionParser.parseForwardDeclaration(parser);
                // TODO skip to end of line if error
            } else if (parser.actual().symbol().equals(Symbol.PROGRAM)) {
                if (program != null) {
                    throw new ParseError(parser.actual().location(), ErrorCode.ENTRY_POINT_ALREADY_DEFINED);
                }
                program = ProgramParser.parse(parser);
            }
        }

        if (program == null) {
            throw new ParseError(parser.actual().location(), ErrorCode.MISSING_ENTRY_POINT);
        }


        Location endLoc = parser.actual().location();
        return new Root(Location.between(startLoc, endLoc), program);
    }
}
