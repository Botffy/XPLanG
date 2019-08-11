package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.parser.NameClashError;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code VariableDeclaration = IDENTIFIER [{COMMA IDENTIFIER}] COLON Typename}
 */
final class VariableDeclarationParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private VariableDeclarationParser() { /* empty private ctor */ }

    static void parse(Parser parser) throws ParseError {
        log.debug("VariableDeclaration");
        List<Token> variables = new ArrayList<>();

        variables.add(parser.accept(parser.symbol(PlangSymbol.IDENTIFIER)));
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.COMMA))) {
            parser.advance(); // consume the comma
            variables.add(parser.accept(parser.symbol(PlangSymbol.IDENTIFIER)));
        }
        parser.accept(parser.symbol(PlangSymbol.COLON));
        Type type = TypenameParser.parse(parser);

        for(Token token : variables) {
            try {
                parser.context().declareVariable(new PlangName(token.lexeme()), token, type);
            } catch(NameClashError error) {
                parser.recordError(error.toErrorMessage());
            }
        }
    }
}
