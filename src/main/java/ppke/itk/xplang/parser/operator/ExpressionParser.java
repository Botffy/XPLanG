package ppke.itk.xplang.parser.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;

/**
 * Pratt parser.
 */
public class ExpressionParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Expression");
    private final static Translator translator = Translator.getInstance("parser");

    private final Parser parser;
    private Token op;

    public ExpressionParser(Parser parser) {
        this.parser = parser;
    }

    public void parse(int rightBindingPower) throws ParseError {
        op = parser.actual();
        parser.advance();

        Operator.Prefix nud = parser.context().prefixOf(op.symbol());
        if(nud == null) throw new RuntimeException(String.format("No Nud for %s", op)); //FIXME, a proper syntax error
        else nud.parsePrefix(this);

        while(parser.actual().symbol() != Symbol.EOF && rightBindingPower < calculateLeftBindingPower()) {
            op = parser.actual();
            parser.advance();

            Operator.Infix led = parser.context().infixOf(op.symbol());
            if(led == null) throw new RuntimeException(String.format("No Led for %s", op)); // FIXME, proper syntax error
            else led.parseInfix(this);
        }
    }

    /**
     * An operator is either an infix op, in which case it will tell us its binding power; or it's a prefix op, in
     * which case it does not bind to the left at all.
     */
    private int calculateLeftBindingPower() {
        Operator op = parser.context().infixOf(parser.actual().symbol());
        if(op != null) return op.getPrecedence();
        return Operator.Precedence.CONTAINING;
    }

    public Context context() {
        return parser.context();
    }

    public Token actual() {
        return op;
    }
}
