package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

/**
 * {@code RValue = IDENTIFIER [{ BRACKET_OPEN RValue BRACKET_CLOSE }] | LITERAL_INT | LITERAL_BOOL}
 */
final class RValueParser {
    private final static Translator translator = Translator.getInstance("Plang");
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private RValueParser() { /* empty private ctor */ }

    static RValue parse(Parser parser) throws ParseError {
        log.debug("RValue");
        Symbol act = parser.actual().symbol();
        if(act.equals(parser.symbol(PlangSymbol.LITERAL_INT))) {
            Token token = parser.advance();
            return new IntegerLiteral(token.location(), Integer.valueOf(token.lexeme()));
        } else if(act.equals(parser.symbol(PlangSymbol.LITERAL_BOOL))) {
            Token token = parser.advance();
            // FIXME, but this should be taken care of by operators and expressions
            return new BooleanLiteral(token.location(), token.lexeme().equalsIgnoreCase("igaz") ? true : false);
        } else if(act.equals(parser.symbol(PlangSymbol.LITERAL_CHAR))) {
            Token token = parser.advance();
            return new CharacterLiteral(token.location(), token.lexeme().charAt(1));
        } else if(act.equals(parser.symbol(PlangSymbol.LITERAL_REAL))) {
            Token token = parser.advance();
            return new RealLiteral(token.location(), Double.valueOf(token.lexeme()));
        } else if(act.equals(parser.symbol(PlangSymbol.LITERAL_STRING))) {
            Token token = parser.advance();
            return new StringLiteral(token.location(), token.lexeme().substring(1, token.lexeme().length() - 1));
        } else if(act.equals(parser.symbol(PlangSymbol.OPERATOR_MINUS))) {
            Token token = parser.advance();
            FunctionSet functionSet = parser.context().lookupFunction(PlangGrammar.name("builtin$minus"));
            functionSet.limitArgumentNumberTo(1);

            RValue arg = RValueParser.parse(parser);
            functionSet.limitArgument(0, singleton(arg.getType()));

            FunctionDeclaration func = null;
            if(functionSet.isResolved()) {
                func = functionSet.getOnlyElement();
                log.debug("Function {} resolved to {}", PlangGrammar.name("builtin$minus"), func.signature());
            } else if(functionSet.isAmbiguous()) {
                throw new FunctionResolutionError(token.location());
            } else if(functionSet.isEmpty()) {
                throw new NameError(token);
            }
            return new FunctionCall(token.location(), func, arg);
        } else if(act.equals(parser.symbol(PlangSymbol.OPERATOR_PIPE))) {
            Token token = parser.advance();
            FunctionSet functionSet = parser.context().lookupFunction(PlangGrammar.name("builtin$length"));
            functionSet.limitArgumentNumberTo(1);

            RValue arg = RValueParser.parse(parser);
            functionSet.limitArgument(0, singleton(arg.getType()));

            FunctionDeclaration func = null;
            if(functionSet.isResolved()) {
                func = functionSet.getOnlyElement();
                log.debug("Function {} resolved to {}", PlangGrammar.name("builtin$length"), func.signature());
            } else if(functionSet.isAmbiguous()) {
                throw new FunctionResolutionError(token.location());
            } else if(functionSet.isEmpty()) {
                throw new NameError(token);
            }

            parser.accept(parser.symbol(PlangSymbol.OPERATOR_PIPE));
            return new FunctionCall(token.location(), func, arg);
        } else if(act.equals(parser.symbol(PlangSymbol.IDENTIFIER))) {
            Token token = parser.advance();
            RValue Result = parser.context().getVariableValue(PlangGrammar.name(token.lexeme()), token);

            while(parser.actual().symbol().equals(parser.symbol(PlangSymbol.BRACKET_OPEN))) {
                Location start = parser.advance().location();
                RValue address = RValueParser.parse(parser);
                Location end = parser.accept(parser.symbol(PlangSymbol.BRACKET_CLOSE)).location();

                Location location = Location.between(start, end);
                if(!Scalar.INTEGER_TYPE.accepts(address.getType())) {
                    throw new TypeError(
                        translator.translate("plang.array_indextype_mismatch", address.getType()),
                        location
                    );
                }
                Result = new ElementVal(location, Result, address);
            }

            return Result;
        }
        throw new SyntaxError(
            // FIXME this list is very much out of date.
            // But it will be taken care of by the introduction of expressions, so won'tfix
            asList(
                parser.symbol(PlangSymbol.IDENTIFIER),
                parser.symbol(PlangSymbol.LITERAL_INT)
            ), act, parser.actual()
        );
    }
}
