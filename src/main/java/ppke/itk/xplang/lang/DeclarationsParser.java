package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.VariableDeclaration;
import ppke.itk.xplang.parser.ErrorCode;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;

import java.util.stream.Stream;

/**
 * {@code Declarations = DECLARE [ COLON ] {variableDeclaration} [{COMMA variableDeclaration}}
 */
final class DeclarationsParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private DeclarationsParser() { /* empty private ctor */ }

    static Stream<VariableDeclaration> parse(Parser parser) throws ParseError {
        log.debug("Declarations");
        parser.accept(Symbol.DECLARE, ErrorCode.EXPECTED_DECLARE);

        if (parser.actual().symbol().equals(Symbol.COLON)) {
            parser.advance();
        }

        Stream<VariableDeclaration> declarations = VariableDeclarationParser.parse(parser);
        while(parser.actual().symbol().equals(Symbol.COMMA)) {
            parser.advance();
            declarations = Stream.concat(declarations, VariableDeclarationParser.parse(parser));
        }
        return declarations;
    }
}
