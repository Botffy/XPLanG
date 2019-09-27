package ppke.itk.xplang.lang;

import ppke.itk.xplang.ast.RValue;
import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.parser.*;

class ConditionParser {
    private ConditionParser() { }

    static RValue parse(Parser parser) throws ParseError {
        Expression conditionExpression = parser.parseExpression();
        Location location = conditionExpression.getLocation();
        return TypeChecker.in(parser.context())
            .checking(conditionExpression)
            .expecting(parser.context().booleanType())
            .withCustomErrorMessage(
                node -> new ParseError(location, ErrorCode.TYPE_MISMATCH_CONDITIONAL, node.getType())
            )
            .build()
            .resolve();
    }
}
