package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.FixArray;
import ppke.itk.xplang.type.Type;
import ppke.itk.xplang.util.Stack;

import java.util.Optional;

/**
 *  {@code Typename = Identifier { BRACKET_OPEN LITERAL_INT BRACKET_CLOSE }}
 */
final class TypenameParser {
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

    private static Optional<Type> parseArrayType(Parser parser, Type baseType) throws ParseError {
        Stack<Integer> lengths = new Stack<>();
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.BRACKET_OPEN))) {
            parser.advance();
            Token lengthToken = parser.advance();
            if (!lengthToken.symbol().equals(parser.symbol(PlangSymbol.LITERAL_INT))) {
                throw new ParseError(
                    lengthToken.location(), ErrorCode.ARRAY_LENGTH_EXPECT_INTEGER_LITERAL, lengthToken.symbol()
                );
            }
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
