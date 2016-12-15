package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;

public class PlangGrammar extends Grammar {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        try {
            makeSymbol(PlangSymbol.PROGRAM).register(ctx);
            makeSymbol(PlangSymbol.END_PROGRAM).register(ctx);
            makeSymbol(PlangSymbol.DECLARE).register(ctx);
            makeSymbol(PlangSymbol.IF).register(ctx);
            makeSymbol(PlangSymbol.THEN).register(ctx);
            makeSymbol(PlangSymbol.ELSE).register(ctx);
            makeSymbol(PlangSymbol.ENDIF).register(ctx);
            makeSymbol(PlangSymbol.ASSIGNMENT).register(ctx);
            makeSymbol(PlangSymbol.COLON).register(ctx);
            makeSymbol(PlangSymbol.COMMA).register(ctx);
            makeSymbol(PlangSymbol.BRACKET_OPEN).register(ctx);
            makeSymbol(PlangSymbol.BRACKET_CLOSE).register(ctx);
            makeSymbol(PlangSymbol.IDENTIFIER).withPrecedence(Symbol.Precedence.IDENTIFIER).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_INT).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_REAL).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_BOOL).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_CHAR).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_STRING).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.EOL).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.WHITESPACE).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.COMMENT).notSignificant().register(ctx);

            ctx.declareType(name("Egész"), Scalar.INTEGER_TYPE);
            ctx.declareType(name("Logikai"), Scalar.BOOLEAN_TYPE);
            ctx.declareType(name("Karakter"), Scalar.CHARACTER_TYPE);
            ctx.declareType(name("Valós"), Scalar.REAL_TYPE);
            ctx.declareType(name("Szöveg"), Scalar.STRING_TYPE);
        } catch(ParseError error) {
            throw new IllegalStateException("Failed to initialise PlangGrammar", error);
        }
    }

    /**
     * {@code start = Program}
     */
    @Override protected Root start(Parser parser) throws ParseError {
        log.debug("start");
        Program program = ProgramParser.parse(parser);
        return new Root(program.location(), program);
    }

    static PlangName name(String name) {
        return new PlangName(name);
    }

    private Symbol.Builder makeSymbol(PlangSymbol symbol) {
        log.trace("Creating symbol {} as {}", symbol.name(), symbol.getPattern());
        Symbol.Builder builder = Symbol.create()
            .caseInsensitive()
            .named(symbol.name());
        if(symbol.isLiteral()) {
            return builder.matchingLiteral(symbol.getPattern());
        } else {
            return builder.matching(symbol.getPattern());
        }
    }
}
