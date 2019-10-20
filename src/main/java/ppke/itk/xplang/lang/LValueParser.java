package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.ElementRef;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

import static ppke.itk.xplang.lang.PlangName.name;

/**
 * {@code LValue = IDENTIFIER [BRACKET_OPEN Expression BRACKET_CLOSE] }
 */
final class LValueParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private LValueParser() { /* empty private ctor */ }

    static LValue parse(Parser parser) throws ParseError {
        log.debug("LValue");
        Token token = parser.accept(Symbol.IDENTIFIER);

        LValue Result = parser.context().getVariableReference(name(token.lexeme()), token);
        while(parser.actual().symbol().equals(Symbol.BRACKET_OPEN)) {
            if (Result.getType().indexType().equals(Archetype.NONE)) {
                throw new ParseError(Result.location(), ErrorCode.TYPE_MISMATCH_NOT_ADDRESSABLE, Result.getType());
            }

            Token startToken = parser.advance();

            Expression indexExpression = parser.parseExpression();
            RValue index = TypeChecker.in(parser.context())
                .checking(indexExpression)
                .expecting(Result.getType().indexType())
                .withCustomErrorMessage(
                    node -> new ParseError(node.location(), ErrorCode.TYPE_MISMATCH_ARRAY_INDEX, node.getType())
                ).build()
                .resolve();

            Token endToken = parser.accept(Symbol.BRACKET_CLOSE);
            Result = new ElementRef(
                new Location(startToken.location().start, endToken.location().end),
                Result.toRValue(), index
            );
        }
        return Result;
    }
}
