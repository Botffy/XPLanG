package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.ParseError;
import ppke.itk.xplang.parser.Parser;
import ppke.itk.xplang.parser.Symbol;
import ppke.itk.xplang.parser.Token;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * {@code Function = FUNCTION IDENTIFIER PAREN_OPEN ParameterList PAREN_CLOSE COLON Typename [Declarations] Sequence END_FUNCTION }
 */
class FunctionParser {
    private static final Logger log = LoggerFactory.getLogger("Root.Parser");

    private FunctionParser() { /* empty private ctor */ }

    static void parse(Parser parser) throws ParseError {
        log.debug("Function");

        Function function = register(parseSignature(parser), parser);
        parser.context().openScope();
        try {
            // Fixme the identifier should be localized. somehow.
            VariableDeclaration retVal = new VariableDeclaration(
                function.location(), "Eredmény", function.signature().getReturnType()
            );
            function.parameters().add(0, retVal);

            for (VariableDeclaration parameter : function.parameters()) {
                parser.context().declareVariable(new PlangName(parameter.getName()), parameter);
            }

            if (parser.actual().symbol().equals(Symbol.DECLARE)) {
                DeclarationsParser.parse(parser);
            }
            Sequence sequence = SequenceParser.parse(parser, Symbol.END_FUNCTION);
            parser.accept(Symbol.END_FUNCTION);

            // todo this is a bit iffy. We SUPPOSE the statements below won't throw parseErrors, but it'd be more exact
            //      to close the scope outside the try block.
            Scope scope = parser.context().closeScope();
            Block block = new Block(scope, sequence);

            function.setBlock(block);
        } catch (ParseError e) {
            parser.context().closeScope();
            throw e;
        }
    }

    private static Function register(Function function, Parser parser) throws ParseError {
        Optional<FunctionDeclaration> maybeDeclared = parser.context().lookupFunction(function.signature());
        if (maybeDeclared.isPresent()) {
            FunctionDeclaration declaration = maybeDeclared.get();
            if (!declaration.isDefined() && declaration instanceof Function) {
                return (Function) declaration;
            }
        }

        parser.context().registerFunction(function);
        return function;
    }

    static Function parseSignature(Parser parser) throws ParseError {
        Token functionToken = parser.accept(Symbol.FUNCTION);
        Token functionNameToken = parser.accept(Symbol.IDENTIFIER);

        List<VariableDeclaration> parameters = parseParameterList(parser);

        parser.accept(Symbol.COLON);
        Type type = TypenameParser.parse(parser);

        Signature signature = new Signature(
            new PlangName(functionNameToken.lexeme()),
            type,
            parameters.stream().map(VariableDeclaration::getType).collect(toList())
        );

        return new Function(
            Location.between(functionToken.location(), parser.actual().location()),
            signature,
            parameters,
            null
        );
    }

    static Function parseForwardDeclaration(Parser parser) throws ParseError {
        log.debug("Forward declaration");

        parser.accept(Symbol.FORWARD_DECLARATION);
        Function function = parseSignature(parser);
        parser.context().registerFunction(function);
        return function;
    }

    private static List<VariableDeclaration> parseParameterList(Parser parser) throws ParseError {
        parser.accept(Symbol.PAREN_OPEN);
        Stream<VariableDeclaration> declarations = VariableDeclarationParser.parse(parser);
        while(parser.actual().symbol().equals(Symbol.COMMA)) {
            parser.advance();
            declarations = Stream.concat(declarations, VariableDeclarationParser.parse(parser));
        }
        parser.accept(Symbol.PAREN_CLOSE);
        return declarations.collect(toList());
    }
}
