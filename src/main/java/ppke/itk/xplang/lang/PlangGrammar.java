package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Root;
import ppke.itk.xplang.parser.*;

import java.util.stream.Stream;

public class PlangGrammar extends Grammar {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    public PlangGrammar() {
        super(Attribute.CASE_INSENSITIVE);
    }

    @Override
    public void setup(Context ctx) {
        log.debug("Setting up context");
        Stream.of(
            createSymbol("PROGRAM")    .matchingLiteral("program"),
            createSymbol("END_PROGRAM").matchingLiteral("program_vége"),
            createSymbol("IDENTIFIER")
                .matching("[a-zA-Záéíóöőúüű][a-zA-Z0-9_áéíóöőúüű]*")
                .withPrecedence(Symbol.Precedence.IDENTIFIER),
            createSymbol("FUNNY_STARE_LEFT").matchingLiteral("<_<"),
            createSymbol("FUNNY_STARE_RIGHT").matchingLiteral(">_>"),
            createSymbol("WHITESPACE")
                .matching("\\s+")
                .notSignificant(),
            createSymbol("COMMENT")
                .matching("\\*\\*.*")
                .notSignificant()
        ).forEach(x->x.register(ctx));
    }

    /**
     * {@code S = Program}
     */
    @Override public Root S(Parser parser) throws ParseError {
        log.debug("S");
        Program program = program(parser);
        return new Root(program);
    }

    /**
     * {@code Program = PROGRAM IDENTIFIER {FUNNY_STARE} END_PROGRAM}
     */
    protected Program program(Parser parser) throws ParseError {
        log.debug("Program");
        parser.accept("PROGRAM", "A programnak a PROGRAM kulcsszóval kell keződnie! (%s, %s)");
        Token nameToken = parser.accept("IDENTIFIER", "Hiányzik a program neve (egy azonosító).");

        while(!parser.actual().getSymbol().equals(parser.context().getSymbol("END_PROGRAM"))) {
            parser.advance();
        }

        parser.accept("END_PROGRAM", "A programot a PROGRAM_VÉGE kulcsszóval kell lezárni.");
        return new Program(nameToken.getLexeme());
    }
}
