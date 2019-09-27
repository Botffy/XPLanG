package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.ast.Assignment;
import ppke.itk.xplang.ast.LValue;
import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.parser.*;

/**
 * {@code Assignment = LValue ASSIGNMENT RValue}
 */
final class AssignmentParser {
    private final static Logger log = LoggerFactory.getLogger("Root.Parser.Grammar");

    private AssignmentParser() { /* empty private ctor */ }

    static Assignment parse(Parser parser) throws ParseError {
        log.debug("Assignment");
        LValue lhs = LValueParser.parse(parser);
        Token token = parser.accept(parser.symbol(PlangSymbol.ASSIGNMENT));
        Expression rhsExpression = parser.parseExpression();
        RValue rhs = TypeChecker.in(parser.context())
            .checking(rhsExpression)
            .expecting(lhs.getType())
            .withCustomErrorMessage(node -> new ParseError(
                token.location(), ErrorCode.TYPE_MISMATCH_ASSIGNMENT, lhs.getType(), node.getType())
            ).build()
            .resolve();

        return new Assignment(
            token.location(),
            lhs,
            rhs
        );
    }
}
