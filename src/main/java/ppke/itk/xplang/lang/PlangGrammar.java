package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.parser.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * {@code Program = PROGRAM IDENTIFIER {STATEMENT} END_PROGRAM}
     */
    protected Program program(Parser parser) throws ParseError {
        log.debug("Program");
        parser.accept("PROGRAM", "A programnak a PROGRAM kulcsszóval kell keződnie!");
        Token nameToken = parser.accept("IDENTIFIER", "Hiányzik a program neve (egy azonosító).");

        List<Statement> statementList = new ArrayList<>();
        while(!parser.actual().symbol().equals(parser.context().lookup("END_PROGRAM"))) {
            statementList.add(statement(parser));
        }

        parser.accept("END_PROGRAM", "A programot a PROGRAM_VÉGE kulcsszóval kell lezárni.");
        Scope scope = parser.context().closeScope();
        Sequence sequence = new Sequence(statementList);

        return new Program(nameToken.lexeme(), new Block(scope, sequence));
    }

    /**
     * @code Statement = FUNNY_STARE_LEFT | FUNNY_STARE_RIGHT
     */
    protected Statement statement(Parser parser) throws ParseError {
        log.debug("Statement");
        Symbol act = parser.actual().symbol();
        if(act.equals(parser.context().lookup("FUNNY_STARE_LEFT"))) {
            parser.advance();
            return new Decrementation();
        } else if(act.equals(parser.context().lookup("FUNNY_STARE_RIGHT"))) {
            parser.advance();
            return new Incrementation();
        }

        throw new SyntaxError(
            Arrays.asList(
                parser.context().lookup("FUNNY_STARE_LEFT"),
                parser.context().lookup("FUNNY_STARE_RIGHT")
            ), act, parser.actual()
        );
    }
}
