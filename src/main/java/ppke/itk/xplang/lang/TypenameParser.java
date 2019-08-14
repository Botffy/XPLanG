package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Type;

/**
 *  {@code Typename = Identifier}
 */
final class TypenameParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private TypenameParser() { /* empty private ctor */ }

    static Type parse(Parser parser) throws ParseError {
        log.debug("Typename");
        Token token = parser.accept(parser.symbol(PlangSymbol.IDENTIFIER));
        Type type = parser.context().lookupType(new PlangName(token.lexeme()), token);
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.BRACKET_OPEN))) {
            parser.advance();
            Token lengthToken = parser.accept(
                parser.symbol(PlangSymbol.LITERAL_INT),
                translator.translate("plang.vardecl.array_length_must_be_integer")
            );
            int length = Integer.parseInt(lengthToken.lexeme());
            parser.accept(parser.symbol(PlangSymbol.BRACKET_CLOSE));
            type = FixArray.of(length, type).indexedBy(parser.context().integerType());
        }

        log.debug("Resolved type: {}", type);

        return type;
    }
}
