package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Type;

/**
 *  {@code Typename = Identifier}
 */
final class TypenameParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private TypenameParser() { /* empty private ctor */ }

    static Type parse(Parser parser) throws ParseError {
        log.debug("Typename");
        Token token = parser.accept(PlangSymbol.IDENTIFIER.symbol());
        Type type = parser.context().lookupType(PlangGrammar.name(token.lexeme()), token);
        while(parser.actual().symbol().equals(PlangSymbol.BRACKET_OPEN.symbol())) {
            parser.advance();
            int length = Integer.parseInt(parser.accept(PlangSymbol.LITERAL_INT.symbol()).lexeme());
            parser.accept(PlangSymbol.BRACKET_CLOSE.symbol());
            type = FixArray.of(length, type);
        }

        log.debug("Resolved type: {}", type);

        return type;
    }
}
