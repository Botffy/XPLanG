package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.ErrorCode;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * {@code Function = FUNCTION IDENTIFIER PAREN_OPEN ParameterList PAREN_CLOSE COLON Typename [Declarations] Sequence END_FUNCTION }
 */
class FunctionParser {
    private static final Logger log = LoggerFactory.getLogger(FunctionParser.class);

    private FunctionParser() { /* empty private ctor */ }

    static FunctionDeclaration parse(Parser parser) throws ParseError {
        log.debug("Function");

        Token startToken = parser.accept(parser.symbol(PlangSymbol.FUNCTION));
        Token functionNameToken = parser.accept(parser.symbol(PlangSymbol.IDENTIFIER));

        parser.context().openScope();

        try {
            List<VariableDeclaration> parameters = parseParameterList(parser);

            parser.accept(parser.symbol(PlangSymbol.COLON));
            Type type = TypenameParser.parse(parser);

            // Fixme the identifier should be localized. somehow.
            parser.context().declareVariable(new PlangName("Eredmény"), functionNameToken, type);

            if (parser.actual().symbol().equals(parser.symbol(PlangSymbol.DECLARE))) {
                DeclarationsParser.parse(parser);
            }
            Sequence sequence = SequenceParser.parse(parser, parser.symbol(PlangSymbol.END_FUNCTION));
            Token endToken = parser.accept(parser.symbol(PlangSymbol.END_FUNCTION));

            Signature signature = new Signature(
                new PlangName(functionNameToken.lexeme()),
                type,
                parameters.stream().map(VariableDeclaration::getType).collect(toList())
            );

            // todo this is a bit iffy. We SUPPOSE the statements below won't throw parseErrors, but it'd be more exact
            //      to close the scope outside the try block.
            Scope scope = parser.context().closeScope();
            Block block = new Block(scope, sequence);

            return new Function(
                Location.between(startToken.location(), endToken.location()),
                signature,
                parameters,
                block
            );
        } catch (ParseError e) {
            parser.context().closeScope();
            throw e;
        }
    }

    private static List<VariableDeclaration> parseParameterList(Parser parser) throws ParseError {
        parser.accept(parser.symbol(PlangSymbol.PAREN_OPEN));

        // fixme more than one parameter
        Token token = parser.accept(parser.symbol(PlangSymbol.IDENTIFIER));
        parser.accept(parser.symbol(PlangSymbol.COLON), ErrorCode.EXPECTED_COLON_AFTER_VARIABLE);
        Type type = TypenameParser.parse(parser);
        VariableDeclaration param = parser.context().declareVariable(new PlangName(token.lexeme()), token, type);

        parser.accept(parser.symbol(PlangSymbol.PAREN_CLOSE));

        return singletonList(param);
    }
}
