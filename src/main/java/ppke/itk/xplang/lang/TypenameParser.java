package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Type;
import ppke.itk.xplang.util.Stack;

import java.util.Optional;

/**
 *  {@code Typename = Identifier { BRACKET_OPEN LITERAL_INT BRACKET_CLOSE }}
 */
final class TypenameParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private TypenameParser() { /* empty private ctor */ }

    static Type parse(Parser parser) throws ParseError {
        log.debug("Typename");
        Token token = parser.accept(parser.symbol(PlangSymbol.IDENTIFIER));
        Type baseType = parser.context().lookupType(new PlangName(token.lexeme()), token);
        Type result = parseArrayType(parser, baseType).orElse(baseType);
        log.debug("Resolved type: {}", result);
        return result;
    }

    private static Optional<Type> parseArrayType(Parser parser, Type baseType) throws LexerError, SyntaxError {
        Stack<Integer> lengths = new Stack<>();
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.BRACKET_OPEN))) {
            parser.advance();
            Token lengthToken = parser.accept(
                parser.symbol(PlangSymbol.LITERAL_INT),
                translator.translate("plang.vardecl.array_length_must_be_integer")
            );
            int length = Integer.parseInt(lengthToken.lexeme());
            lengths.push(length);
            parser.accept(parser.symbol(PlangSymbol.BRACKET_CLOSE));
        }

        if (lengths.isEmpty()) {
            return Optional.empty();
        }

        Type result = FixArray.of(lengths.pop(), baseType).indexedBy(parser.context().integerType());
        while (!lengths.isEmpty()) {
            result = FixArray.of(lengths.pop(), result).indexedBy(parser.context().integerType());
        }
        return Optional.of(result);
    }
}
