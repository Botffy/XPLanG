package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;

/**
 * {@code LValue = IDENTIFIER [BRACKET_OPEN INT_LITERAL BRACKET_CLOSE] }
 */
class LValueParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private LValueParser() { /* empty private ctor */ }

    static LValue parse(Parser parser) throws ParseError {
        log.debug("LValue");
        Token var = parser.accept(PlangSymbol.IDENTIFIER.symbol());

        LValue Result = parser.context().getVariableReference(var);
        if(parser.actual().symbol().equals(PlangSymbol.BRACKET_OPEN.symbol())) {
            parser.advance();
            int n = Integer.parseInt(parser.accept(PlangSymbol.LITERAL_INT.symbol()).lexeme());
            RValue address = new IntegerLiteral(n);
            parser.accept(PlangSymbol.BRACKET_CLOSE.symbol());
            Result = new ElementRef(toRValue(Result), address);
        }

        return Result;
    }

    private static RValue toRValue(LValue lValue) {
        if(lValue instanceof VarRef) {
            return new VarVal(((VarRef) lValue).getVariable());
        }

        throw new IllegalStateException("Could not convert LValue to RValue");
    }
}
