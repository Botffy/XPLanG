package ppke.itk.xplang.parser.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.common.Translator;
import ppke.itk.xplang.parser.*;

import static java.util.Collections.emptyList;

/**
 * Pratt parser for parsing expressions.
 *
 * The expression parser produces a mutable parse tree
 */
public class ExpressionParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Expression");
    private final static Translator translator = Translator.getInstance("parser");

    private final Parser parser;
    private Token op;

    public ExpressionParser(Parser parser) {
        this.parser = parser;
    }

    public Expression parse(int rightBindingPower) throws ParseError {
        op = parser.actual();
        parser.advance();

        Operator.Prefix nud = parser.context().prefixOf(op.symbol());
        Expression left = null;
        if(nud == null) {
            throw new SyntaxError(
                translator.translate("expressionParser.noPrefixOperator.message"), emptyList(), op.symbol(), op
            );
        }
        else left = nud.parsePrefix(this);

        while(parser.actual().symbol() != Symbol.EOF && rightBindingPower < calculateLeftBindingPower()) {
            op = parser.actual();
            parser.advance();

            Operator.Infix led = parser.context().infixOf(op.symbol());
            if(led == null) {
                throw new SyntaxError(
                    translator.translate("expressionParser.noInfixOperator.message"), emptyList(), op.symbol(), op
                );
            }
            else left = led.parseInfix(left, this);
        }

        return left;
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
