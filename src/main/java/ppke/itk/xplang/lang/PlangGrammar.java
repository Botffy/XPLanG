package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;
import ppke.itk.xplang.type.Type;

public class PlangGrammar extends Grammar {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        try {
            LexicalProperties props = new LexicalProperties();

            makeSymbol(PlangSymbol.PROGRAM, props).register(ctx);
            makeSymbol(PlangSymbol.END_PROGRAM, props).register(ctx);
            makeSymbol(PlangSymbol.DECLARE, props).register(ctx);
            makeSymbol(PlangSymbol.IF, props).register(ctx);
            makeSymbol(PlangSymbol.THEN, props).register(ctx);
            makeSymbol(PlangSymbol.ELSE, props).register(ctx);
            makeSymbol(PlangSymbol.ENDIF, props).register(ctx);
            makeSymbol(PlangSymbol.ASSIGNMENT, props).register(ctx);
            makeSymbol(PlangSymbol.COLON, props).register(ctx);
            makeSymbol(PlangSymbol.COMMA, props).register(ctx);
            makeSymbol(PlangSymbol.BRACKET_OPEN, props).register(ctx);
            makeSymbol(PlangSymbol.BRACKET_CLOSE, props).register(ctx);
            makeSymbol(PlangSymbol.IDENTIFIER, props).withPrecedence(Symbol.Precedence.IDENTIFIER).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_INT, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_REAL, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_BOOL, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_CHAR, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.LITERAL_STRING, props).withPrecedence(Symbol.Precedence.LITERAL).register(ctx);
            makeSymbol(PlangSymbol.EOL, props).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.WHITESPACE, props).notSignificant().register(ctx);
            makeSymbol(PlangSymbol.COMMENT, props).notSignificant().register(ctx);

            makeType(ctx, Scalar.BOOLEAN_TYPE, props);
            makeType(ctx, Scalar.INTEGER_TYPE, props);
            makeType(ctx, Scalar.REAL_TYPE, props);
            makeType(ctx, Scalar.CHARACTER_TYPE, props);
            makeType(ctx, Scalar.STRING_TYPE, props);
        } catch(ParseError | IllegalStateException error) {
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

    private Symbol.Builder makeSymbol(PlangSymbol symbol, LexicalProperties props) {
        return Symbol.create()
            .named(symbol.name())
            .matching(props.getSymbolPattern(symbol))
            .caseInsensitive();
    }

    private void makeType(Context ctx, Type type, LexicalProperties props) throws ParseError {
        ctx.declareType(name(props.getTypeName(type)), type);
    }
}
