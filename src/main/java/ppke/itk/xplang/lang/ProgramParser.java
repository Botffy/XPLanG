package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Block;
import ppke.itk.xplang.ast.Program;
import ppke.itk.xplang.ast.Scope;
import ppke.itk.xplang.ast.Sequence;
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
        parser.accept(PlangSymbol.PROGRAM.symbol(),
            translator.translate("plang.program_keyword_missing", "PROGRAM"));  // TODO reverse lookup
        Token nameToken = parser.accept(PlangSymbol.IDENTIFIER.symbol(),
            translator.translate("plang.missing_program_name"));

        if(parser.actual().symbol().equals(PlangSymbol.DECLARE.symbol())) {
            DeclarationsParser.parse(parser);
        }

        Sequence sequence = SequenceParser.parse(parser, PlangSymbol.END_PROGRAM.symbol());

        parser.accept(PlangSymbol.END_PROGRAM.symbol(),
            translator.translate("plang.missing_end_program", "PROGRAM_VÉGE"));
        Scope scope = parser.context().closeScope();

        return new Program(nameToken.lexeme(), new Block(scope, sequence));
    }
}
