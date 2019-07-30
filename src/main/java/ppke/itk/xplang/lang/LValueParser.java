package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.parser.TypeError;
import ppke.itk.xplang.type.Archetype;

/**
 * {@code LValue = IDENTIFIER [BRACKET_OPEN INT_LITERAL BRACKET_CLOSE] }
 */
final class LValueParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");
    private final static Translator translator = Translator.getInstance("Plang");

    private LValueParser() { /* empty private ctor */ }

    static LValue parse(Parser parser) throws ParseError {
        log.debug("LValue");
        Token token = parser.accept(parser.symbol(PlangSymbol.IDENTIFIER));

        LValue Result = parser.context().getVariableReference(PlangGrammar.name(token.lexeme()), token);
        log.trace("LValue {}", Result.getType());
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.BRACKET_OPEN))) {
            Token startToken = parser.advance();
            RValue address = RValueParser.parse(parser);
            if(!Archetype.INTEGER_TYPE.accepts(address.getType())) {
                throw new TypeError(
                    translator.translate("plang.array_indextype_mismatch", address.getType()),
                    address.location()
                );
            }
            Token endToken = parser.accept(parser.symbol(PlangSymbol.BRACKET_CLOSE));
            Result = new ElementRef(
                new Location(startToken.location().start, endToken.location().end),
                toRValue(Result), address
            );
            log.trace("LValue {}", Result.getType());
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
