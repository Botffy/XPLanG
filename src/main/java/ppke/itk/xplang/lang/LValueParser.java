package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.ElementRef;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.ast.StringLiteral;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.RecordType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ppke.itk.xplang.lang.PlangName.name;

/**
 * {@code LValue = IDENTIFIER { [BRACKET_OPEN Expression BRACKET_CLOSE] [DOT IDENTIFIER] } }
 */
final class LValueParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");
    private final static Map<Symbol, Parselet> parselets = new HashMap<>();
    static {
        parselets.put(Symbol.BRACKET_OPEN, LValueParser::parseElementAccess);
        parselets.put(Symbol.DOT, LValueParser::parseFieldAccess);
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

    private static LValue parseFieldAccess(LValue base, Parser parser) throws ParseError {
        if (!(base.getType() instanceof RecordType)) {
            throw new ParseError(base.location(), ErrorCode.TYPE_MISMATCH_NOT_RECORD, base.getType());
        }
        RecordType type = (RecordType) base.getType();

        Token startToken = parser.accept(Symbol.DOT);
        Token field = parser.accept(Symbol.IDENTIFIER);
        Optional<RecordType.Field> maybeField = type.getField(name(field.lexeme()));
        if (!maybeField.isPresent()) {
            throw new ParseError(field.location(), ErrorCode.TYPE_MISMATCH_NO_SUCH_FIELD_IN_RECORD, type.getLabel(), field.lexeme());
        }

        return new ElementRef(
            Location.between(startToken.location(), field.location()),
            base.toRValue(),
            new StringLiteral(field.location(), Archetype.STRING_TYPE, name(field.lexeme()).toString()),
            maybeField.get().getType()
        );
    }

    @FunctionalInterface
    private interface Parselet {
        LValue parse(LValue base, Parser parser) throws ParseError;
    }
}
