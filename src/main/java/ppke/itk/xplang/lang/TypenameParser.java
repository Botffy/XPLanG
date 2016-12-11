package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.Type;

/**
 *  {@code Typename = Identifier}
 */
final class TypenameParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private TypenameParser() { /* empty private ctor */ }

    static Type parse(Parser parser) throws ParseError {
        log.debug("Typename");
        Token name = parser.accept(PlangSymbol.IDENTIFIER.symbol());
        return parser.context().lookupType(name);
    }

}
