package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;

/**
 * {@code Declarations = DECLARE [ COLON ] {variableDeclaration} [{COMMA variableDeclaration}}
 */
final class DeclarationsParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private DeclarationsParser() { /* empty private ctor */ }

    static void parse(Parser parser) throws ParseError {
        log.debug("Declarations");
        parser.accept(parser.symbol(PlangSymbol.DECLARE),
            translator.translate(
                "plang.missing_declarations_keyword", parser.symbol(PlangSymbol.DECLARE).getPatternAsString()
            )
        );

        if (parser.actual().symbol().equals(parser.symbol(PlangSymbol.COLON))) {
            parser.advance();
        }

        VariableDeclarationParser.parse(parser);
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.COMMA))) {
            parser.advance();
            VariableDeclarationParser.parse(parser);
        }
    }
}
