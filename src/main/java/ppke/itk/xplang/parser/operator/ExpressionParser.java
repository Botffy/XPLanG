package ppke.itk.xplang.parser.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.parser.*;

/**
 * Pratt parser for parsing expressions.
 *
 * The expression parser produces a mutable parse tree
 */
public class ExpressionParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Expression");

    private final Parser parser;
    private Token op;

    public ExpressionParser(Parser parser) {
        this.parser = parser;
    }

    public Expression parse() throws ParseError {
        return parse(Operator.Precedence.CONTAINING);
    }

    Expression parse(int rightBindingPower) throws ParseError {
        op = parser.actual();
        parser.advance();

        Operator.Prefix nud = parser.context().prefixOf(op.symbol())
            .orElseThrow(() -> new ParseError(op.location(), ErrorCode.NO_SUCH_PREFIX_OP, op.symbol()));
        Expression left = nud.parsePrefix(this);

        while (parser.actual().symbol() != Symbol.EOF && rightBindingPower < calculateLeftBindingPower()) {
            op = parser.actual();
            parser.advance();

            Operator.Infix led = parser.context().infixOf(op.symbol())
                .orElseThrow(() -> new ParseError(op.location(), ErrorCode.NO_SUCH_INFIX_OP, op.symbol()));

            left = led.parseInfix(left, this);
        }

        return left;
    }

    /**
     * An operator is either an infix op, in which case it will tell us its binding power; or it's a prefix op, in
     * which case it does not bind to the left at all.
     */
    private int calculateLeftBindingPower() {
        return parser.context().infixOf(parser.actual().symbol())
            .map(Operator::getPrecedence)
            .orElse(Operator.Precedence.CONTAINING);
    }

    public Context context() {
        return parser.context();
    }

    public Token actual() {
        return op;
    }

    public Token peek() {
        return parser.actual();
    }

    public Token accept(Symbol symbol) throws ParseError {
        return parser.accept(symbol);
    }
}
