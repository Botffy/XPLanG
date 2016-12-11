package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.BooleanLiteral;
import ppke.itk.xplang.ast.IntegerLiteral;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.parser.*;

import static java.util.Arrays.asList;

/**
 * {@code RValue = IDENTIFIER | LITERAL_INT}
 */
final class RValueParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private RValueParser() { /* empty private ctor */ }

    static RValue parse(Parser parser) throws ParseError {
        log.debug("RValue");
        Symbol act = parser.actual().symbol();
        if(act.equals(PlangSymbol.LITERAL_INT.symbol())) {
            Token token = parser.accept("LITERAL_INT");
            return new IntegerLiteral(Integer.valueOf(token.lexeme()));
        } else if(act.equals(PlangSymbol.LITERAL_BOOL.symbol())) {
            Token token = parser.accept("LITERAL_BOOL");
            // FIXME, but this should be taken care of by operators and expressions
            return new BooleanLiteral(token.lexeme().equalsIgnoreCase("igaz")? true : false);
        } else if(act.equals(PlangSymbol.IDENTIFIER.symbol())) {
            Token namTok = parser.accept("IDENTIFIER");
            return parser.context().getVariableValue(namTok);
        }
        throw new SyntaxError(
            asList(
                PlangSymbol.IDENTIFIER.symbol(),
                PlangSymbol.LITERAL_INT.symbol()
            ), act, parser.actual()
        );
    }
}
