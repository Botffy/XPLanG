package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.parser.operator.OldValueOperator;
import ppke.itk.xplang.type.Archetype;
import ppke.itk.xplang.type.Signature;
import ppke.itk.xplang.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ppke.itk.xplang.lang.PlangName.name;

/**
 * {@code Subprogram = FUNCTION IDENTIFIER PAREN_OPEN ParameterList PAREN_CLOSE COLON Typename [Declarations] Sequence END_FUNCTION | PROCEDURE IDENTIFIER PAREN_OPEN ParameterList PAREN_CLOSE [Declarations] Sequence END_PROCEDURE }
 */
class FunctionParser {
    private static final Logger log = LoggerFactory.getLogger("Root.Parser");

    private FunctionParser() { /* empty private ctor */ }

    static void parse(Parser parser) throws ParseError {
        log.debug("Function");

        Function function = register(parseSignature(parser), parser);
        parser.context().openScope();
        try {
            // Fixme the identifier "Eredmény" should be localized.
            Name returnValueName = function.isProcedure() ? SpecialName.NULL_RESULT : name("Eredmény");
            VariableDeclaration retVal = new VariableDeclaration(
                function.location(), returnValueName.toString(), function.signature().getReturnType()
            );
            function.parameters().add(0, retVal);

            for (VariableDeclaration parameter : function.parameters()) {
                parser.context().declareVariable(name(parameter.getName()), parameter);
            }

            if (parser.actual().symbol().equals(Symbol.PRECONDITION)) {
                parsePreconditions(parser, function);
            }
            if (parser.actual().symbol().equals(Symbol.POSTCONDITION)) {
                parsePostcondition(parser, function);
            }
            if (parser.actual().symbol().equals(Symbol.DECLARE)) {
                parseDeclarations(parser, function);
            }

            Symbol endSymbol = function.isProcedure() ? Symbol.END_PROCEDURE : Symbol.END_FUNCTION;
            Sequence sequence = SequenceParser.parse(parser, endSymbol);
            parser.accept(endSymbol);

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

    private static Function parseSignature(Parser parser) throws ParseError {
        Token kindToken = parser.advance();
        Token nameToken = parser.accept(Symbol.IDENTIFIER);

        boolean isFunction = kindToken.symbol() == Symbol.FUNCTION;

        List<VariableDeclaration> parameters = parseParameterList(parser);

        Location endLoc = parser.actual().location();
        Type returnType = Archetype.NONE;
        if (isFunction) {
            parser.accept(Symbol.COLON);
            endLoc = parser.actual().location();
            returnType = TypenameParser.parse(parser);
        }

        Signature signature = new Signature(
            new PlangName(nameToken.lexeme()),
            returnType,
            parameters.stream().map(VariableDeclaration::getType).collect(toList())
        );

        return new Function(
            Location.between(kindToken.location(), endLoc),
            signature,
            parameters
        );
    }

    private static void parseDeclarations(Parser parser, Function function) throws ParseError {
        DeclarationsParser.parse(parser).forEach(variable -> {
            try {
                parser.context().declareVariable(name(variable.getName()), variable);
            } catch (ParseError error) {
                parser.recordError(error.toErrorMessage());
            }
        });
    }

    private static void parsePreconditions(Parser parser, Function function) throws ParseError {
        Token startToken = parser.accept(Symbol.PRECONDITION);
        parser.accept(Symbol.COLON);
        RValue condition = ConditionParser.parse(parser);
        Location location = Location.between(startToken.location(), condition.location());
        Assertion assertion = new Assertion(location, condition);
        function.setPrecondition(assertion);
    }

    private static void parsePostcondition(Parser parser, Function function) throws ParseError {
        Token startToken = parser.accept(Symbol.POSTCONDITION);
        parser.accept(Symbol.COLON);
        parser.context().prefix(Symbol.OPERATOR_OLD, new OldValueOperator(PlangName::new));
        RValue condition = ConditionParser.parse(parser);
        parser.context().removePrefix(Symbol.OPERATOR_OLD);
        Location location = Location.between(startToken.location(), condition.location());
        Assertion assertion = new Assertion(location, condition);
        function.setPostcondition(assertion);
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

        if (parser.actual().symbol() == Symbol.PAREN_CLOSE) {
            parser.advance();
            return new ArrayList<>();
        }

        Stream<VariableDeclaration> declarations = VariableDeclarationParser.parse(parser);
        while(parser.actual().symbol().equals(Symbol.COMMA)) {
            parser.advance();
            declarations = Stream.concat(declarations, VariableDeclarationParser.parse(parser));
        }
        parser.accept(Symbol.PAREN_CLOSE);
        return declarations.collect(toList());
    }
}
