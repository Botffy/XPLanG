package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.Context;
import ppke.itk.xplang.parser.Grammar;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.type.Scalar;

import java.util.EnumSet;

public class PlangGrammar extends Grammar {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        try {
            EnumSet.allOf(PlangSymbol.class).stream().map(PlangSymbol::symbol).forEach(ctx::register);

            ctx.declareType("Egész", Scalar.INTEGER_TYPE);
            ctx.declareType("Logikai", Scalar.BOOLEAN_TYPE);
            ctx.declareType("Karakter", Scalar.CHARACTER_TYPE);
            ctx.declareType("Valós", Scalar.REAL_TYPE);
            ctx.declareType("Szöveg", Scalar.STRING_TYPE);
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
        return new Root(program);
    }
}
