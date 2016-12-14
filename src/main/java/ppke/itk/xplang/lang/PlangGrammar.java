package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;

import java.util.EnumSet;

public class PlangGrammar extends Grammar {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        try {
            EnumSet.allOf(PlangSymbol.class).stream().map(PlangSymbol::symbol).forEach(ctx::register);

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
}
