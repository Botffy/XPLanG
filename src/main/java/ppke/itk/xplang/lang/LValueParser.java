package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Archetype;

import static ppke.itk.xplang.lang.PlangName.name;

/**
 * {@code LValue = IDENTIFIER [BRACKET_OPEN Expression BRACKET_CLOSE] }
 */
final class LValueParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");
    private final static Translator translator = Translator.getInstance("Plang");

    private LValueParser() { /* empty private ctor */ }

    static LValue parse(Parser parser) throws ParseError {
        log.debug("LValue");
        Token token = parser.accept(parser.symbol(PlangSymbol.IDENTIFIER));

        LValue Result = parser.context().getVariableReference(name(token.lexeme()), token);
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.BRACKET_OPEN))) {
            if (Result.getType().indexType().equals(Archetype.NONE)) {
                throw new TypeError(
                    translator.translate("plang.assignment_element_access_for_scalar", Result.getType()),
                    parser.actual().location()
                );
            }

            Token startToken = parser.advance();

            Expression indexExpression = parser.parseExpression();
            RValue index = TypeChecker.in(parser.context())
                .checking(indexExpression)
                .expecting(Result.getType().indexType())
                .withCustomErrorMessage(
                    node -> new TypeError(
                        translator.translate("plang.array_indextype_mismatch", node.getType()),
                        node.location()
                    )
                ).build()
                .resolve();

            Token endToken = parser.accept(parser.symbol(PlangSymbol.BRACKET_CLOSE));
            Result = new ElementRef(
                new Location(startToken.location().start, endToken.location().end),
                toRValue(Result), index
            );
        }
        return Result;
    }

    private static RValue toRValue(LValue lValue) {
        if(lValue instanceof VarRef) {
            return new VarVal(lValue.location(), ((VarRef) lValue).getVariable());
        } else if(lValue instanceof ElementRef) {
            ElementRef ref = (ElementRef) lValue;
            return new ElementVal(lValue.location(), ref.getAddressable(), ref.getAddress());
        }

        throw new IllegalStateException("Could not convert LValue to RValue");
    }
}
