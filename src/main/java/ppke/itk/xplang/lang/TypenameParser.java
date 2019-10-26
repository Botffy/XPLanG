package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
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
        Token token = parser.accept(Symbol.IDENTIFIER);
        try {
            Type baseType = parser.context().lookupType(new PlangName(token.lexeme()), token);
            Type result = parseArrayType(parser, baseType).orElse(baseType);
            log.debug("Resolved type: {}", result);
            return result;
        } catch (ParseError error) {
            log.warn("Failed to resolve type {}", token.lexeme());
            parser.recordError(error.toErrorMessage());
            return Archetype.NONE;
        }
    }

    private static Optional<Type> parseArrayType(Parser parser, Type baseType) throws ParseError {
        Stack<Integer> lengths = new Stack<>();
        while(parser.actual().symbol().equals(Symbol.BRACKET_OPEN)) {
            parser.advance();

            Expression lengthExpression = parser.parseExpression();
            RValue address = TypeChecker.in(parser.context())
                .checking(lengthExpression)
                .expecting(parser.context().integerType())
                .withCustomErrorMessage(x -> new ParseError(x.location(), ErrorCode.ARRAY_LENGTH_EXPECT_INTEGER_EXPRESSION, x.getType()))
                .build()
                .resolve();

            if (!address.isStatic()) {
                throw new ParseError(address.location(), ErrorCode.ARRAY_LENGTH_EXPECT_STATIC);
            }

            int length = parser.eval(address, Integer.class);
            lengths.push(length);
            parser.accept(Symbol.BRACKET_CLOSE);
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
