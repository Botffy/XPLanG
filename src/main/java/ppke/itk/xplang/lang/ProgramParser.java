package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Block;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Scope;
import ppke.itk.xplang.ast.Sequence;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;

/**
 * {@code Program = PROGRAM IDENTIFIER [Declarations] Sequence END_PROGRAM}
 */
final class ProgramParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private ProgramParser() { /* empty private ctor */ }

    static Program parse(Parser parser) throws ParseError {
        log.debug("Program");

        Token startToken = parser.accept(parser.symbol(PlangSymbol.PROGRAM),
            translator.translate(
                "plang.program_keyword_missing", parser.symbol(PlangSymbol.PROGRAM).getPatternAsString()
            )
        );

        Token nameToken = parser.accept(parser.symbol(PlangSymbol.IDENTIFIER),
            translator.translate("plang.missing_program_name"));

        if(parser.actual().symbol().equals(parser.symbol(PlangSymbol.DECLARE))) {
            DeclarationsParser.parse(parser);
        }

        Sequence sequence = SequenceParser.parse(parser, parser.symbol(PlangSymbol.END_PROGRAM));

        Token endToken = parser.accept(parser.symbol(PlangSymbol.END_PROGRAM),
            translator.translate(
                "plang.missing_end_program", parser.symbol(PlangSymbol.END_PROGRAM).getPatternAsString()
            )
        );
        Scope scope = parser.context().closeScope();

        return new Program(
            Location.between(startToken.location(), endToken.location()),
            nameToken.lexeme(),
            new Block(scope, sequence)
        );
    }
}
