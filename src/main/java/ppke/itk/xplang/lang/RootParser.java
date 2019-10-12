package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.FunctionDeclaration;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

/**
 * {@code Root = [Function] Program}
 */
class RootParser {
    private static final Logger log = LoggerFactory.getLogger(RootParser.class);

    private RootParser() { /* empty private ctor */ }

    static Root parse(Parser parser) throws ParseError {
        log.debug("Root");
        Location startLoc = parser.actual().location();

        Symbol act = parser.actual().symbol();

        if (act.equals(parser.symbol(PlangSymbol.FUNCTION))) {
            FunctionParser.parse(parser);
        }

        Program program = ProgramParser.parse(parser);

        Location endLoc = parser.actual().location();
        return new Root(Location.between(startLoc, endLoc), program);
    }
}
