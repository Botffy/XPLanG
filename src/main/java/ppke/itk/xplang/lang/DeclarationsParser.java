package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;

/**
 * {@code Declarations = DECLARE COLON {variableDeclaration} [{COMMA variableDeclaration}}
 */
final class DeclarationsParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private DeclarationsParser() { /* empty private ctor */ }

    static void parse(Parser parser) throws ParseError {
        log.debug("Declarations");
        parser.accept(PlangSymbol.DECLARE.symbol(),
            translator.translate(
                "plang.missing_declarations_keyword", PlangSymbol.DECLARE.symbol().getPatternAsString()
            )
        );
        parser.accept(PlangSymbol.COLON.symbol(),
            translator.translate(
                "plang.missing_colon_after_declarations_keyword", PlangSymbol.DECLARE.symbol().getPatternAsString()
            )
        );

        VariableDeclarationParser.parse(parser);
        while(parser.actual().symbol().equals(PlangSymbol.COMMA.symbol())) {
            parser.advance();
            VariableDeclarationParser.parse(parser);
        }
    }
}
