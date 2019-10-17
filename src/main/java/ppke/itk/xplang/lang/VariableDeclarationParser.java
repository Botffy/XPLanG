package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.parser.ErrorCode;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * {@code VariableDeclaration = IDENTIFIER [{COMMA IDENTIFIER}] COLON Typename}
 */
final class VariableDeclarationParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private VariableDeclarationParser() { /* empty private ctor */ }

    static Stream<VariableDeclaration> parse(Parser parser) throws ParseError {
        log.debug("VariableDeclaration");
        List<Token> variables = new ArrayList<>();

        variables.add(parser.accept(parser.symbol(PlangSymbol.IDENTIFIER)));
        while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.COMMA))) {
            parser.advance(); // consume the comma
            variables.add(parser.accept(parser.symbol(PlangSymbol.IDENTIFIER)));
        }
        parser.accept(parser.symbol(PlangSymbol.COLON), ErrorCode.EXPECTED_COLON_AFTER_VARIABLE);
        Type type = TypenameParser.parse(parser);

        return variables.stream()
            .map(token -> new VariableDeclaration(token.location(), token.lexeme(), type));
    }
}
