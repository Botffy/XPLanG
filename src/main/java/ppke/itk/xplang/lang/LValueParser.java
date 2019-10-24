package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.ElementRef;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

import java.util.HashMap;
import java.util.Map;

import static ppke.itk.xplang.lang.PlangName.name;

/**
 * {@code LValue = IDENTIFIER [BRACKET_OPEN Expression BRACKET_CLOSE] }
 */
final class LValueParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");
    private final static Map<Symbol, Parselet> parselets = new HashMap<>();
    static {
        parselets.put(Symbol.BRACKET_OPEN, LValueParser::parseElementAccess);
    }

    private LValueParser() { /* empty private ctor */ }

    static LValue parse(Parser parser) throws ParseError {
        log.debug("LValue");
        Token token = parser.accept(Symbol.IDENTIFIER);

        LValue result = parser.context().getVariableReference(name(token.lexeme()), token);
        while(parselets.containsKey(parser.actual().symbol())) {
            result = parselets.get(parser.actual().symbol()).parse(result, parser);
        }
        return result;
    }

    private static LValue parseElementAccess(LValue base, Parser parser) throws ParseError {
        if (!base.getType().indexType().accepts(parser.context().integerType())) {
            throw new ParseError(base.location(), ErrorCode.TYPE_MISMATCH_NOT_ADDRESSABLE, base.getType());
        }

        Token startToken = parser.advance();

        Expression indexExpression = parser.parseExpression();
        RValue index = TypeChecker.in(parser.context())
            .checking(indexExpression)
            .expecting(base.getType().indexType())
            .withCustomErrorMessage(
                node -> new ParseError(node.location(), ErrorCode.TYPE_MISMATCH_ARRAY_INDEX, node.getType())
            ).build()
            .resolve();

        Token endToken = parser.accept(Symbol.BRACKET_CLOSE);
        return new ElementRef(
            new Location(startToken.location().start, endToken.location().end),
            base.toRValue(),
            index,
            base.getType().elementType()
        );
    }

    @FunctionalInterface
    private static interface Parselet {
        LValue parse(LValue base, Parser parser) throws ParseError;
    }
}
