package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.*;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;
import ppke.itk.xplang.type.Scalar;

import static java.util.Arrays.asList;

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
        if(act.equals(PlangSymbol.LITERAL_INT.symbol())) {
            Token token = parser.accept(PlangSymbol.LITERAL_INT.symbol());
            return new IntegerLiteral(Integer.valueOf(token.lexeme()));
        } else if(act.equals(PlangSymbol.LITERAL_BOOL.symbol())) {
            Token token = parser.accept(PlangSymbol.LITERAL_BOOL.symbol());
            // FIXME, but this should be taken care of by operators and expressions
            return new BooleanLiteral(token.lexeme().equalsIgnoreCase("igaz") ? true : false);
        } else if(act.equals(PlangSymbol.LITERAL_CHAR.symbol())) {
            Token token = parser.accept(PlangSymbol.LITERAL_CHAR.symbol());
            return new CharacterLiteral(token.lexeme().charAt(1));
        } else if(act.equals(PlangSymbol.LITERAL_REAL.symbol())) {
            Token token = parser.accept(PlangSymbol.LITERAL_REAL.symbol());
            return new RealLiteral(Double.valueOf(token.lexeme()));
        } else if(act.equals(PlangSymbol.LITERAL_STRING.symbol())) {
            Token token = parser.accept(PlangSymbol.LITERAL_STRING.symbol());
            return new StringLiteral(token.lexeme().substring(1, token.lexeme().length() - 1));
        } else if(act.equals(PlangSymbol.IDENTIFIER.symbol())) {
            Token token = parser.accept(PlangSymbol.IDENTIFIER.symbol());
            RValue Result = parser.context().getVariableValue(PlangGrammar.name(token.lexeme()), token);

            while(parser.actual().symbol().equals(PlangSymbol.BRACKET_OPEN.symbol())) {
                parser.advance();
                Location location = parser.actual().location();
                RValue address = RValueParser.parse(parser);
                if(!Scalar.INTEGER_TYPE.accepts(address.getType())) {
                    throw new TypeError(
                        translator.translate("plang.array_indextype_mismatch", address.getType()),
                        location
                    );
                }
                parser.accept(PlangSymbol.BRACKET_CLOSE.symbol());
                Result = new ElementVal(Result, address);
            }

            return Result;
        }
        throw new SyntaxError(
            asList(
                PlangSymbol.IDENTIFIER.symbol(),
                PlangSymbol.LITERAL_INT.symbol()
            ), act, parser.actual()
        );
    }
}
